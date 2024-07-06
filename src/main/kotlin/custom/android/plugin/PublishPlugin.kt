package custom.android.plugin

import com.android.build.gradle.LibraryExtension
import custom.android.plugin.BasePublishTask.Companion.MAVEN_PUBLICATION_NAME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.TaskContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import java.net.URI

/**
 * 执行publishToMavenLocal
 * */
open class PublishPlugin : Plugin<Project> {
    companion object {

        private const val TAG = "PublishPlugin"
    }

    private fun supportAppModule(container: PluginContainer): Boolean {
        return container.hasPlugin("com.android.application")
    }

    private fun supportPluginModule(container: PluginContainer): Boolean {
        return container.hasPlugin("org.gradle.kotlin.kotlin-dsl")
                || container.hasPlugin("groovy")
    }

    private fun supportLibraryModule(container: PluginContainer) =
        container.hasPlugin("com.android.library")


    override fun apply(project: Project) {
        // 应用Gradle官方的Maven插件

        val container = project.plugins
        if (supportAppModule(container)) {
            PluginLogUtil.printlnDebugInScreen("$TAG is app")
            return
        }
        container.apply(MavenPublishPlugin::class.java)
        project.extensions.create(
            PublishInfo.EXTENSION_PUBLISH_INFO_NAME, PublishInfo::class.java,
        )
        project.afterEvaluate { currentProject ->
            try {
                val publishInfo = project.extensions.getByType(PublishInfo::class.java)
                val publishing = project.extensions.getByType(PublishingExtension::class.java)
                val gradlePluginDevelopmentExtension =
                    project.extensions.getByType(GradlePluginDevelopmentExtension::class.java)
                val components = currentProject.components
                var softwareComponent: SoftwareComponent?=null
                components.forEach {
                    PluginLogUtil.printlnDebugInScreen("$TAG name: ${it.name}")
                    if (supportPluginModule(container)) {
                        if (it.name == "java") {
                            softwareComponent=it
                            gradlePluginDevelopmentExtension.plugins { namedDomainObjectContainer ->
                                namedDomainObjectContainer.create("gradlePluginCreate") { pluginDeclaration ->
                                    // 插件ID
                                    pluginDeclaration.id = publishInfo.pluginId
                                    // 插件的实现类
                                    pluginDeclaration.implementationClass =
                                        publishInfo.implementationClass
                                }
                            }
                            //注册上传task
                            publishing.publications { publications ->
                                publications.create(
                                    MAVEN_PUBLICATION_NAME,
                                    MavenPublication::class.java
                                ) { publication ->
                                    publication.groupId = publishInfo.groupId
                                    publication.artifactId = publishInfo.artifactId
                                    publication.version = publishInfo.version
                                    if (publication.version.endsWith("-debug")) {
                                        val taskName = "androidSourcesJar"
                                        //获取build.gradle中的android节点
                                        val androidSet =
                                            project.extensions.getByName("android") as LibraryExtension
                                        val sourceSet = androidSet.sourceSets
                                        //获取android节点下的源码目录
                                        val sourceSetFiles =
                                            sourceSet.findByName("main")?.java?.srcDirs
                                        val task =
                                            project.tasks.findByName(taskName)
                                                ?: project.tasks.create(
                                                    taskName,
                                                    Jar::class.java
                                                ) { jar ->
                                                    jar.from(sourceSetFiles)
                                                    jar.archiveClassifier.set("sources")
                                                }
                                        publication.artifact(task)
                                    }
                                    publication.from(it)
                                }
                            }
                            val publishUrl = publishInfo.publishUrl
                            if (publishUrl.isNotEmpty()) {
                                publishing.repositories { artifactRepositories ->
                                    artifactRepositories.maven { mavenArtifactRepository ->
                                        mavenArtifactRepository.url =
                                            URI(publishInfo.publishUrl)
                                        mavenArtifactRepository.credentials { credentials ->
                                            credentials.username = publishInfo.publishUserName
                                            credentials.password =
                                                publishInfo.publishPassword
                                        }
                                    }
                                }
                            } else {
                                PluginLogUtil.printlnErrorInScreen("$TAG publishUrl is null")
                            }
                        }
                    }
                    if (supportLibraryModule(container)) {
                        if (it.name == "release") {
                            //注册上传task
                            publishing.publications { publications ->
                                publications.create(
                                    MAVEN_PUBLICATION_NAME,
                                    MavenPublication::class.java
                                ) { publication ->
                                    publication.groupId = publishInfo.groupId
                                    publication.artifactId = publishInfo.artifactId
                                    publication.version = publishInfo.version
                                    if (publication.version.endsWith("-debug")) {
                                        val taskName = "androidSourcesJar"
                                        //获取build.gradle中的android节点
                                        val androidSet =
                                            project.extensions.getByName("android") as LibraryExtension
                                        val sourceSet = androidSet.sourceSets
                                        //获取android节点下的源码目录
                                        val sourceSetFiles =
                                            sourceSet.findByName("main")?.java?.srcDirs
                                        val task =
                                            project.tasks.findByName(taskName)
                                                ?: project.tasks.create(
                                                    taskName,
                                                    Jar::class.java
                                                ) { jar ->
                                                    jar.from(sourceSetFiles)
                                                    jar.archiveClassifier.set("sources")
                                                }
                                        publication.artifact(task)
                                    }
                                    publication.from(it)
                                }
                            }
                            val publishUrl = publishInfo.publishUrl
                            if (publishUrl.isNotEmpty()) {
                                publishing.repositories { artifactRepositories ->
                                    artifactRepositories.maven { mavenArtifactRepository ->
                                        mavenArtifactRepository.url =
                                            URI(publishInfo.publishUrl)
                                        mavenArtifactRepository.credentials { credentials ->
                                            credentials.username = publishInfo.publishUserName
                                            credentials.password =
                                                publishInfo.publishPassword
                                        }
                                    }
                                }
                            } else {
                                PluginLogUtil.printlnErrorInScreen("$TAG publishUrl is null")
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                PluginLogUtil.printlnErrorInScreen("$TAG PluginModule error ${e.message}")
            }



        }
        val currProjectName = project.displayName
        PluginLogUtil.printlnDebugInScreen("$TAG currProjectName $currProjectName")
        project.gradle.afterProject { currProject ->
            PluginLogUtil.printlnDebugInScreen("$TAG currProject.displayName ${currProject.displayName}")
            if (currProjectName == currProject.displayName) {
                PluginLogUtil.printlnDebugInScreen("$TAG $currProjectName start register ")
                project.tasks.register(
                    PublishLibraryLocalTask.TAG,
                    PublishLibraryLocalTask::class.java
                )
                project.tasks.register(
                    PublishLibraryRemoteTask.TAG,
                    PublishLibraryRemoteTask::class.java
                )
            }
        }
    }

    private fun registerTask(container: TaskContainer, task: BasePublishTask) {
        container.register(
            task.fetchTaskName(),
            task::class.java
        )
    }

}
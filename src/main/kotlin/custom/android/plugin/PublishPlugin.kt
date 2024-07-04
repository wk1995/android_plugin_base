package custom.android.plugin

import custom.android.plugin.BasePublishTask.Companion.MAVEN_PUBLICATION_NAME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.tasks.Jar
import java.net.URI
import com.android.build.gradle.LibraryExtension

/**
 * 执行publishToMavenLocal
 * */
open class PublishPlugin : Plugin<Project> {
    companion object {

        private const val TAG = "PublishPlugin"
    }

    override fun apply(project: Project) {
        // 应用Gradle官方的Maven插件
        project.plugins.apply(MavenPublishPlugin::class.java)
        project.extensions.create(
            PublishInfo.EXTENSION_PUBLISH_INFO_NAME, PublishInfo::class.java,
        )
        project.afterEvaluate { currentProject ->
            try {
                val publishInfo = project.extensions.getByType(PublishInfo::class.java)
                val publishing = project.extensions.getByType(PublishingExtension::class.java)
                val components = currentProject.components
                components.forEach {
                    println("$TAG name: ${it.name}")
                    if (it.name == "release") {
                        publishing.publications { publications ->
                            //注册上传task
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
                                    val sourceSetFiles = sourceSet.findByName("main")?.java?.srcDirs
                                    val task =
                                        project.tasks.findByName(taskName) ?: project.tasks.create(
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

                        publishing.repositories { artifactRepositories ->
                            artifactRepositories.maven { mavenArtifactRepository ->
                                mavenArtifactRepository.url =
                                    URI("https://s01.oss.sonatype.org/content/repositories/releases/")
                                mavenArtifactRepository.credentials { credentials ->
                                    credentials.username = "Entertech"
                                    credentials.password = "bestes-7koTma-bordyn"
                                    //4v0XAb0C:V4CC8CqBqY/9IrnRji+YTkOuzEnyNzeLzGucCC+ZcZwf
                                    /*<server>
                                    <id>${server}</id>
                                    <username>4v0XAb0C</username>
                                    <password>V4CC8CqBqY/9IrnRji+YTkOuzEnyNzeLzGucCC+ZcZwf</password>
                                    </server>*/
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("$TAG error ${e.message}")
            }

        }
        val currProjectName = project.displayName
        project.gradle.afterProject { currProject ->
            if (currProjectName == currProject.displayName) {
                println(" $currProjectName start register ")
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


}
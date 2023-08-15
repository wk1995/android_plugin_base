package custom.android.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import java.net.URI

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
                println("$TAG afterEvaluate")
                val publishing = project.extensions.getByType(PublishingExtension::class.java)
                println("$TAG publishing $publishing")
                val components = currentProject.components
                println("$TAG size ${components.size}")
                components.forEach {
                    println("$TAG name: ${it.name}")
                    if (it.name == "release") {
                        publishing.publications { publications ->
                            println("$TAG create myRelease $publications")
                            //注册上传task
                            publications.create(
                                "myRelease",
                                MavenPublication::class.java
                            ) { publication ->
                                publication.groupId = publishInfo.groupId
                                publication.artifactId = publishInfo.artifactId
                                publication.version = publishInfo.version
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
                project.tasks.register("PublishLocalMaven", PublishLocalTask::class.java)
//                project.tasks.register("publishLocal", PublishLocalTask::class.java)
            }
        }

//        target.getTasks().register()
        /* target.getTasks().register("greeting",PublishTask.class) {
            println("Hello from plugin 'com.lenebf.plugin.hello.greeting'")
            doLast {
                println("√lugin.hello.greeting' doLast ")
            }
            doFirst {
                println("Hello from plugin 'com.lenebf.plugin.hello.greeting' doFirst ")
            }
            setGroup("customPlugin");
            setDescription("this is description");
        }*/
    }


}
package custom.android.plugin

import custom.android.plugin.PluginLogUtil.printlnDebugInScreen
import custom.android.plugin.PluginLogUtil.printlnErrorInScreen
import custom.android.plugin.PluginLogUtil.printlnInfoInScreen
import org.gradle.api.DefaultTask
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * 如果不写成open，会报找不到这个类的错误
 * */
open class PublishLocalTask : DefaultTask() {

    //检验状态是否通过
    private var checkStatus = false

    companion object {
        private const val TAG = "PublishLocalTask"
    }

    init {
        group = "customPlugin"
    }

    @TaskAction
    fun doTask() {
        executeTask()
    }

    private fun executeTask() {
        //1、对publisher配置的信息进行基础校验
        //2、把publisher上传到服务器端，做版本重复性校验
        checkStatus = requestCheckVersion()
        //如果前两步都校验通过了，checkStatus设置为true
        val realTaskName =
            project.projectDir.absolutePath
                .removePrefix(project.rootDir.absolutePath)
                .replace(File.separator, ":") + ":publishToMavenLocal"

        if (checkStatus) {
            val out = ByteArrayOutputStream()
            val path="${project.rootDir}${File.separator}gradlew"
            printlnDebugInScreen("$TAG path: $path realTaskName: $realTaskName")
            //通过命令行的方式进行调用上传maven的task
            project.exec { exec ->
                exec.standardOutput=out
                exec.setCommandLine(
                    path,
                    realTaskName
                )
            }
            val result = out.toString()

            if (result.contains("UP-TO-DATE")) {
                //上传maven仓库成功，上报到服务器
                val isSuccess = requestUploadVersion()
                if (isSuccess) {
                    val publishing = project.extensions.getByType(PublishingExtension::class.java)
                    var groupId = ""
                    var artifactId = ""
                    var version = ""
                    var url=""
                    publishing.publications { publications ->
                        val mavenPublication =
                            publications.getByName("myRelease") as MavenPublication
                        groupId = mavenPublication.groupId
                        artifactId = mavenPublication.artifactId
                        version = mavenPublication.version

                    }
                    publishing.repositories {artifactRepositories->
                        artifactRepositories.maven {
                            //url可能为null，虽然提示不会为null
                            printlnInfoInScreen("${it.name} url: ${it.url?.toString()}")
                        }
                    }
                    publishing.repositories.maven {
                        printlnInfoInScreen("11111   ${it.name} url: ${it.url?.toString()}")
                    }
                    printlnInfoInScreen("mavenLocal  ${publishing.repositories.mavenLocal().url}")
                    printlnInfoInScreen("构建成功")
                    printlnInfoInScreen("仓库地址：  $url")
                    printlnInfoInScreen("===================================================================")
                    printlnInfoInScreen("")
                    printlnInfoInScreen("implementation '$groupId:$artifactId:$version'")
                    printlnInfoInScreen("")
                    printlnInfoInScreen("==================================================================")
                    //提示成功信息
                } else {
                    //提示错误信息
                }
            } else {
                printlnErrorInScreen("===============================下面是执行指令的输出结果===============================")
                printlnErrorInScreen("")
                printlnErrorInScreen(result)
                printlnErrorInScreen("")
                printlnErrorInScreen("==================================================================================")
                throw Exception("上传Maven仓库失败，请检查配置！")
            }
            printlnDebugInScreen("$TAG executeTask finish ")
        }
    }

    /**
     * 上报服务器进行版本检查,这里直接模拟返回成功
     * */
    private fun requestCheckVersion(): Boolean {
        return true
    }

    /**
     * 上报服务器进行版本更新操作,这里直接模拟返回成功
     * */
    private fun requestUploadVersion(): Boolean {
        return true
    }

}
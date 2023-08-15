package custom.android.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.io.File

open class PublishLocalTask : DefaultTask() {

    //是否完成执行任务

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
        println("$TAG doTask")
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
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        publishing.publications { publications ->
            val mavenPublication =
                publications.getByName("myRelease") as MavenPublication
            println("executeTask mavenPublication version ${mavenPublication.version}")
        }
        if (checkStatus) {
            val out = ByteArrayOutputStream()
            val path="${project.rootDir}${File.separator}gradlew"
            println("$TAG path: $path realTaskName: $realTaskName")
            //通过命令行的方式进行调用上传maven的task
            var output:String=""
            val execResult=project.exec { exec ->
                exec.standardOutput=out
                exec.setCommandLine(
                    path,
                    realTaskName
                )
            }
            println("Command output:\n$output  result ${execResult.assertNormalExitValue()}")

            val result = out.toString()
            println("$TAG executeTask result $result")
            if (result.contains("UP-TO-DATE")) {
                //上传maven仓库成功，上报到服务器
                println("$TAG executeTask result 1111  $result")
                val isSuccess = requestUploadVersion()
                println("isSuccess: ${isSuccess}")
                if (isSuccess) {
                    //提示成功信息
                } else {
                    //提示错误信息
                }
                executeFinish()
            } else {
                throw Exception("上传Maven仓库失败，请检查配置！")
            }
            println("$TAG executeTask finish ")
        }
    }

    private fun requestCheckVersion(): Boolean {
        //TODO 上报服务器进行版本检查,这里直接模拟返回成功
        return true
    }

    private fun requestUploadVersion(): Boolean {
        //TODO 上报服务器进行版本更新操作,这里直接模拟返回成功
        return true
    }

    /**
     * 任务执行完毕
     */
    private fun executeFinish() {
    }
}
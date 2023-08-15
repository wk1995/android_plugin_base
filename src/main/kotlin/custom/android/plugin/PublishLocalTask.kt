package custom.android.plugin

import org.gradle.api.tasks.TaskAction


/**
 * 如果不写成open，会报找不到这个类的错误
 * */
open class PublishLocalTask : BasePublishTask() {

    companion object {
        private const val TAG = "PublishLocalTask"
    }

    override fun initPublishCommandLine()=":publishToMavenLocal"

}
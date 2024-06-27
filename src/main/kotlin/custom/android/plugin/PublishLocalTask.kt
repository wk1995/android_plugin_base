package custom.android.plugin

import org.gradle.api.publish.PublishingExtension


/**
 * 如果不写成open，会报找不到这个类的错误
 * */
open class PublishLocalTask : BasePublishTask() {

    companion object {
        private const val TAG = "PublishLocalTask"
    }

    override fun initPublishCommandLine() = ":publishToMavenLocal"

    override fun getPublishingExtensionRepositoriesPath(publishing: PublishingExtension): String {
        return publishing.repositories.mavenLocal().url.toString()
    }
}
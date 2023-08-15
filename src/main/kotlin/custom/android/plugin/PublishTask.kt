package custom.android.plugin

import org.gradle.api.tasks.TaskAction


open class PublishTask : BasePublishTask() {

    companion object {
        private const val TAG = "PublishTask"
    }


    override fun initPublishCommandLine()=":publish${MAVEN_PUBLICATION_NAME}PublicationToMavenRepository"

}
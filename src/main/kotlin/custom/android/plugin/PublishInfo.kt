package custom.android.plugin

open class PublishInfo {

    companion object{
        const val EXTENSION_PUBLISH_INFO_NAME = "PublishInfo"
    }
    /**
     * 包名
     */
    var groupId = ""

    /**
     * 项目名
     */
    var artifactId = ""

    /**
     * 版本号
     */
    var version = ""

}
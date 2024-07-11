package custom.android.plugin.base.dependency

object DependencyItem {
    //appcompat中默认引入了很多库，比如activity库、fragment库、core库、annotation库、drawerLayout库、appcompat-resources等
    const val appcompat = "androidx.appcompat:appcompat:1.6.1"

    /**
     * support兼容库
     * */
    const val supportV4 = "androidx.legacy:legacy-support-v4:1.0.0"

    /**
     * core包+ktx扩展函数
     * */
    const val coreKtx = "androidx.core:core-ktx:1.9.0"

    /**
     * activity+ktx扩展函数
     * */
    const val activityKtx = "androidx.activity:activity-ktx:1.8.0"

    /**
     * fragment+ktx扩展函数
     * */
    const val fragmentKtx = "androidx.fragment:fragment-ktx:1.5.1"

    /**
     * 约束布局
     * */
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.1.4"

    /**
     * 卡片控件
     * */
    const val cardView = "androidx.cardview:cardview:1.0.0"

    /**
     * recyclerView
     * */
    const val recyclerView = "androidx.recyclerview:recyclerview:1.2.1"

    const val material = "com.google.android.material:material:1.12.0"

    /**
     * 分包
     * */
    const val multidex = "androidx.multidex:multidex:2.0.1"
    const val junit_junit = "junit:junit:4.13.2"
    const val ext_junit = "androidx.test.ext:junit:1.1.5"
    const val espresso_espresso_core = "androidx.test.espresso:espresso-core:3.5.1"


    const val affectiveSdkOfflineFlowTime =
        "cn.entertech.android:affective-offline-sdk-flowtime:1.2.3"

    const val affectiveSdkApi = "cn.entertech.android:affective_sdk_api:1.2.3"


    const val entertechBase = "cn.entertech.android:base:latest.release"

    const val room_version = "2.5.0"
    const val database_room_runtime = "androidx.room:room-runtime:$room_version"
    const val database_room_compiler = "androidx.room:room-compiler:$room_version"
    const val database_room_ktx = "androidx.room:room-ktx:$room_version"
    const val database_room_guava = "androidx.room:room-guava:$room_version"
    const val database_room_test = "androidx.room:room-testing:$room_version"
    const val database_room_paging = "androidx.room:room-paging:$room_version"

}


package custom.android.plugin.base.dependency

import custom.android.plugin.base.dependency.DependencyType.DEPENDENCY_TYPE_IMPLEMENTATION
import custom.android.plugin.base.dependency.DependencyType.DEPENDENCY_TYPE_KAPT
import custom.android.plugin.base.dependency.DependencyType.DEPENDENCY_TYPE_TEST_IMPLEMENTATION
import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo


/**
 * @param configurationName see [DependencyType]
 * */
fun DependencyHandler.appcompat(configurationName: String = DEPENDENCY_TYPE_IMPLEMENTATION) {
    add(configurationName, DependencyItem.appcompat)
    add(configurationName, DependencyItem.supportV4)
    add(configurationName, DependencyItem.coreKtx)
    add(configurationName, DependencyItem.activityKtx)
    add(configurationName, DependencyItem.fragmentKtx)
    add(configurationName, DependencyItem.multidex)
}

/**
 * @param configurationName see [DependencyType]
 * */
fun DependencyHandler.affectiveSdk(configurationName: String = DEPENDENCY_TYPE_IMPLEMENTATION) {
    add(configurationName, DependencyItem.affectiveSdkOfflineFlowTime)
    add(configurationName, DependencyItem.affectiveSdkApi)
}

/**
 * 新建module 与app 默认依赖
 * */
fun DependencyHandler.initDependencies() {
    add(DEPENDENCY_TYPE_IMPLEMENTATION, DependencyItem.coreKtx)
    add(DEPENDENCY_TYPE_IMPLEMENTATION, DependencyItem.appcompat)
    add(DEPENDENCY_TYPE_IMPLEMENTATION, DependencyItem.material)
    add(DEPENDENCY_TYPE_IMPLEMENTATION, DependencyItem.constraintlayout)
    add(DEPENDENCY_TYPE_IMPLEMENTATION, DependencyItem.entertechBase)
    add(DependencyType.DEPENDENCY_TYPE_TEST_IMPLEMENTATION, DependencyItem.junit_junit)
    add(DependencyType.DEPENDENCY_TYPE_ANDROID_TEST_IMPLEMENTATION, DependencyItem.ext_junit)
    add(
        DependencyType.DEPENDENCY_TYPE_ANDROID_TEST_IMPLEMENTATION,
        DependencyItem.espresso_espresso_core
    )
}

fun DependencyHandler.databaseRoom(exclude: Map<String, Action<ExternalModuleDependency>> = emptyMap()) {
    listOf(
        DependencyItem.database_room_runtime,
        DependencyItem.database_room_ktx,
        DependencyItem.database_room_guava,
        DependencyItem.database_room_paging,
    ).forEach {
        val action = exclude[it] ?: Action<ExternalModuleDependency> {}
        addDependencyTo(
            this@databaseRoom, DEPENDENCY_TYPE_IMPLEMENTATION, it, action
        )
    }
    add(DEPENDENCY_TYPE_KAPT, DependencyItem.database_room_compiler)
    add(DEPENDENCY_TYPE_TEST_IMPLEMENTATION, DependencyItem.database_room_test)
}


package custom.android.plugin.base

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import custom.android.plugin.base.dependency.DependencyType
import custom.android.plugin.base.dependency.affectiveSdk
import custom.android.plugin.base.dependency.initDependencies
import custom.android.plugin.log.PluginLogUtil
import custom.android.plugin.push.PublishOperate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.PluginContainer
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

open class DefaultGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create(
            "gradleConfig", ProjectConfigExtension::class.java,
        )
        setProjectConfig(target)
    }

    private fun supportAppModule(container: PluginContainer): Boolean {
        return container.hasPlugin("com.android.application")
    }

    private fun supportPluginModule(container: PluginContainer): Boolean {
        return container.hasPlugin("org.gradle.kotlin.kotlin-dsl")
                || container.hasPlugin("groovy")
    }

    private fun supportLibraryModule(container: PluginContainer) =
        container.hasPlugin("com.android.library")

    private fun setProjectConfig(project: Project) {
        if (supportAppModule(project.plugins)) {
            //是app
            setProjectConfigByApp(project)
        } else {
            //是 libraray
            setProjectConfigByLibrary(project)
        }
    }

    private fun setProjectConfigByApp(project: Project) {
        project.apply {
            plugin("kotlin-android")
            plugin("kotlin-kapt")
            plugin("org.jetbrains.kotlin.android")
        }
        val projectConfigInfo = try {
            project.extensions.getByType(ProjectConfigExtension::class.java)
        } catch (e: Exception) {
            PluginLogUtil.printlnErrorInScreen("getByType ProjectConfigInfo error ${e.message}")
            ProjectConfigExtension()
        }
        PluginLogUtil.printlnDebugInScreen("projectConfigInfo: $projectConfigInfo")
        project.extensions.getByType<BaseAppModuleExtension>(BaseAppModuleExtension::class.java).apply {
            compileSdk = projectConfigInfo.compileSdk
            namespace = projectConfigInfo.namespace
            defaultConfig {
                applicationId = projectConfigInfo.applicationId
                minSdk = projectConfigInfo.minSk
                targetSdk = projectConfigInfo.targetSdk
                versionCode = projectConfigInfo.versionCode
                versionName = projectConfigInfo.versionName
                testInstrumentationRunner = projectConfigInfo.testInstrumentationRunner
            }
            compileOptions {
                sourceCompatibility = projectConfigInfo.javeVersion
                targetCompatibility = projectConfigInfo.javeVersion
            }
            //kotlinOptions
            (this as? ExtensionAware)?.extensions?.configure<KotlinJvmOptions>(
                "kotlinOptions"
            ) {
                jvmTarget = projectConfigInfo.getJavaTarget()
            }
            buildTypes {
                release {
                    isDebuggable = false
                    isMinifyEnabled = true
                    isShrinkResources = true
                    isJniDebuggable = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
                debug {
                    isDebuggable = true
                    isMinifyEnabled = false
                    isShrinkResources = false
                    isJniDebuggable = true
                }

            }
        }
        project.dependencies {
            initDependencies()
            affectiveSdk()
        }
    }


    private fun setProjectConfigByLibrary(project: Project) {
        val projectConfigInfo = try {
            project.extensions.getByType(ProjectConfigExtension::class.java)
        } catch (e: Exception) {
            ProjectConfigExtension()
        }
        if (supportLibraryModule(project.plugins)) {
            project.apply {
                plugin("kotlin-android")
                plugin("kotlin-kapt")
                plugin("org.jetbrains.kotlin.android")
            }
            project.extensions.getByType(LibraryExtension::class.java).apply {
                compileSdk = projectConfigInfo.compileSdk
                defaultConfig {
                    minSdk = projectConfigInfo.minSk
                    testInstrumentationRunner = projectConfigInfo.testInstrumentationRunner

                }
                compileOptions {
                    sourceCompatibility = projectConfigInfo.javeVersion
                    targetCompatibility = projectConfigInfo.javeVersion
                }
                //kotlinOptions
                (this as? ExtensionAware)?.extensions?.configure<KotlinJvmOptions>(
                    "kotlinOptions"
                ) {
                    jvmTarget = projectConfigInfo.getJavaTarget()
                }
            }
            project.dependencies {
                initDependencies()
            }
        }

        if (supportPluginModule(project.plugins)) {
            project.dependencies {
                //gradle sdk
                this.add(DependencyType.DEPENDENCY_TYPE_IMPLEMENTATION, gradleApi())
                //groovy sdk
                this.add(DependencyType.DEPENDENCY_TYPE_IMPLEMENTATION, localGroovy())
            }
        }
        projectConfigInfo.publishInfo?.apply {
            PublishOperate.apply(project, this)
        }

    }

}
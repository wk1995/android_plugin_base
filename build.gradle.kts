plugins {
    `kotlin-dsl`
    id("maven-publish")
    id("custom.android.plugin")
}

dependencies {
    //gradle sdk
    implementation(gradleApi())
    //groovy sdk
    implementation(localGroovy())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
    implementation("com.android.tools.build:gradle:8.1.3")
}

gradlePlugin {
    plugins {
        create("pushCustomPlugin") {
            // 插件ID
            id = "android.plugin.publish"
            // 插件的实现类
            implementationClass = "custom.android.plugin.push.PublishPlugin"
        }
    }

}

publishing {
    publications {
        create<MavenPublication>("pushCustomPlugin") {
            groupId = "custom.android.plugin"
            artifactId = "publish"
            version = "0.0.5"
            from(components["java"])
        }
    }
}

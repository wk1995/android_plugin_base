package custom.android.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

public class PublishPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        target.getTasks().register("greeting",PublishTask.class) {
            println("Hello from plugin 'com.lenebf.plugin.hello.greeting'")
            doLast {
                println("√lugin.hello.greeting' doLast ")
            }
            doFirst {
                println("Hello from plugin 'com.lenebf.plugin.hello.greeting' doFirst ")
            }
            setGroup("customPlugin");
            setDescription("this is description");
        }

    }
}
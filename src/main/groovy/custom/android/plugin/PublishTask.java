package custom.android.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskAction;

public class PublishTask extends DefaultTask {


    @TaskAction
    void runTask(){
        System.out.println("run task");
    }

}

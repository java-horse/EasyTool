package easy.api.data;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;

public class ApiDocData {

    private AnActionEvent event;

    private Module module;

    public AnActionEvent getEvent() {
        return event;
    }

    public void setEvent(AnActionEvent event) {
        this.event = event;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}

package easy.action.mybatis.log;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import easy.icons.EasyIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PrettyPrintToggleAction extends ToggleAction {

    public PrettyPrintToggleAction() {
        super("Pretty SQL", "Pretty SQL", EasyIcons.ICON.MAGIC);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        if (Objects.isNull(e.getProject())) {
            return false;
        }
        return PropertiesComponent.getInstance().getBoolean(PrettyPrintToggleAction.class.getName());
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        if (Objects.isNull(e.getProject())) {
            return;
        }
        PropertiesComponent.getInstance().setValue(PrettyPrintToggleAction.class.getName(), state);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

}
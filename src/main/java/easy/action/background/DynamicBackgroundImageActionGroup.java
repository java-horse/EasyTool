package easy.action.background;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import easy.enums.BackgroundImageActionEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DynamicBackgroundImageActionGroup extends DefaultActionGroup {

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[]{
                new BackgroundImageAction(BackgroundImageActionEnum.START),
                new BackgroundImageAction(BackgroundImageActionEnum.RESTART),
                new BackgroundImageAction(BackgroundImageActionEnum.PAUSE),
                new BackgroundImageAction(BackgroundImageActionEnum.CLEAR)
        };
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

}

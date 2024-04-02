package easy.action.api;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import easy.enums.ApiDocEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DynamicApiDocActionGroup extends DefaultActionGroup {

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[]{
                new ApiDocAction(ApiDocEnum.YAPI),
                new ApiDocAction(ApiDocEnum.API_FOX)
        };
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(e.getProject()));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}

package easy.action.search;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import easy.enums.WebSearchEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 动态WebSearch注册Action
 *
 * @project: EasyChar
 * @package: easy.action
 * @author: mabin
 * @date: 2023/10/11 09:25:52
 */
public class DynamicWebSearchActionGroup extends DefaultActionGroup {

    /**
     * 动态注册Action
     *
     * @param e
     * @return com.intellij.openapi.actionSystem.AnAction[]
     * @author mabin
     * @date 2023/10/11 10:09
     */
    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[]{
                new WebSearchAction(WebSearchEnum.BING),
                new WebSearchAction(WebSearchEnum.BAIDU),
                new WebSearchAction(WebSearchEnum.SO_GOU),
                new WebSearchAction(WebSearchEnum.SO),
                new WebSearchAction(WebSearchEnum.GOOGLE),
                new WebSearchAction(WebSearchEnum.YANDEX),
                new WebSearchAction(WebSearchEnum.DUCK_DUCK_GO),
                new WebSearchAction(WebSearchEnum.STACK_OVERFLOW),
        };
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor)
                && editor.getSelectionModel().hasSelection());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

}

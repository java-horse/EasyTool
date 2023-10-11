package easy.action.search;

import com.intellij.icons.AllIcons;
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
    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[]{
                new WebSearchAction(WebSearchEnum.BAIDU.title, WebSearchEnum.BAIDU.remark, AllIcons.Xml.Browsers.Explorer),
                new WebSearchAction(WebSearchEnum.BING.title, WebSearchEnum.BING.remark, AllIcons.Xml.Browsers.Edge),
                new WebSearchAction(WebSearchEnum.GOOGLE.title, WebSearchEnum.GOOGLE.remark, AllIcons.Xml.Browsers.Chrome),
                new WebSearchAction(WebSearchEnum.STACK_OVERFLOW.title, WebSearchEnum.STACK_OVERFLOW.remark, AllIcons.Xml.Browsers.Chrome),
                new WebSearchAction(WebSearchEnum.SO.title, WebSearchEnum.SO.remark, AllIcons.Xml.Browsers.Explorer),
                new WebSearchAction(WebSearchEnum.SO_GOU.title, WebSearchEnum.SO_GOU.remark, AllIcons.Xml.Browsers.Explorer)
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
        return super.getActionUpdateThread();
    }

}

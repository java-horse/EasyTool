package easy.action.search;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import easy.enums.WebSearchEnum;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * WEB搜索action
 *
 * @project: EasyChar
 * @package: easy.action
 * @author: mabin
 * @date: 2023/10/10 13:49:59
 */
public class WebSearchAction extends AnAction {

    private static final Logger log = Logger.getInstance(WebSearchAction.class);

    public WebSearchAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (Objects.isNull(e.getProject())) {
            return;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(editor) || !editor.getSelectionModel().hasSelection()) {
            return;
        }
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (StringUtils.isBlank(selectedText)) {
            return;
        }
        Presentation presentation = e.getPresentation();
        String actionText = presentation.getText();
        String searchUrl = null;
        try {
            selectedText = URLEncoder.encode(selectedText, StandardCharsets.UTF_8.name());
            if (WebSearchEnum.BAIDU.title.equals(actionText)) {
                searchUrl = String.format(WebSearchEnum.BAIDU.templateUrl, selectedText);
            } else if (WebSearchEnum.BING.title.equals(actionText)) {
                searchUrl = String.format(WebSearchEnum.BING.templateUrl, selectedText);
            } else if (WebSearchEnum.GOOGLE.title.equals(actionText)) {
                searchUrl = String.format(WebSearchEnum.GOOGLE.templateUrl, selectedText);
            } else if (WebSearchEnum.STACK_OVERFLOW.title.equals(actionText)) {
                searchUrl = WebSearchEnum.STACK_OVERFLOW.templateUrl + selectedText;
            } else if (WebSearchEnum.SO.title.equals(actionText)) {
                searchUrl = String.format(WebSearchEnum.SO.templateUrl, selectedText);
            } else if (WebSearchEnum.SO_GOU.title.equals(actionText)) {
                searchUrl = String.format(WebSearchEnum.SO_GOU.templateUrl, selectedText);
            }
            if (Objects.isNull(searchUrl)) {
                return;
            }
            Desktop dp = Desktop.getDesktop();
            if (dp.isSupported(Desktop.Action.BROWSE)) {
                dp.browse(URI.create(searchUrl));
            }
        } catch (Exception ex) {
            log.error("打开链接失败: " + searchUrl, ex);
        }
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

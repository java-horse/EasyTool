package easy.action.search;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import easy.enums.WebSearchEnum;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

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

    public WebSearchAction(WebSearchEnum webSearchEnum) {
        super(webSearchEnum.title, webSearchEnum.remark, webSearchEnum.icon);
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
            if (StringUtils.equalsAny(actionText, WebSearchEnum.BAIDU.title, WebSearchEnum.BING.title, WebSearchEnum.GOOGLE.title,
                    WebSearchEnum.SO.title, WebSearchEnum.SO_GOU.title, WebSearchEnum.DUCK_DUCK_GO.title, WebSearchEnum.YANDEX.title)) {
                searchUrl = String.format(WebSearchEnum.BAIDU.templateUrl, selectedText);
            } else if (StringUtils.equals(actionText, WebSearchEnum.STACK_OVERFLOW.title)) {
                searchUrl = WebSearchEnum.STACK_OVERFLOW.templateUrl + selectedText;
            }
            if (StringUtils.isBlank(searchUrl)) {
                return;
            }
            Desktop dp = Desktop.getDesktop();
            if (dp.isSupported(Desktop.Action.BROWSE)) {
                dp.browse(URI.create(searchUrl));
                
            }
        } catch (Exception ex) {
            log.error("Failed to open link: " + searchUrl, ex);
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

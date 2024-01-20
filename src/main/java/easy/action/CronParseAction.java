package easy.action;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import easy.base.Constants;
import easy.util.CronUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Cron表达式解析Action
 *
 * @project: EasyTool
 * @package: easy.action
 * @author: mabin
 * @date: 2024/01/20 14:41:52
 */
public class CronParseAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(project) || Objects.isNull(editor)) {
            return;
        }
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (StringUtils.isBlank(selectedText)) {
            return;
        }
        if (!CronUtil.isCron(selectedText)) {
            HintManager.getInstance().showErrorHint(editor, "Invalid cron expression");
            return;
        }
        List<String> cronList = CronUtil.nextExecutionTime(selectedText, Constants.NUM.TEN);
        if (CollectionUtils.isEmpty(cronList)) {
            HintManager.getInstance().showErrorHint(editor, "Unable to preview time");
        }
        String title = "最近" + Constants.NUM.TEN + "次执行时间";
        HintManager.getInstance().showInformationHint(editor, title + StringUtils.LF + StringUtils.join(cronList, StringUtils.LF));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor)
                && StringUtils.isNotBlank(editor.getSelectionModel().getSelectedText()));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}

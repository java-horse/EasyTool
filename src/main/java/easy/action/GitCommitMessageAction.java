package easy.action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CommitMessageI;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.ui.Refreshable;
import easy.base.Constants;
import easy.form.GitCommitMessageView;
import easy.util.JsonUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Git Commit Message模板的Action
 *
 * @project: EasyTool
 * @package: easy.action
 * @author: mabin
 * @date: 2024/01/24 17:26:05
 */
public class GitCommitMessageAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        CommitMessageI commitMessageI = getCommitPanel(e);
        if (Objects.isNull(commitMessageI)) {
            return;
        }
        GitCommitMessageView gitCommitMessageView = new GitCommitMessageView(project);
        if (gitCommitMessageView.showAndGet()) {
            commitMessageI.setCommitMessage(gitCommitMessageView.getCommitMessage());
        }
        String commitMessageJson = JsonUtil.toJson(gitCommitMessageView.getGitCommitMessageTemplate());
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        propertiesComponent.setValue(Constants.Persistence.GIT_COMMIT_MESSAGE.LAST_COMMIT_MESSAGE, commitMessageJson);
    }

    private static CommitMessageI getCommitPanel(@NotNull AnActionEvent e) {
        Refreshable data = Refreshable.PANEL_KEY.getData(e.getDataContext());
        if (data instanceof CommitMessageI) {
            return (CommitMessageI) data;
        }
        return VcsDataKeys.COMMIT_MESSAGE_CONTROL.getData(e.getDataContext());
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

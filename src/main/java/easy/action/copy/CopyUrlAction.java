package easy.action.copy;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import easy.enums.CopyUrlEnum;
import easy.handler.CopyUrlHandler;
import easy.util.MessageUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.util.Objects;

/**
 * 复制全路径URL
 *
 * @project: EasyChar
 * @package: easy.action
 * @author: mabin
 * @date: 2023/11/07 13:33:14
 */
public class CopyUrlAction extends AnAction {

    public CopyUrlAction(CopyUrlEnum copyUrlEnum) {
        super(copyUrlEnum.title, null, copyUrlEnum.icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (ObjectUtils.anyNull(project, editor, psiFile)) {
            return;
        }
        PsiElement psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
        if (Objects.isNull(psiElement)) {
            return;
        }
        String actionText = e.getPresentation().getText();
        if (StringUtils.isBlank(actionText)) {
            return;
        }
        String copyUrl = StringUtils.EMPTY;
        CopyUrlHandler copyUrlHandler = new CopyUrlHandler();
        if (StringUtils.equals(actionText, CopyUrlEnum.COPY_FULL_URL.title)) {
            copyUrl = copyUrlHandler.doCopyFullUrl(psiElement);
        } else if (StringUtils.equals(actionText, CopyUrlEnum.COPY_HTTP_URL.title)) {
            copyUrl = copyUrlHandler.doCopyHttpUrl(project, psiElement);
        }
        if (StringUtils.isBlank(copyUrl)) {
            return;
        }
        CopyPasteManager.getInstance().setContents(new StringSelection(copyUrl));
        MessageUtil.sendActionDingMessage(e);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabledAndVisible(ObjectUtils.allNotNull(project, editor, psiFile));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}

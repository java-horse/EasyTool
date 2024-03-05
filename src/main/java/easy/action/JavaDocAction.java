package easy.action;

import cn.hutool.core.text.CharPool;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import easy.doc.service.JavaDocGenerateService;
import easy.doc.service.JavaDocWriterService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JavaDocAction extends AnAction {

    private final JavaDocGenerateService JavaDocGenerateService = ApplicationManager.getApplication().getService(JavaDocGenerateService.class);
    private final JavaDocWriterService javaDocWriterService = ApplicationManager.getApplication().getService(JavaDocWriterService.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (ObjectUtils.anyNull(project, editor, psiElement, psiFile)) {
            return;
        }
        if (Objects.isNull(psiElement.getNode())) {
            return;
        }
        String comment = JavaDocGenerateService.generate(psiElement);
        if (StringUtils.isBlank(comment)) {
            return;
        }
        javaDocWriterService.writeJavadoc(project, psiElement, comment, endCount(comment, CharPool.LF));
    }

    /**
     * 统计字符串结尾为某指定字符的数量
     *
     * @param str 字符串
     * @param c   字符
     * @return int
     */
    private int endCount(String str, char c) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        char[] chars = str.toCharArray();
        int count = 0;
        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] == c) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

}

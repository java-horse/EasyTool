package easy.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ThrowableRunnable;
import easy.base.Constants;
import easy.util.MessageUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * serialVersionUID快捷生成
 *
 * @project: EasyChar
 * @package: easy.action
 * @author: mabin
 * @date: 2023/10/14 15:11:50
 */
public class SerialVersionUIDAction extends AnAction {
    private static final Logger log = Logger.getInstance(SerialVersionUIDAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (ObjectUtils.anyNull(project, editor, psiFile)) {
            return;
        }
        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        if (Objects.isNull(psiClass)) {
            return;
        }
        PsiElement psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass caretPsiClass = PsiTreeUtil.getContextOfType(psiElement, PsiClass.class);
        try {
            boolean dingNotify = false;
            if (Objects.nonNull(caretPsiClass)) {
                if (StringUtils.equals(psiClass.getQualifiedName(), caretPsiClass.getQualifiedName())) {
                    dingNotify = genUID(caretPsiClass);
                    PsiClass[] innerClasses = psiClass.getInnerClasses();
                    for (PsiClass innerClass : innerClasses) {
                        dingNotify = dingNotify || genUID(innerClass);
                    }
                } else {
                    if (Arrays.stream(psiClass.getInnerClasses()).anyMatch(innerItem -> StringUtils.equals(innerItem.getQualifiedName(), caretPsiClass.getQualifiedName()))) {
                        dingNotify = genUID(caretPsiClass);
                    }
                }
            }
            if (dingNotify) {
                MessageUtil.sendActionDingMessage(e);
            }
        } catch (Throwable ex) {
            log.error("serialVersionUID write editor exception!", ex);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (ObjectUtils.anyNull(project, editor, psiFile)) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        PsiClass psiClass = PsiTreeUtil.findChildOfAnyType(psiFile, PsiClass.class);
        if (Objects.isNull(psiClass)) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        PsiClassType[] implementsListTypes = psiClass.getImplementsListTypes();
        boolean serializable = Arrays.stream(implementsListTypes)
                .anyMatch(type -> StringUtils.equalsAny(type.getClassName(), "Serializable", "java.io.Serializable"));
        boolean serialResult = Arrays.stream(psiClass.getFields()).noneMatch(field -> StringUtils.equalsAny(field.getName(), Constants.UID));
        PsiClass[] innerClasses = psiClass.getInnerClasses();
        boolean innerSerialResult = false;
        if (innerClasses.length == 0) {
            innerSerialResult = true;
        } else {
            for (PsiClass innerClass : innerClasses) {
                if (Arrays.stream(innerClass.getFields()).noneMatch(field -> StringUtils.equalsAny(field.getName(), Constants.UID))) {
                    innerSerialResult = true;
                    break;
                }
            }
        }
        e.getPresentation().setEnabledAndVisible(serializable && (serialResult || innerSerialResult));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

    /**
     * 生成UID
     *
     * @param psiClass
     * @return boolean
     * @author mabin
     * @date 2023/11/15 15:02
     */
    private boolean genUID(PsiClass psiClass) throws Throwable {
        if (Objects.isNull(psiClass) || Arrays.stream(psiClass.getFields()).anyMatch(field ->
                StringUtils.equalsAny(field.getName(), Constants.UID))) {
            return false;
        }
        WriteCommandAction.writeCommandAction(psiClass.getProject()).run((ThrowableRunnable<Throwable>) () -> {
            String insertStr = "private static final long serialVersionUID = " + UUID.randomUUID().getLeastSignificantBits() + "L;";
            PsiElementFactory elementFactory = PsiElementFactory.getInstance(psiClass.getProject());
            PsiStatement psiStatement = elementFactory.createStatementFromText(insertStr, null);
            PsiElement lBrace = psiClass.getLBrace();
            if (Objects.nonNull(lBrace)) {
                psiClass.addBefore(psiStatement, lBrace.getNextSibling());
            }
        });
        return true;
    }

}

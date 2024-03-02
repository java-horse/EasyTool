package easy.doc.service;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.EditorModificationUtilEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.ThrowableRunnable;
import easy.base.Constants;
import org.apache.commons.lang3.StringUtils;

public class JavaDocWriterService {
    private static final Logger log = Logger.getInstance(JavaDocWriterService.class);

    public void writeJavadoc(Project project, PsiElement psiElement, PsiDocComment comment, int emptyLineNum) {
        try {
            WriteCommandAction.writeCommandAction(project).run(
                    (ThrowableRunnable<Throwable>) () -> {
                        if (psiElement.getContainingFile() == null) {
                            return;
                        }
                        // 写入文档注释
                        if (psiElement instanceof PsiJavaDocumentedElement psiJavaDocumentedElement) {
                            PsiDocComment psiDocComment = psiJavaDocumentedElement.getDocComment();
                            if (psiDocComment == null) {
                                psiElement.getNode().addChild(comment.getNode(), psiElement.getFirstChild().getNode());
                            } else {
                                psiDocComment.replace(comment);
                            }
                        }
                        // 格式化文档注释
                        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(psiElement.getProject());
                        PsiElement javadocElement = psiElement.getFirstChild();
                        int startOffset = javadocElement.getTextOffset();
                        int endOffset = javadocElement.getTextOffset() + javadocElement.getText().length();
                        codeStyleManager.reformatText(psiElement.getContainingFile(), startOffset, endOffset + 1);
                        // 添加空行
                        if (emptyLineNum > Constants.NUM.ZERO) {
                            PsiElement whiteSpaceElement = psiElement.getChildren()[1];
                            if (whiteSpaceElement instanceof PsiWhiteSpaceImpl psiWhiteSpace) {
                                // 修改whiteSpace
                                String space = StringUtils.repeat(StringUtils.LF, emptyLineNum + 1);
                                String exists = StringUtils.stripStart(whiteSpaceElement.getText(), StringUtils.LF);
                                psiWhiteSpace.replaceWithText(space + exists);
                            }
                        }
                    });
        } catch (Throwable throwable) {
            log.error("Automatically generate comment exceptions!", throwable);
        }
    }

    public void write(Project project, Editor editor, String text) {
        if (project == null || editor == null || StringUtils.isBlank(text)) {
            return;
        }
        try {
            WriteCommandAction.writeCommandAction(project).run(
                    (ThrowableRunnable<Throwable>) () -> {
                        int start = editor.getSelectionModel().getSelectionStart();
                        EditorModificationUtilEx.insertStringAtCaret(editor, text);
                        editor.getSelectionModel().setSelection(start, start + text.length());
                    });
        } catch (Throwable throwable) {
            log.error("Automatically generate comment exceptions!", throwable);
        }
    }

}

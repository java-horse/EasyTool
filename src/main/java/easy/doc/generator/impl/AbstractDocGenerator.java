package easy.doc.generator.impl;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import easy.doc.generator.DocGenerator;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class AbstractDocGenerator implements DocGenerator {

    /**
     * 合并DOC
     *
     * @param psiElement psi元素
     * @param targetDoc  目标文件
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/15 14:45
     */
    protected String mergeDoc(PsiJavaDocumentedElement psiElement, String targetDoc) {
        PsiDocComment sDoc = psiElement.getDocComment();
        if (sDoc == null) {
            return targetDoc;
        }
        List<String> docList = Lists.newArrayList();
        PsiElementFactory factory = PsiElementFactory.getInstance(psiElement.getProject());
        PsiDocComment tDoc = factory.createDocCommentFromText(targetDoc);
        PsiDocTag[] sTags = sDoc.getTags();
        for (PsiElement child : tDoc.getChildren()) {
            if (!(child instanceof PsiDocTag docTag)) {
                docList.add(child.getText());
                continue;
            }
            boolean append = true;
            for (PsiDocTag sTag : sTags) {
                if (sTag.getName().equals(docTag.getName())) {
                    docList.add(sTag.getText());
                    append = false;
                }
            }
            if (append) {
                docList.add(docTag.getText());
            }
        }
        return String.join(StringUtils.LF, docList);
    }

}

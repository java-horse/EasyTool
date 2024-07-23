package easy.doc.generator.impl;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import easy.doc.generator.DocGenerator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

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
        if (Objects.isNull(sDoc)) {
            return targetDoc;
        }
        PsiDocComment tDoc = PsiElementFactory.getInstance(psiElement.getProject()).createDocCommentFromText(targetDoc);
        List<String> docList = Lists.newArrayList();
        for (PsiElement child : tDoc.getChildren()) {
            if (!(child instanceof PsiDocTag docTag)) {
                docList.add(child.getText());
                continue;
            }
            boolean append = true;
            for (PsiDocTag sTag : sDoc.getTags()) {
                if (sTag.getName().equals(docTag.getName())) {
                    if (Objects.isNull(sTag.getValueElement()) || Objects.isNull(docTag.getValueElement())) {
                        continue;
                    }
                    PsiElement[] sChildren = sTag.getValueElement().getChildren();
                    PsiElement[] dChildren = docTag.getValueElement().getChildren();
                    if (ArrayUtils.isEmpty(sChildren) || ArrayUtils.isEmpty(dChildren)) {
                        continue;
                    }
                    if (StringUtils.equals(sChildren[0].getText(), dChildren[0].getText())) {
                        docList.add(sTag.getText());
                        append = false;
                    }
                }
            }
            if (append) {
                docList.add(docTag.getText());
            }
        }
        return String.join(StringUtils.LF, docList);
    }

}

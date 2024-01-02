package easy.postfix.postfix;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import easy.postfix.convert.template.GenerateConvertTemplate;
import easy.postfix.getter.template.GenerateAllGetterNoSuperClassTemplate;
import easy.postfix.getter.template.GenerateAllGetterTemplate;
import easy.postfix.setter.template.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GeneratePostfixTemplateProvider implements PostfixTemplateProvider {

    private final Set<PostfixTemplate> templates;

    public GeneratePostfixTemplateProvider() {
        templates = ContainerUtil.newHashSet(
                new GenerateAllGetterTemplate(),
                new GenerateAllGetterNoSuperClassTemplate(),
                new GenerateAllSetterWithDefaultValTemplate(),
                new GenerateAllSetterNoSuperClassTemplate(),
                new GenerateAllSetterWithoutDefaultValTemplate(),
                new GenerateAllSetterByBuilderTemplate(),
                new GenerateAllSetterWithChainTemplate(),
                new GenerateConvertTemplate()
        );
    }

    @NotNull
    @Override
    public Set<PostfixTemplate> getTemplates() {
        return templates;
    }

    @Override
    public boolean isTerminalSymbol(char currentChar) {
        return false;
    }

    @Override
    public void preExpand(@NotNull PsiFile file, @NotNull Editor editor) {

    }

    @Override
    public void afterExpand(@NotNull PsiFile file, @NotNull Editor editor) {

    }

    @Override
    public @NotNull PsiFile preCheck(@NotNull PsiFile copyFile, @NotNull Editor realEditor, int currentOffset) {
        return copyFile;
    }
}
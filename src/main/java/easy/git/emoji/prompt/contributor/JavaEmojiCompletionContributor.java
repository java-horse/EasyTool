package easy.git.emoji.prompt.contributor;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiJavaToken;


/**
 * Java
 *
 * @project EasyTool
 * @package easy.git.emoji.prompt.contributor
 * @author mabin
 * @date 2024/03/09 09:54
 */
public class JavaEmojiCompletionContributor extends EmojiCompletionContributor {
    @Override
    protected PsiElementPattern.Capture<PsiJavaToken> getPlace() {
        return PlatformPatterns.psiElement(PsiJavaToken.class);
    }

}

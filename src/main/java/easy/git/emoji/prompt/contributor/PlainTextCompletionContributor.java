package easy.git.emoji.prompt.contributor;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPlainText;

/**
 * txt纯文本
 *
 * @project EasyTool
 * @package easy.git.emoji.prompt.contributor
 * @author mabin
 * @date 2024/03/08 16:45
 */
public class PlainTextCompletionContributor extends EmojiCompletionContributor {

    @Override
    protected ElementPattern<? extends PsiElement> getPlace() {
        return PlatformPatterns.psiElement(PsiPlainText.class);
    }

}

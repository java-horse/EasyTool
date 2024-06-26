package easy.git.emoji.prompt.contributor;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownCodeFence;
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile;

/**
 * markdown文件
 *
 * @author mabin
 * @project EasyTool
 * @package easy.git.emoji.prompt.contributor
 * @date 2024/03/08 16:45
 */
public class MarkdownEmojiCompletionContributor extends EmojiCompletionContributor {
    @Override
    protected ElementPattern<? extends PsiElement> getPlace() {
        return PlatformPatterns.psiElement()
                .inside(MarkdownFile.class)
                .andNot(PlatformPatterns.psiElement().inside(MarkdownCodeFence.class));
    }

}

package easy.git.emoji.prompt.contributor;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownCodeFenceImpl;
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile;

/**
 * markdown文件
 *
 * @project EasyTool
 * @package easy.git.emoji.prompt.contributor
 * @author mabin
 * @date 2024/03/08 16:45
 */
public class MarkdownEmojiCompletionContributor extends EmojiCompletionContributor {
    @Override
    protected ElementPattern<? extends PsiElement> getPlace() {
        return PlatformPatterns.psiElement()
                .inside(MarkdownFile.class)
                .andNot(PlatformPatterns.psiElement().inside(MarkdownCodeFenceImpl.class));
    }

}

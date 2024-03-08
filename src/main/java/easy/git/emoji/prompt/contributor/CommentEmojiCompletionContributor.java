package easy.git.emoji.prompt.contributor;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;


/**
 * 评论文本
 *
 * @project EasyTool
 * @package easy.git.emoji.prompt.contributor
 * @author mabin
 * @date 2024/03/08 17:55
 */
public class CommentEmojiCompletionContributor extends EmojiCompletionContributor {
    @Override
    protected ElementPattern<? extends PsiElement> getPlace() {
        return PlatformPatterns.psiElement(PsiComment.class);
    }

}

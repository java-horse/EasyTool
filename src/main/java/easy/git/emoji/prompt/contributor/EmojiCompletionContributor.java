package easy.git.emoji.prompt.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import easy.git.emoji.GitEmojiHelper;
import easy.git.emoji.prompt.EmojiCompletionProvider;
import org.jetbrains.annotations.NotNull;

public abstract class EmojiCompletionContributor extends CompletionContributor {

    private final CompletionProvider<CompletionParameters> provider = new EmojiCompletionProvider();
    private final GitEmojiHelper gitEmojiHelper = new GitEmojiHelper();

    protected abstract ElementPattern<? extends PsiElement> getPlace();

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
        if (parameters.getCompletionType() == CompletionType.BASIC && getPlace().accepts(parameters.getPosition())) {
            CompletionResultSet newResult;
            int colonPosition = gitEmojiHelper.findColonPosition(parameters);
            if (colonPosition >= 0) {
                String prefix = parameters.getEditor().getDocument().getText(new TextRange(colonPosition, parameters.getEditor().getCaretModel().getCurrentCaret().getOffset()));
                newResult = result.withPrefixMatcher(prefix);
            } else {
                newResult = result;
            }
            provider.addCompletionVariants(parameters, new ProcessingContext(), newResult);
        }
    }

}
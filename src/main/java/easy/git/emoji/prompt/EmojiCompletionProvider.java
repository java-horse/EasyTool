package easy.git.emoji.prompt;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.util.ProcessingContext;
import easy.config.emoji.GitEmojiConfig;
import easy.config.emoji.GitEmojiConfigComponent;
import easy.git.emoji.GitEmojiData;
import easy.git.emoji.GitEmojiHelper;
import easy.helper.ServiceHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * emoji符号提示处理
 *
 * @author mabin
 * @project EasyTool
 * @package easy.git.emoji
 * @date 2024/03/08 14:55
 */
public class EmojiCompletionProvider extends CompletionProvider<CompletionParameters> {

    private static final Logger log = Logger.getInstance(EmojiCompletionProvider.class);
    private GitEmojiConfig gitEmojiConfig = ServiceHelper.getService(GitEmojiConfigComponent.class).getState();
    private final GitEmojiHelper gitEmojiHelper = new GitEmojiHelper();

    @Override
    public void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        // 如果当前编辑器处于单行模式，则返回，不执行补全逻辑
        if (parameters.getEditor().isOneLineMode()) {
            return;
        }
        // 查找冒号位置，如果没有找到冒号位置，则返回，不执行补全逻辑
        int colonPosition = gitEmojiHelper.findColonPosition(parameters);
        if (colonPosition < 0) {
            return;
        }
        List<GitEmojiData> emojiDataList = gitEmojiHelper.loadEmoji(gitEmojiConfig.getLanguageRealValue());
        if (CollectionUtils.isEmpty(emojiDataList)) {
            return;
        }
        for (GitEmojiData emojiData : emojiDataList) {
            LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(emojiData.getCode() + StringUtils.SPACE + emojiData.getDescription()
                            + StringUtils.SPACE + emojiData.getEmoji())
                    .withIcon(emojiData.getIcon())
                    .withInsertHandler((insertionContext, lookupElement) -> {
                        Document document = insertionContext.getDocument();
                        document.replaceString(colonPosition, insertionContext.getTailOffset(), emojiData.getEmoji());
                    });
            result.addElement(lookupElementBuilder);
        }
    }


}

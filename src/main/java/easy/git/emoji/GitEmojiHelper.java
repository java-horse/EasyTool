package easy.git.emoji;

import cn.hutool.core.text.CharPool;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import easy.base.Constants;
import easy.util.JsonUtil;
import easy.util.LanguageUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * git表情符号资源加载服务
 *
 * @project EasyTool
 * @package easy.git.emoji
 * @author mabin
 * @date 2024/03/08 15:19
 */
public class GitEmojiHelper {

    private static final Logger log = Logger.getInstance(GitEmojiHelper.class);

    private final List<GitEmojiData> GIT_EMOJI_LIST = new ArrayList<>();

    /**
     * 加载本地emoji源信息: 语种相同则不再重新加载
     *
     * @param language 语言
     * @return {@link java.util.List<easy.git.emoji.GitEmojiData> }
     * @author mabin
     * @date 2024/03/08 15:23
     */
    public List<GitEmojiData> loadEmoji(String language) {
        if (CollectionUtils.isNotEmpty(GIT_EMOJI_LIST)) {
            GitEmojiData emojiData = GIT_EMOJI_LIST.get(Constants.NUM.ZERO);
            boolean containsChinese = LanguageUtil.isContainsChinese(emojiData.getDescription());
            if (StringUtils.equals(language, "en") && !containsChinese) {
                return GIT_EMOJI_LIST;
            } else if (StringUtils.equals(language, "zh-CN") && containsChinese) {
                return GIT_EMOJI_LIST;
            }
        }
        try (InputStream inputStream = getClass().getResourceAsStream("/emoji/json/" + language + ".json")) {
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                Gitmojis gitmojisJson = JsonUtil.fromJson(content.toString(), Gitmojis.class);
                if (Objects.isNull(gitmojisJson)) {
                    return GIT_EMOJI_LIST;
                }
                GIT_EMOJI_LIST.clear();
                GitEmojiData emojiData;
                for (Gitmojis.Gitmoji gitmoji : gitmojisJson.getGitmojis()) {
                    emojiData = new GitEmojiData(gitmoji.getCode(), gitmoji.getEmoji(), gitmoji.getDescription());
                    GIT_EMOJI_LIST.add(emojiData);
                }
            }
        } catch (IOException e) {
            log.error("Loading emoji resource exception", e);
        }
        return GIT_EMOJI_LIST;
    }

    /**
     * 查找冒号位置
     *
     * @param parameters 参数
     * @return int
     * @author mabin
     * @date 2024/03/08 15:03
     */
    public int findColonPosition(CompletionParameters parameters) {
        Editor editor = parameters.getEditor();
        CaretModel caretModel = editor.getCaretModel();
        int start = caretModel.getOffset() - 1;
        int end = Math.max(parameters.getPosition().getTextRange().getStartOffset() - 1, 0);
        String text = editor.getDocument().getText();
        for (int index = start; index >= end; index--) {
            char current = text.charAt(index);
            if (Character.isWhitespace(current)) {
                return -1;
            } else if (current == CharPool.COLON) {
                return index;
            } else if (index == end) {
                return -1;
            }
        }
        return -1;
    }

}

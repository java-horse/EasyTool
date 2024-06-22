package easy.handler;

import cn.hutool.core.text.StrPool;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import easy.base.Constants;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.helper.ServiceHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 中英文字符转换处理器
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/02 15:17
 **/

public class ConvertHandler implements TypedActionHandler {
    private static final Logger log = Logger.getInstance(ConvertHandler.class);
    private CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();
    public static Map<String, String> EN_ZH_CHAR_MAP = new HashMap<>(16);
    private final TypedActionHandler orignTypedActionHandler;
    private char lastChar = StrPool.C_SPACE;

    static {
        reload();
    }

    public ConvertHandler(TypedActionHandler orignTypedActionHandler) {
        this.orignTypedActionHandler = orignTypedActionHandler;
    }

    /**
     * 重新加载字符映射关系
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/4/24 15:02
     **/
    public static void reload() {
        EN_ZH_CHAR_MAP.clear();
        String defaultStr = PropertiesComponent.getInstance().getValue(Constants.Persistence.CONVERT.EASY_CHAR_KEY, Constants.DEFAULT_STRING);
        if (StringUtils.isBlank(defaultStr)) {
            return;
        }
        String[] configString = defaultStr.split("\n");
        int length = configString.length / Constants.SPLIT_LENGTH;
        for (int i = 0; i < length; i++) {
            EN_ZH_CHAR_MAP.put(configString[Constants.SPLIT_LENGTH * i].trim(), configString[Constants.SPLIT_LENGTH * i + 1].trim());
        }
    }

    /**
     * 中英文字符自动替换处理: 每次在编辑器中按键都会触发
     *
     * @param editor
     * @param c
     * @param dataContext
     * @return void
     * @author mabin
     * @date 2023/4/24 15:03
     **/
    @Override
    public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
        try {
            if (Boolean.FALSE.equals(commonConfig.getConvertCharEnableCheckBox())) {
                orignTypedActionHandler.execute(editor, c, dataContext);
                return;
            }
            String cStr = String.valueOf(c);
            String enChar = EN_ZH_CHAR_MAP.get(cStr);
            if (lastChar == Constants.PREFIX_CHAR && enChar != null) {
                Document document = editor.getDocument();
                Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
                int caretOffset = primaryCaret.getOffset();
                WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
                    document.deleteString(caretOffset - 1, caretOffset);
                    document.insertString(caretOffset - 1, cStr);
                    primaryCaret.moveToOffset(caretOffset);
                });
            } else if (enChar != null) {
                orignTypedActionHandler.execute(editor, enChar.charAt(0), dataContext);
            } else {
                orignTypedActionHandler.execute(editor, c, dataContext);
            }
            this.lastChar = c;
        } catch (Exception e) {
            log.warn("Automatic replacement of Chinese and English characters exception: " + e.getMessage());
        }
    }

}

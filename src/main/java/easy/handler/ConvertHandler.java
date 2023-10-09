package easy.handler;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import easy.base.Constants;
import easy.form.Statistics;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
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
    private static PropertiesComponent PROPERTIES_COMPONENT = PropertiesComponent.getInstance();
    public static Map<String, String> EN_ZH_CHAR_MAP = new HashMap<>(16);
    private final TypedActionHandler orignTypedActionHandler;
    private char lastChar = ' ';

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
        String[] configString = PropertiesComponent.getInstance()
                .getValue(Constants.EASY_CHAR_KEY, Constants.DEFAULT_STRING)
                .split("\n");
        int length = configString.length / Constants.SPLIT_LENGTH;
        for (int i = 0; i < length; i++) {
            EN_ZH_CHAR_MAP.put(configString[Constants.SPLIT_LENGTH * i].trim(), configString[Constants.SPLIT_LENGTH * i + 1].trim());
        }
    }

    /**
     * 中英文字符自动替换处理: 每次在编辑器中按键都会触发此方法
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
        Document document = editor.getDocument();
        Project project = editor.getProject();
        CaretModel caretModel = editor.getCaretModel();
        Caret primaryCaret = caretModel.getPrimaryCaret();
        int caretOffset = primaryCaret.getOffset();
        String cStr = String.valueOf(c);
        String enChar = EN_ZH_CHAR_MAP.get(cStr);
        if (StringUtils.isBlank(PROPERTIES_COMPONENT.getValue(Constants.TOTAL_CONVERT_COUNT))) {
            PROPERTIES_COMPONENT.setValue(Constants.TOTAL_CONVERT_COUNT, "0");
        }
        if (lastChar == Constants.PREFIX_CHAR && enChar != null) {
            Runnable runnable = () -> {
                document.deleteString(caretOffset - 1, caretOffset);
                document.insertString(caretOffset - 1, cStr);
                primaryCaret.moveToOffset(caretOffset);
            };
            WriteCommandAction.runWriteCommandAction(project, runnable);
            PROPERTIES_COMPONENT.setValue(Constants.TOTAL_CONVERT_COUNT, Long.toString(getTotalConvertCount() + 1));
        } else if (enChar != null) {
            orignTypedActionHandler.execute(editor, enChar.charAt(0), dataContext);
            PROPERTIES_COMPONENT.setValue(Constants.TOTAL_CONVERT_COUNT, Long.toString(getTotalConvertCount() + 1));
        } else {
            orignTypedActionHandler.execute(editor, c, dataContext);
        }
        this.lastChar = c;
        JTextField textField = Statistics.getInstance().getTextField();
        javax.swing.text.Document doc = textField.getDocument();
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, "EasyChar已累计为您自动转换中英文字符 " + getTotalConvertCount() + " 次", null);
            textField.setDocument(doc);
        } catch (Exception e) {
            log.error("EasyChar自动转换中英文字符异常: " + e.getMessage(), e);
        }
    }

    /**
     * 获取总转换次数
     *
     * @param
     * @return java.lang.Long
     * @author mabin
     * @date 2023/5/23 10:54
     **/
    private Long getTotalConvertCount() {
        String value = PROPERTIES_COMPONENT.getValue(Constants.TOTAL_CONVERT_COUNT);
        return StringUtils.isBlank(value) ? 0 : Long.parseLong(value);
    }

}
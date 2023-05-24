package easy.action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import easy.base.Constants;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.util.HashMap;
import java.util.Map;

/**
 * EasyChar中文字符实时替换Action
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/04/24 10:31
 **/

public class EasyCharAction extends AnAction {

    static {
        TypedAction typedAction = TypedAction.getInstance();
        typedAction.setupRawHandler(new EasyCharHandler(typedAction.getRawHandler()));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

}

class EasyCharHandler implements TypedActionHandler {

    private static PropertiesComponent PROPERTIES_COMPONENT = PropertiesComponent.getInstance();
    public static Map<String, String> EN_ZH_CHAR_MAP = new HashMap<>(16);
    private final TypedActionHandler orignTypedActionHandler;
    private char lastChar = ' ';

    static {
        reload();
    }

    public EasyCharHandler(TypedActionHandler orignTypedActionHandler) {
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
     * 中英文字符自动替换处理
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
        String existCount = PROPERTIES_COMPONENT.getValue(Constants.TOTAL_CONVERT_COUNT);
        if (StringUtils.isBlank(existCount)) {
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
        JTextField textField = Constants.STATISTICS.getTextField();
        javax.swing.text.Document doc = textField.getDocument();
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, "EasyChar已累计为您自动转换中英文字符 ", null);
            SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
            StyleConstants.setForeground(simpleAttributeSet, JBColor.GREEN);
            doc.insertString(doc.getLength(), Long.toString(getTotalConvertCount()), simpleAttributeSet);
            doc.insertString(doc.getLength(), " 次", null);
            textField.setDocument(doc);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
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

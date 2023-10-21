package easy.handler;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import easy.base.Constants;
import easy.form.Statistics;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        String defaultStr = PropertiesComponent.getInstance().getValue(Constants.EASY_CHAR_KEY, Constants.DEFAULT_STRING);
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
        String cStr = String.valueOf(c);
        String enChar = EN_ZH_CHAR_MAP.get(cStr);
        if (lastChar == Constants.PREFIX_CHAR && enChar != null) {
            Document document = editor.getDocument();
            Project project = editor.getProject();
            Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
            int caretOffset = primaryCaret.getOffset();
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.deleteString(caretOffset - 1, caretOffset);
                document.insertString(caretOffset - 1, cStr);
                primaryCaret.moveToOffset(caretOffset);
            });
        } else if (enChar != null) {
            orignTypedActionHandler.execute(editor, enChar.charAt(0), dataContext);
            updateStatisticsText();
        } else {
            orignTypedActionHandler.execute(editor, c, dataContext);
        }
        this.lastChar = c;
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
        return PropertiesComponent.getInstance().getLong(Constants.TOTAL_CONVERT_COUNT, 0);
    }

    /**
     * 获取天转换次数
     *
     * @param
     * @param dayKeyName
     * @return java.lang.Long
     * @author mabin
     * @date 2023/10/14 9:51
     */
    private Long getDayConvertCount(String dayKeyName) {
        return PropertiesComponent.getInstance().getLong(dayKeyName, 0);
    }

    /**
     * 更新统计文本
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/10/14 9:55
     */
    private void updateStatisticsText() {
        try {
            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
            propertiesComponent.setValue(Constants.TOTAL_CONVERT_COUNT, Long.toString(getTotalConvertCount() + 1));
            String dayKeyName = Constants.DAY_CONVERT_COUNT + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            propertiesComponent.setValue(dayKeyName, Long.toString(getDayConvertCount(dayKeyName) + 1));

            Statistics statistics = Statistics.getInstance();
            JTextField dayTextField = statistics.getDayTextField();
            javax.swing.text.Document dayDocument = dayTextField.getDocument();
            dayDocument.remove(0, dayDocument.getLength());
            dayDocument.insertString(0, Constants.PLUGIN_NAME + " 今日已转换中英文字符 " + getDayConvertCount(dayKeyName) +
                    " 次 累计 " + getTotalConvertCount() + " 次", null);
            dayTextField.setDocument(dayDocument);
        } catch (Exception e) {
            log.error("更新工具栏统计选项卡文本数据异常", e);
        }
    }

}

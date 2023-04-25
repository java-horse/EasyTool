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
import easy.base.Constants;
import org.jetbrains.annotations.NotNull;

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
        if (lastChar == Constants.PREFIX_CHAR && enChar != null) {
            Runnable runnable = () -> {
                document.deleteString(caretOffset - 1, caretOffset);
                document.insertString(caretOffset - 1, cStr);
                primaryCaret.moveToOffset(caretOffset);
            };
            WriteCommandAction.runWriteCommandAction(project, runnable);
        } else if (enChar != null) {
            orignTypedActionHandler.execute(editor, enChar.charAt(0), dataContext);
        } else {
            orignTypedActionHandler.execute(editor, c, dataContext);
        }
        this.lastChar = c;
    }

}

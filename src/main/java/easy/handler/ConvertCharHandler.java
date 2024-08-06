package easy.handler;

import cn.hutool.core.text.StrPool;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import easy.base.Constants;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.config.convert.ConvertCharConfig;
import easy.config.convert.ConvertCharConfigComponent;
import easy.helper.ServiceHelper;
import org.apache.commons.collections.MapUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * 中英文字符转换处理器
 *
 * @author mabin
 * @project EasyTool
 * @package easy.handler
 * @date 2024/08/03 11:38
 */
public class ConvertCharHandler implements TypedActionHandler {
    private final CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();
    private final ConvertCharConfig convertCharConfig = ServiceHelper.getService(ConvertCharConfigComponent.class).getState();
    private final TypedActionHandler orignTypedActionHandler;
    private char lastChar = StrPool.C_SPACE;

    public ConvertCharHandler(TypedActionHandler orignTypedActionHandler) {
        this.orignTypedActionHandler = orignTypedActionHandler;
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
        if (Objects.isNull(commonConfig) || Objects.isNull(convertCharConfig)
                || Boolean.FALSE.equals(commonConfig.getConvertCharEnableCheckBox())
                || MapUtils.isEmpty(convertCharConfig.getConvertCharMap())) {
            orignTypedActionHandler.execute(editor, c, dataContext);
            return;
        }
        String cStr = String.valueOf(c);
        String enChar = convertCharConfig.getConvertCharMap().get(cStr);
        if (lastChar == '/' && enChar != null) {
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
    }

}

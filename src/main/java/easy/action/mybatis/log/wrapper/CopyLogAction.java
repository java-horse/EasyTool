package easy.action.mybatis.log.wrapper;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

/**
 * 复制SQL日志的Action
 *
 * @project: EasyTool
 * @package: easy.action.mybatis.log.wrapper
 * @author: mabin
 * @date: 2024/01/18 10:13:36
 */
public class CopyLogAction extends AnAction {

    private final Editor editor;

    public CopyLogAction(String text, @NotNull Editor editor) {
        super(text, text, AllIcons.Actions.Copy);
        this.editor = editor;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Document document = editor.getDocument();
        String text = document.getText();
        if (StringUtils.isBlank(text)) {
            return;
        }
        CopyPasteManager.getInstance().setContents(new StringSelection(text));
    }

}

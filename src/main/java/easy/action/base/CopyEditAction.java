package easy.action.base;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import easy.util.BundleUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

/**
 * 通用复制Action
 *
 * @project: EasyTool
 * @package: easy.action.base
 * @author: mabin
 * @date: 2024/01/19 10:43:29
 */
public class CopyEditAction extends AnAction {

    private final Editor editor;

    public CopyEditAction(@NotNull Editor editor) {
        super(BundleUtil.getI18n("global.button.copy.text"), BundleUtil.getI18n("global.button.copy.text"), AllIcons.Actions.Copy);
        this.editor = editor;
    }

    public CopyEditAction(String text, @NotNull Editor editor) {
        super(text, text, AllIcons.Actions.Copy);
        this.editor = editor;
    }

    public CopyEditAction(String text, String desc, @NotNull Editor editor) {
        super(text, desc, AllIcons.Actions.Copy);
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

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(StringUtils.isNotEmpty(editor.getDocument().getText()));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}

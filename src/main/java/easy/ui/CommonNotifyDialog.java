package easy.ui;

import cn.hutool.http.ContentType;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 通用通知对话框
 *
 * @author mabin
 * @project EasyTool
 * @package easy.ui
 * @date 2024/04/25 17:20
 */
public class CommonNotifyDialog extends DialogWrapper {

    private final String content;

    /**
     * 构造函数
     *
     * @param title   标题
     * @param content 内容
     * @author mabin
     * @date 2024/04/25 17:24
     */
    public CommonNotifyDialog(String title, String content) {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle(title);
        this.content = content;
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JTextPane textPane = new JTextPane();
        textPane.setContentType(ContentType.TEXT_HTML.getValue());
        textPane.setText(content);
        return textPane;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{};
    }

}

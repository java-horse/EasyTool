package easy.config;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import easy.base.Constants;
import easy.form.Statistics;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * 侧边栏工具窗口配置
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/05/23 09:02
 **/

public class SidebarConfigFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.getInstance();
        Statistics statistics = Statistics.getInstance();
        Content content = contentFactory.createContent(statistics.getTextField(), StringUtils.EMPTY, false);
        toolWindow.getContentManager().addContent(content);
        String value = PropertiesComponent.getInstance().getValue(Constants.TOTAL_CONVERT_COUNT);
        JTextField textField = statistics.getTextField();
        Document document = textField.getDocument();
        try {
            document.remove(0, document.getLength());
            document.insertString(0, "EasyChar已累计为您自动转换中英文字符 " + (StringUtils.isBlank(value) ? "0" : value) + " 次", null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        textField.setDocument(document);
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setFont(new Font("微软雅黑", Font.BOLD, 18));
        textField.setEditable(false);
    }

}

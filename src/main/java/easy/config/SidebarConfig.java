package easy.config;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import easy.base.Constants;
import easy.form.Statistics;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * 侧边栏工具窗口配置
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/05/23 09:02
 **/

public class SidebarConfig implements ToolWindowFactory {

    private static final Logger log = Logger.getInstance(SidebarConfig.class);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.getInstance();
        ContentManager contentManager = toolWindow.getContentManager();
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        try {
            // 创建转换统计选项卡
            createStatisticsTab(contentFactory, contentManager, propertiesComponent);
        } catch (Exception e) {
            log.error("创建侧边栏选项卡异常", e);
        }
    }

    /**
     * 创建转换统计选项卡
     *
     * @param contentFactory
     * @param contentManager
     * @param propertiesComponent
     * @return void
     * @author mabin
     * @date 2023/10/14 9:21
     */
    private void createStatisticsTab(ContentFactory contentFactory, ContentManager contentManager, PropertiesComponent propertiesComponent) throws Exception {
        // 天转换次数
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.UK);
        dateFormat.setTimeZone(new SimpleTimeZone(8, "GMT"));
        String dayKeyName = Constants.DAY_CONVERT_COUNT + dateFormat.format(new Date());
        String dayCountValue = propertiesComponent.getValue(dayKeyName);
        if (StringUtils.isBlank(dayCountValue)) {
            propertiesComponent.setValue(dayKeyName, "0");
        }
        // 总转换次数
        String totalCountValue = propertiesComponent.getValue(Constants.TOTAL_CONVERT_COUNT);
        if (StringUtils.isBlank(totalCountValue)) {
            propertiesComponent.setValue(Constants.TOTAL_CONVERT_COUNT, "0");
        }

        Statistics statistics = Statistics.getInstance();
        JTextField dayTextField = statistics.getDayTextField();
        Document dayDocument = dayTextField.getDocument();
        dayDocument.remove(0, dayDocument.getLength());
        dayDocument.insertString(0, Constants.PLUGIN_NAME + " 今日已为您自动转换中英文字符 " + propertiesComponent.getValue(dayKeyName) + " 次", null);
        dayTextField.setDocument(dayDocument);
        dayTextField.setHorizontalAlignment(SwingConstants.CENTER);
        dayTextField.setFont(new Font("微软雅黑", Font.BOLD, 18));
        dayTextField.setFocusable(false);
        dayTextField.setBorder(null);

        JTextField totalTextField = statistics.getTotalTextField();
        Document totalDocument = totalTextField.getDocument();
        totalDocument.remove(0, totalDocument.getLength());
        totalDocument.insertString(0, Constants.PLUGIN_NAME + " 累计共为您自动转换中英文字符 " + propertiesComponent.getValue(Constants.TOTAL_CONVERT_COUNT) + " 次", null);
        totalTextField.setDocument(totalDocument);
        totalTextField.setHorizontalAlignment(SwingConstants.CENTER);
        totalTextField.setFont(new Font("微软雅黑", Font.BOLD, 18));
        totalTextField.setFocusable(false);
        totalTextField.setBorder(null);

        contentManager.addContent(contentFactory.createContent(statistics.getComponent(), "Statistics", false));
    }

}

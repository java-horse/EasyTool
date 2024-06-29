package easy.form.widget.core;

import cn.hutool.core.convert.Convert;
import com.cronutils.model.CronType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.Messages;
import easy.base.Constants;
import easy.ui.CommonNotifyDialog;
import easy.util.CronUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * cron对话框视图
 *
 * @author mabin
 * @project EasyTool
 * @package easy.form.widget
 * @date 2024/06/29 13:40
 */
public class CronDialogView extends CoreCommonView {
    private JPanel panel;
    private JTextArea upTextArea;
    private JButton previewButton;
    private JTextArea downTextArea;
    private JComboBox<String> cronTypeComboBox;
    private JSpinner cronPreviewSpinner;
    private JButton exampleButton;

    Map<String, CronType> cronTypeMap = new HashMap<>() {{
        put("SPRING", CronType.SPRING);
        put("CRON4J", CronType.CRON4J);
        put("QUARTZ", CronType.QUARTZ);
        put("UNIX", CronType.UNIX);
        put("SPRING53", CronType.SPRING53);
    }};

    public CronDialogView() {
        previewButton.setIcon(AllIcons.General.ArrowDown);
        previewButton.setEnabled(false);
        areaListener(upTextArea, previewButton);
        previewButton.addActionListener(e -> {
            String cron = upTextArea.getText();
            if (StringUtils.isBlank(cron)) {
                return;
            }
            Integer nextCount = Convert.toInt(cronPreviewSpinner.getModel().getValue());
            if (Objects.isNull(nextCount) || nextCount < 1) {
                return;
            }
            CronType cronType = cronTypeMap.get(String.valueOf(cronTypeComboBox.getSelectedItem()));
            if (Objects.isNull(cronType)) {
                return;
            }
            if (!CronUtil.isCron(cron, cronType)) {
                Messages.showErrorDialog(String.format("【%s】非合法Cron表达式", cron), Constants.PLUGIN_NAME);
                return;
            }
            List<String> cronExceList = CronUtil.nextExecutionTime(cron, nextCount);
            downTextArea.setText(String.format("最近%s次执行时间:%s", nextCount, StringUtils.LF) + String.join(StringUtils.LF, cronExceList));
        });
        cronPreviewSpinner.setModel(new SpinnerNumberModel(Constants.NUM.FIVE, Constants.NUM.ONE, Constants.NUM.TEN, Constants.NUM.ONE));
        exampleButton.setIcon(AllIcons.Ide.LocalScope);
        exampleButton.addActionListener(e -> new CommonNotifyDialog(String.format("%s Cron Expression Example", Constants.PLUGIN_NAME),
                genExampleCron()).show());
    }

    /**
     * 组装常用Cron表达式示例
     *
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/06/29 14:16
     */
    private String genExampleCron() {
        Map<String, String> exampleCronMap = CronUtil.getExampleCronMap();
        if (MapUtils.isEmpty(exampleCronMap)) {
            return StringUtils.EMPTY;
        }
        StringBuilder cronBuilder = new StringBuilder("<ul>");
        for (Map.Entry<String, String> entry : exampleCronMap.entrySet()) {
            cronBuilder.append(String.format("<li>%s ->【%s】</li>", entry.getKey(), entry.getValue()));
        }
        return cronBuilder.append("</ul>").toString();
    }

    public JPanel getContent() {
        return this.panel;
    }

}

package easy.form.widget.core.cron;

import cn.hutool.core.convert.Convert;
import com.cronutils.model.CronType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsSafe;
import easy.base.Constants;
import easy.config.widget.WidgetConfig;
import easy.config.widget.WidgetConfigComponent;
import easy.helper.ServiceHelper;
import easy.ui.CommonNotifyDialog;
import easy.util.BundleUtil;
import easy.util.CronUtil;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.*;


/**
 * cron对话框视图
 *
 * @author mabin
 * @project EasyTool
 * @package easy.form.widget
 * @date 2024/06/29 13:40
 */
public class CronCoreView extends CoreCommonView {
    private JPanel panel;
    private JTextArea upTextArea;
    private JButton previewButton;
    private JTextArea downTextArea;
    private JComboBox<String> cronTypeComboBox;
    private JSpinner cronPreviewSpinner;
    private JButton exampleButton;
    private JButton collecteButton;
    private JButton collectionButton;

    private final WidgetConfig widgetConfig = ServiceHelper.getService(WidgetConfigComponent.class).getState();

    Map<String, CronType> cronTypeMap = new HashMap<>() {{
        put("SPRING", CronType.SPRING);
        put("CRON4J", CronType.CRON4J);
        put("QUARTZ", CronType.QUARTZ);
        put("UNIX", CronType.UNIX);
        put("SPRING53", CronType.SPRING53);
    }};

    public CronCoreView() {
        previewButton.setIcon(AllIcons.General.ArrowDown);
        previewButton.setEnabled(false);
        collecteButton.setIcon(AllIcons.Nodes.Folder);
        collecteButton.setEnabled(false);
        collectionButton.setIcon(AllIcons.Nodes.WebFolder);

        areaListener(upTextArea, previewButton, collecteButton);
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
                MessageUtil.showErrorDialog(String.format("【%s】非合法Cron表达式", cron));
                return;
            }
            List<String> cronExceList = CronUtil.nextExecutionTime(cron, nextCount);
            downTextArea.setText(String.format("最近%s次执行时间:%s", nextCount, StringUtils.LF) + String.join(StringUtils.LF, cronExceList));
        });
        cronPreviewSpinner.setModel(new SpinnerNumberModel(Constants.NUM.FIVE, Constants.NUM.ONE, Constants.NUM.TEN, Constants.NUM.ONE));
        exampleButton.setIcon(AllIcons.Ide.LocalScope);
        exampleButton.addActionListener(e -> new CommonNotifyDialog(String.format("%s Cron Expression Example", Constants.PLUGIN_NAME),
                genExampleCron()).show());
        collecteButton.addActionListener(e -> {
            String cron = StringUtils.trim(upTextArea.getText());
            if (StringUtils.isBlank(cron)) {
                return;
            }
            String inputResult = Messages.showMultilineInputDialog(ProjectManagerEx.getInstance().getDefaultProject(),
                    String.format("确认收藏【%s】Cron表达式? 请在下方输入描述备注", cron),
                    Constants.PLUGIN_NAME, StringUtils.EMPTY, Messages.getQuestionIcon(), new InputValidator() {
                        @Override
                        public boolean checkInput(@NlsSafe String inputString) {
                            return StringUtils.isNotBlank(inputString) && inputString.length() <= Constants.NUM.HUNDRED;
                        }

                        @Override
                        public boolean canClose(@NlsSafe String inputString) {
                            return true;
                        }
                    });
            if (StringUtils.isBlank(inputResult)) {
                MessageUtil.showErrorDialog(String.format("Cron表达式【%s】收藏失败, 描述备注不能为空", cron));
                return;
            }
            if (Objects.nonNull(widgetConfig)) {
                LinkedHashMap<String, String> cronCollectionMap = widgetConfig.getCronCollectionMap();
                if (StringUtils.isNotBlank(cronCollectionMap.get(cron))) {
                    int confirm = MessageUtil.showYesNoDialog(String.format("Cron表达式【%s】已存在, 是否覆盖?", cron));
                    if (confirm == MessageConstants.YES) {
                        cronCollectionMap.put(cron, inputResult);
                        int jump = MessageUtil.showOkCancelDialog(String.format("Cron表达式【%s】保存成功", cron), BundleUtil.getI18n("global.button.jump.text"));
                        if (jump == MessageConstants.OK) {
                            new CronCollectionDialogView().show();
                        }
                    }
                    return;
                }
                cronCollectionMap.put(cron, inputResult);
                int jump = MessageUtil.showOkCancelDialog(String.format("Cron表达式【%s】保存成功", cron), BundleUtil.getI18n("global.button.jump.text"));
                if (jump == MessageConstants.OK) {
                    new CronCollectionDialogView().show();
                }
            }
        });
        collectionButton.addActionListener(e -> new CronCollectionDialogView().show());
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

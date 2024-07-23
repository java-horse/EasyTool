package easy.form.widget.core;

import cn.hutool.core.date.ChineseDate;
import cn.hutool.core.util.NumberUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.ui.JBColor;
import easy.base.Constants;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TimestampCoreView extends CoreCommonView {
    private JPanel panel;
    private JTextArea timestampTextArea;
    private JTextArea datetimeTextArea;
    private JComboBox unitComboBox;
    private JComboBox zoneComboBox;
    private JButton convertButton;
    private JButton restoreButton;
    private JComboBox formatComboBox;
    private JButton timestampButton;
    private JLabel timestampLabel;
    private JButton clearButton;
    private JLabel chineseDateLabel;

    private Timer timer;


    private static final String SECOND_UNIX = "Second";
    private static final String START = "Start";
    private static final String STOP = "Stop";
    private static final String TIMESTAMP_PREFIX = "Timestamp: ";

    public TimestampCoreView() {
        // 初始化定时器
        timestampButton.setText(STOP);
        timestampButton.setIcon(AllIcons.Actions.Suspend);
        timestampButton.addActionListener(e -> {
            if (StringUtils.equals(timestampButton.getText(), START)) {
                timestampButton.setText(STOP);
                timestampButton.setIcon(AllIcons.Actions.Suspend);
                startTimer();
            } else {
                timestampButton.setText(START);
                timestampButton.setIcon(AllIcons.Actions.Execute);
                stopTimer();
            }
        });
        startTimer();

        timestampLabel.setToolTipText("Click auto copy timestamp");
        timestampLabel.setForeground(JBColor.GREEN);
        timestampLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (StringUtils.isNotBlank(timestampLabel.getText())) {
                    String timestamp = StringUtils.replace(timestampLabel.getText(), TIMESTAMP_PREFIX, StringUtils.EMPTY);
                    timestampTextArea.setText(timestamp);
                    CopyPasteManagerEx.getInstanceEx().setContents(new StringSelection(timestamp));
                }
            }
        });
        chineseDateLabel.setForeground(JBColor.GREEN);


        convertButton.setIcon(AllIcons.General.ArrowDown);
        convertButton.setEnabled(false);
        areaListener(timestampTextArea, convertButton);
        convertButton.addActionListener(e -> {
            try {
                String timestampText = StringUtils.trim(timestampTextArea.getText());
                if (StringUtils.isBlank(timestampText)) {
                    return;
                }
                if (MessageUtil.showQuestionDialog("Confirm again convert?") != MessageConstants.OK) {
                    return;
                }
                if (!NumberUtil.isNumber(timestampText)) {
                    MessageUtil.showErrorDialog("Illegal timestamp format");
                    return;
                }
                long timestamp = Long.parseLong(timestampText);
                LocalDateTime localDateTime = LocalDateTime.ofInstant(StringUtils.equals(String.valueOf(unitComboBox.getSelectedItem()), SECOND_UNIX)
                        ? Instant.ofEpochSecond(timestamp) : Instant.ofEpochMilli(timestamp), ZoneId.of(String.valueOf(zoneComboBox.getSelectedItem())));
                String dateTimeFormat = localDateTime.format(DateTimeFormatter.ofPattern(String.valueOf(formatComboBox.getSelectedItem())));
                datetimeTextArea.setText(dateTimeFormat);
                ChineseDate chineseDate = new ChineseDate(localDateTime.toLocalDate());
                chineseDateLabel.setText(String.format("中华黄历: %s（%s）%s", chineseDate, chineseDate.getCyclicalYMD(),
                        StringUtils.isNotBlank(chineseDate.getFestivals()) ? String.format("（%s）", chineseDate.getFestivals()) : StringUtils.EMPTY));
            } catch (Exception ex) {
                MessageUtil.showErrorDialog(String.format("Timestamp convert exception: %s", ex.getMessage()));
            }
        });
        restoreButton.setIcon(AllIcons.General.ArrowUp);
        restoreButton.setEnabled(false);
        areaListener(datetimeTextArea, restoreButton);
        restoreButton.addActionListener(e -> {
            try {
                if (StringUtils.isBlank(datetimeTextArea.getText())) {
                    return;
                }
                if (MessageUtil.showQuestionDialog("Confirm again restore?") != MessageConstants.OK) {
                    return;
                }
                long epochMilli = LocalDateTime.parse(StringUtils.trim(datetimeTextArea.getText()), DateTimeFormatter.ofPattern(String.valueOf(formatComboBox.getSelectedItem())))
                        .atZone(ZoneId.of(String.valueOf(zoneComboBox.getSelectedItem()))).toInstant().toEpochMilli();
                if (String.valueOf(unitComboBox.getSelectedItem()).equals(SECOND_UNIX)) {
                    epochMilli /= Constants.NUM.ONE_THOUSAND;
                }
                timestampTextArea.setText(Long.toString(epochMilli));
            } catch (Exception ex) {
                MessageUtil.showErrorDialog(String.format("Timestamp restore exception: %s", ex.getMessage()));
            }
        });
        clearButton.setIcon(AllIcons.Actions.GC);
        clearButton.addActionListener(e -> {
            if (!StringUtils.isAllBlank(timestampTextArea.getText(), datetimeTextArea.getText(), chineseDateLabel.getText())
                    && MessageUtil.showOkCancelDialog("Confirm Clear Data?") == MessageConstants.OK) {
                timestampTextArea.setText(StringUtils.EMPTY);
                datetimeTextArea.setText(StringUtils.EMPTY);
                chineseDateLabel.setText(StringUtils.EMPTY);
            }
        });
    }

    /**
     * 启动计时器
     *
     * @author mabin
     * @date 2024/07/10 11:40
     */
    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long epochMilli = LocalDateTime.now().atZone(ZoneId.of(String.valueOf(zoneComboBox.getSelectedItem()))).toInstant().toEpochMilli();
                if (String.valueOf(unitComboBox.getSelectedItem()).equals(SECOND_UNIX)) {
                    epochMilli /= 1000;
                }
                timestampLabel.setText(String.format("%s%s", TIMESTAMP_PREFIX, epochMilli));
            }
        }, 100, 1000);
    }

    /**
     * 停止计时器
     *
     * @author mabin
     * @date 2024/07/10 11:40
     */
    private void stopTimer() {
        if (Objects.nonNull(timer)) {
            timer.cancel();
        }
    }

    public JPanel getContent() {
        return this.panel;
    }
}

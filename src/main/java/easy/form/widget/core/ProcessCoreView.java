package easy.form.widget.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.CharsetUtil;
import com.google.gson.JsonObject;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.refactoring.ui.StringTableCellEditor;
import com.intellij.ui.JBColor;
import easy.util.EasyCommonUtil;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.*;

public class ProcessCoreView extends CoreCommonView {
    private JPanel panel;
    private JTable processTable;
    private JTextField portTextField;
    private JButton searchButton;
    private JTextField pidTextField;
    private JButton killButton;
    private JLabel processSummaryLabel;
    private JButton refreshButton;

    private Timer timer;

    private static final String PID = "PID";
    private static final String PROTOCOL = "Protocol";
    private static final String INNER_HOST = "InnerHost";
    private static final String OUTER_HOST = "OuterHost";
    private static final String STATUS = "Status";

    private static final Vector<String> TABLE_NAMES = new Vector<>() {{
        add(PID);
        add(PROTOCOL);
        add(INNER_HOST);
        add(OUTER_HOST);
        add(STATUS);
    }};

    public ProcessCoreView() {
        EasyCommonUtil.customBackgroundText(portTextField, "请输入进程端口");
        EasyCommonUtil.customBackgroundText(pidTextField, "请输入进程PID");
        processSummaryLabel.setForeground(JBColor.GREEN);
        searchButton.setIcon(AllIcons.Actions.Search);
        refreshButton.setIcon(AllIcons.Actions.Refresh);
        killButton.setIcon(AllIcons.Process.Stop);
        searchButton.addActionListener(e -> {
            String portText = portTextField.getText();
            if (StringUtils.isBlank(portText) || StringUtils.equals(portText, "请输入进程端口")) {
                MessageUtil.showInfoMessage("请输入进程端口");
                return;
            }
            List<JsonObject> processList = searchSystemProcess();
            if (CollectionUtils.isEmpty(processList)) {
                MessageUtil.showInfoMessage(String.format("未搜索到【%s】端口进程", portText));
                return;
            }
            List<JsonObject> searchPortList = processList.stream()
                    .filter(item -> {
                        String port;
                        String innerHost = item.get(INNER_HOST).getAsString();
                        if (innerHost.startsWith("[")) {
                            port = innerHost.substring(innerHost.indexOf("]:") + 2);
                        } else {
                            port = innerHost.substring(innerHost.indexOf(":") + 1);
                        }
                        return StringUtils.equals(portText.trim(), port.trim());
                    }).toList();
            if (CollectionUtils.isEmpty(searchPortList)) {
                MessageUtil.showInfoMessage(String.format("未搜索到【%s】端口进程", portText));
                return;
            }
            refreshProcessTable(searchPortList);
            stopTimer();
        });
        refreshButton.addActionListener(e -> {
            refreshProcessTable(searchSystemProcess());
            startTimer();
        });
        killButton.addActionListener(e -> {
            String pidText = pidTextField.getText();
            if (StringUtils.isBlank(pidText) || StringUtils.equals(pidText, "请输入进程PID")) {
                MessageUtil.showInfoMessage("请输入进程PID");
                return;
            }
            if (MessageUtil.showOkCancelDialog(String.format("确定要终止【%s】进程吗？", pidText)) == MessageConstants.OK) {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("taskkill", "-PID", pidText.trim(), "-F");
                try {
                    processBuilder.start();
                    MessageUtil.showInfoMessage(String.format("进程【%s】终止成功", pidText));
                    refreshProcessTable(searchSystemProcess());
                } catch (Exception ignore) {
                }
            }
        });
        processTable.setCellEditor(new StringTableCellEditor(ProjectManagerEx.getInstance().getDefaultProject()) {
            @Override
            public boolean isCellEditable(EventObject e) {
                return false;
            }
        });
        refreshProcessTable(searchSystemProcess());
        processTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = processTable.getSelectedRow();
                if (selectedRow != -1) {
                    String pid = Convert.toStr(processTable.getValueAt(selectedRow, 0));
                    pidTextField.setText(pid);
                    String port;
                    String innerHost = Convert.toStr(processTable.getValueAt(selectedRow, 2));
                    if (innerHost.startsWith("[")) {
                        port = innerHost.substring(innerHost.indexOf("]:") + 2);
                    } else {
                        port = innerHost.substring(innerHost.indexOf(":") + 1);
                    }
                    portTextField.setText(port);

                    int selectedColumn = processTable.getSelectedColumn();
                    CopyPasteManager.getInstance().setContents(new StringSelection((String) processTable.getValueAt(selectedRow, selectedColumn)));
                }
            }
        });
        // 启动定时器
        startTimer();
    }

    /**
     * 刷新进程表
     *
     * @author mabin
     * @date 2024/07/22 11:20
     */
    private void refreshProcessTable(@NotNull List<JsonObject> processList) {
        Vector<Vector<String>> rowVector = new Vector<>(processList.size());
        for (JsonObject processObject : processList) {
            Vector<String> row = new Vector<>(8);
            row.add(processObject.get(PID).getAsString());
            row.add(processObject.get(PROTOCOL).getAsString());
            row.add(processObject.get(INNER_HOST).getAsString());
            row.add(processObject.get(OUTER_HOST).getAsString());
            row.add(processObject.get(STATUS).getAsString());
            rowVector.add(row);
        }
        DefaultTableModel cronTableModel = new DefaultTableModel(rowVector, TABLE_NAMES);
        processTable.setModel(cronTableModel);
        processTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        processTable.setPreferredScrollableViewportSize(new Dimension(-1, processTable.getRowHeight() * processTable.getRowCount()));
        processTable.setFillsViewportHeight(true);
        processSummaryLabel.setText(String.format("Summary: 进程【%s】刷新, 共【%s】条", DateUtil.now(), processList.size()));
    }


    /**
     * 搜索系统进程
     *
     * @return {@link java.util.List<com.google.gson.JsonObject>}
     * @author mabin
     * @date 2024/07/22 13:34
     */
    private List<JsonObject> searchSystemProcess() {
        List<JsonObject> processList = new ArrayList<>();
        if (!EasyCommonUtil.isWindows()) {
            return processList;
        }
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("netstat", "-ano");
        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), CharsetUtil.GBK))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    List<String> readerprocessList = Arrays.stream(line.split(StringUtils.SPACE))
                            .filter(item -> !StringUtils.equals(item, StringUtils.EMPTY))
                            .toList();
                    if (CollectionUtils.isEmpty(readerprocessList) || readerprocessList.size() != 5) {
                        continue;
                    }
                    JsonObject processObject = new JsonObject();
                    processObject.addProperty(PROTOCOL, readerprocessList.get(0));
                    processObject.addProperty(INNER_HOST, readerprocessList.get(1));
                    processObject.addProperty(OUTER_HOST, readerprocessList.get(2));
                    processObject.addProperty(STATUS, readerprocessList.get(3));
                    processObject.addProperty(PID, readerprocessList.get(4));
                    processList.add(processObject);
                }
            } catch (Exception ignore) {
            }
        } catch (IOException ignore) {
        }
        return processList.stream().skip(1).toList();
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
                refreshProcessTable(searchSystemProcess());
            }
        }, 5000, 10000);
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

    public JComponent getContent() {
        return panel;
    }

}
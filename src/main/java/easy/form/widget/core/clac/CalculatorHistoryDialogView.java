package easy.form.widget.core.clac;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import easy.base.Constants;
import easy.config.widget.WidgetConfig;
import easy.config.widget.WidgetConfigComponent;
import easy.helper.ServiceHelper;
import easy.util.BundleUtil;
import easy.util.EasyCommonUtil;
import easy.util.MessageUtil;
import org.apache.commons.collections.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class CalculatorHistoryDialogView extends DialogWrapper {

    private JTable table;

    private final WidgetConfig widgetConfig = ServiceHelper.getService(WidgetConfigComponent.class).getState();

    private static final Vector<String> TABLE_NAMES = new Vector<>() {{
        add("算式");
        add("结果");
        add("时间");
    }};

    public CalculatorHistoryDialogView() {
        super(ProjectManagerEx.getInstanceEx().getDefaultProject());
        setTitle("Calculator History View");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        table = new JBTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        refreshHistoryTable();
        EasyCommonUtil.addTableCellCopyListener(table);
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(table);
        toolbarDecorator.setRemoveAction(anActionButton -> {
            String calc = table.getValueAt(table.getSelectedRow(), 0).toString();
            String date = table.getValueAt(table.getSelectedRow(), 2).toString();
            int confirmRemove = MessageUtil.showYesNoDialog(String.format("确认移除【%s（%s）】历史记录?", calc, date));
            if (MessageConstants.YES == confirmRemove) {
                widgetConfig.getCalculatorHistoryMap().remove(date + StrUtil.UNDERLINE + calc);
                refreshHistoryTable();
            }
        });
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
        defaultActionGroup.addSeparator();
        defaultActionGroup.addAction(new AnAction(BundleUtil.getI18n("global.button.export.text"),
                BundleUtil.getI18n("global.button.export.text"), AllIcons.ToolbarDecorator.Export) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (Objects.isNull(widgetConfig) || MapUtils.isEmpty(widgetConfig.getCalculatorHistoryMap())) {
                    MessageUtil.showWarningDialog("计算历史记录为空");
                    return;
                }
                // 创建文件保存弹窗
                FileSaverDescriptor fsd = new FileSaverDescriptor(String.format("%s Widget Calculator Tool", Constants.PLUGIN_NAME),
                        "Select a location to save the calculator history", "csv");
                FileSaverDialog saveFileDialog = FileChooserFactory.getInstance().createSaveFileDialog(fsd, ProjectManagerEx.getInstance().getDefaultProject());
                VirtualFileWrapper virtualFileWrapper = saveFileDialog.save(Constants.PLUGIN_NAME + StrPool.UNDERLINE + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN) + StrPool.DOT + "csv");
                if (Objects.isNull(virtualFileWrapper)) {
                    return;
                }
                // 组装并导出csv文件
                CsvWriter csvWriter = CsvUtil.getWriter(virtualFileWrapper.getFile(), CharsetUtil.CHARSET_UTF_8);
                csvWriter.writeHeaderLine(TABLE_NAMES.toArray(new String[]{}));
                for (Map.Entry<String, String> entry : widgetConfig.getCalculatorHistoryMap().entrySet()) {
                    List<String> keyList= StrUtil.split(entry.getKey(), StrUtil.UNDERLINE);
                    csvWriter.writeLine(keyList.get(1), entry.getValue(), keyList.get(0));
                }
                csvWriter.close();
                MessageUtil.showInfoMessage(BundleUtil.getI18n("global.message.handle.success"));
            }
        });
        defaultActionGroup.addAction(new AnAction(BundleUtil.getI18n("global.button.clear.text"),
                BundleUtil.getI18n("global.button.clear.text"), AllIcons.Actions.GC) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (Objects.isNull(widgetConfig) || MapUtils.isEmpty(widgetConfig.getCalculatorHistoryMap())) {
                    MessageUtil.showWarningDialog("计算历史记录为空");
                    return;
                }
                if (MessageUtil.showOkCancelDialog("确认清空历史计算记录?") == MessageConstants.OK) {
                    for (String key : new ArrayList<>(widgetConfig.getCalculatorHistoryMap().keySet())) {
                        widgetConfig.getCalculatorHistoryMap().remove(key);
                    }
                    refreshHistoryTable();
                    MessageUtil.showInfoMessage(BundleUtil.getI18n("global.message.handle.success"));
                }
            }
        });
        toolbarDecorator.setActionGroup(defaultActionGroup);

        JScrollPane scrollPane = new JBScrollPane(toolbarDecorator.createPanel());
        panel.add(scrollPane);
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 700), Math.max(size.height, 400));
        return panel;
    }

    /**
     * 刷新历史记录表
     *
     * @author mabin
     * @date 2024/07/14 17:58
     */
    private void refreshHistoryTable() {
        if (Objects.isNull(widgetConfig)) {
            return;
        }
        Vector<Vector<String>> rowVector = new Vector<>(widgetConfig.getCalculatorHistoryMap().size());
        for (Map.Entry<String, String> entry : widgetConfig.getCalculatorHistoryMap().entrySet()) {
            List<String> keyList= StrUtil.split(entry.getKey(), StrUtil.UNDERLINE);
            Vector<String> row = new Vector<>(5);
            row.add(keyList.get(1));
            row.add(entry.getValue());
            row.add(keyList.get(0));
            rowVector.add(row);
        }
        DefaultTableModel cronTableModel = new DefaultTableModel(rowVector, TABLE_NAMES);
        table.setModel(cronTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredScrollableViewportSize(new Dimension(-1, table.getRowHeight() * table.getRowCount()));
        table.setFillsViewportHeight(true);
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{};
    }

}

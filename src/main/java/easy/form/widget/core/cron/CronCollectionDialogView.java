package easy.form.widget.core.cron;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
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

public class CronCollectionDialogView extends DialogWrapper {
    private JTable cronTable;

    private final WidgetConfig widgetConfig = ServiceHelper.getService(WidgetConfigComponent.class).getState();

    private static final Vector<String> CRON_TABLE_NAMES = new Vector<>() {{
        add("Cron表达式");
        add("执行描述");
    }};

    public CronCollectionDialogView() {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle("Cron Collection View");
        init();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        cronTable = new JBTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cronTable.setAutoCreateRowSorter(true);
        refreshCronTable();
        EasyCommonUtil.addTableCellCopyListener(cronTable);
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(cronTable);
        toolbarDecorator.setAddAction(button -> {
            if (new CronAddDialogView(widgetConfig).showAndGet()) {
                refreshCronTable();
            }
        });
        toolbarDecorator.setRemoveAction(anActionButton -> {
            String cron = cronTable.getValueAt(cronTable.getSelectedRow(), 0).toString();
            int confirmRemove = MessageUtil.showYesNoDialog(String.format("确认移除【%s】Cron表达式?", cron));
            if (MessageConstants.YES == confirmRemove) {
                widgetConfig.getCronCollectionMap().remove(cron);
                refreshCronTable();
            }
        });
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
        defaultActionGroup.addSeparator();
        defaultActionGroup.addAction(new AnAction(() -> BundleUtil.getI18n("global.button.export.text"), AllIcons.ToolbarDecorator.Export) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (Objects.isNull(widgetConfig) || MapUtils.isEmpty(widgetConfig.getCronCollectionMap())) {
                    MessageUtil.showWarningDialog("Cron收藏夹为空");
                    return;
                }
                // 创建文件保存弹窗
                FileSaverDescriptor fsd = new FileSaverDescriptor(String.format("%s Widget Cron Tool", Constants.PLUGIN_NAME),
                        "Select a location to save the cron expression", "csv");
                FileSaverDialog saveFileDialog = FileChooserFactory.getInstance().createSaveFileDialog(fsd, ProjectManagerEx.getInstance().getDefaultProject());
                VirtualFileWrapper virtualFileWrapper = saveFileDialog.save(Constants.PLUGIN_NAME + StrPool.UNDERLINE + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN) + StrPool.DOT + "csv");
                if (Objects.isNull(virtualFileWrapper)) {
                    return;
                }
                // 组装并导出csv文件
                CsvWriter csvWriter = CsvUtil.getWriter(virtualFileWrapper.getFile(), CharsetUtil.CHARSET_UTF_8);
                csvWriter.writeHeaderLine(CRON_TABLE_NAMES.toArray(new String[]{}));
                for (Map.Entry<String, String> entry : widgetConfig.getCronCollectionMap().entrySet()) {
                    csvWriter.writeLine(entry.getKey(), entry.getValue());
                }
                csvWriter.close();
                MessageUtil.showInfoMessage(BundleUtil.getI18n("global.message.handle.success"));
            }
        });
        defaultActionGroup.addAction(new AnAction(() -> BundleUtil.getI18n("global.button.clear.text"), AllIcons.Actions.GC) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (Objects.isNull(widgetConfig) || MapUtils.isEmpty(widgetConfig.getCronCollectionMap())) {
                    MessageUtil.showWarningDialog("Cron收藏夹为空");
                    return;
                }
                if (MessageUtil.showOkCancelDialog("确认清空Cron收藏夹记录?") == MessageConstants.OK) {
                    for (String key : new ArrayList<>(widgetConfig.getCronCollectionMap().keySet())) {
                        widgetConfig.getCronCollectionMap().remove(key);
                    }
                    refreshCronTable();
                    MessageUtil.showInfoMessage(BundleUtil.getI18n("global.message.handle.success"));
                }
            }
        });
        toolbarDecorator.setActionGroup(defaultActionGroup);
        JScrollPane cronScrollPane = new JBScrollPane(toolbarDecorator.createPanel());
        panel.add(cronScrollPane);
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 700), Math.max(size.height, 400));
        return panel;
    }

    /**
     * 刷新cron表格数据
     *
     * @author mabin
     * @date 2024/07/03 10:10
     */
    private void refreshCronTable() {
        if (Objects.isNull(widgetConfig)) {
            return;
        }
        LinkedHashMap<String, String> cronCollectionMap = widgetConfig.getCronCollectionMap();
        Vector<Vector<String>> rowVector = new Vector<>(cronCollectionMap.size());
        for (Map.Entry<String, String> entry : cronCollectionMap.entrySet()) {
            Vector<String> row = new Vector<>(4);
            row.add(entry.getKey());
            row.add(entry.getValue());
            rowVector.add(row);
        }
        DefaultTableModel cronTableModel = new DefaultTableModel(rowVector, CRON_TABLE_NAMES);
        cronTable.setModel(cronTableModel);
        cronTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cronTable.setPreferredScrollableViewportSize(new Dimension(-1, cronTable.getRowHeight() * cronTable.getRowCount()));
        cronTable.setFillsViewportHeight(true);
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{};
    }


}

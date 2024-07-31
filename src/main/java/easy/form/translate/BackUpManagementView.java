package easy.form.translate;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.db.handler.BeanListHandler;
import cn.hutool.db.handler.NumberHandler;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.refactoring.ui.StringTableCellEditor;
import com.intellij.ui.JBColor;
import easy.base.Constants;
import easy.base.SqliteConstants;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.helper.ServiceHelper;
import easy.helper.SqliteHelper;
import easy.util.EasyCommonUtil;
import easy.util.EventBusUtil;
import easy.util.MessageUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.*;

public class BackUpManagementView extends DialogWrapper {
    private final TranslateConfig translateConfig = ServiceHelper.getService(TranslateConfigComponent.class).getState();

    private JPanel panel;
    private JTextField searchTextField;
    private JButton searchButton;
    private JButton refreshButton;
    private JTable table;
    private JButton previousButton;
    private JButton nextButton;
    private JLabel pageSummaryLabel;
    private JComboBox<Integer> pageSizeComboBox;
    private JButton editButton;
    private JButton removeButton;
    private JButton exportButton;

    private Integer currentPage = Constants.NUM.ONE;
    private Long totalPage;
    private Long totalCount;

    private static final Vector<String> TABLE_NAMES = new Vector<>() {{
        add("源数据");
        add("目标数据");
        add("翻译引擎");
        add("创建时间");
        add("更新时间");
        add("IDE");
    }};

    public BackUpManagementView() {
        super(ProjectManagerEx.getInstanceEx().getDefaultProject());
        setTitle("翻译备份管理");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        pageSummaryLabel.setForeground(JBColor.GREEN);
        EasyCommonUtil.customBackgroundText(searchTextField, "请输入源数据");
        searchButton.setIcon(AllIcons.Actions.Search);
        searchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String searchText = searchTextField.getText();
                if (StringUtils.isBlank(searchText) || StringUtils.equals(searchText, "请输入源数据")) {
                    MessageUtil.showInfoMessage("请输入搜索条件");
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // 左键: 模糊
                    searchText = " WHERE source LIKE '%" + searchText + "%'";
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    // 右键: 精确
                    searchText = " WHERE source = '" + searchText + "'";
                }
                // 刷新表格
                refreshTable(searchText);
            }
        });
        refreshButton.setIcon(AllIcons.Actions.Refresh);
        refreshButton.addActionListener(e -> {
            searchTextField.setText(null);
            currentPage = Constants.NUM.ONE;
            pageSizeComboBox.setSelectedItem(Constants.NUM.TWENTY);
            refreshTable(null);
        });
        editButton.setIcon(AllIcons.Actions.Edit);
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String source = Convert.toStr(table.getValueAt(row, Constants.NUM.ZERO));
                String target = Convert.toStr(table.getValueAt(row, Constants.NUM.ONE));
                String channel = Convert.toStr(table.getValueAt(row, Constants.NUM.TWO));
                BackUpEditView backUpEditView = new BackUpEditView(source, target, channel);
                if (backUpEditView.showAndGet()) {
                    EventBusUtil.TranslateBackUpEvent translateBackUp = backUpEditView.getTranslateBackUp();
                    if (Objects.isNull(translateBackUp)) {
                        MessageUtil.showErrorDialog("翻译备份更新数据不存在");
                        return;
                    }
                    translateBackUp.setModifiedTime(DateUtil.now());
                    changeBackUp(translateBackUp);
                    MessageUtil.showInfoMessage("更新成功");
                    refreshTable(getSearchSql());
                }
            } else {
                BackUpEditView backUpEditView = new BackUpEditView(null, null, null);
                if (backUpEditView.showAndGet()) {
                    EventBusUtil.TranslateBackUpEvent translateBackUp = backUpEditView.getTranslateBackUp();
                    if (Objects.isNull(translateBackUp)) {
                        MessageUtil.showErrorDialog("翻译备份新增数据不存在");
                        return;
                    }
                    changeBackUp(translateBackUp);
                    MessageUtil.showInfoMessage("新增成功");
                    refreshTable(getSearchSql());
                }
            }
        });
        removeButton.setIcon(AllIcons.Actions.GC);
        removeButton.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            if (ArrayUtils.isEmpty(selectedRows)) {
                MessageUtil.showInfoMessage("请选择要删除的行");
                return;
            }
            if (MessageConstants.OK == MessageUtil.showInfoMessage(String.format("确认删除选中【%s】行数据?", table.getSelectedRowCount()))) {
                for (int row : selectedRows) {
                    String source = Convert.toStr(table.getValueAt(row, Constants.NUM.ZERO));
                    String channel = Convert.toStr(table.getValueAt(row, Constants.NUM.TWO));
                    removeBackUp(source, channel);
                }
                refreshTable(getSearchSql());
            }
        });
        exportButton.setIcon(AllIcons.Actions.Download);
        exportButton.addActionListener(e -> {
            SqliteHelper sqliteHelper = new SqliteHelper(translateConfig.getBackupFilePath());
            if (Objects.isNull(totalCount) || totalCount == 0) {
                MessageUtil.showInfoMessage("暂无备份数据!");
                return;
            }
            if (MessageConstants.OK == MessageUtil.showInfoMessage(String.format("确认导出【%s】条翻译备份数据", totalCount))) {
                FileSaverDescriptor fsd = new FileSaverDescriptor(String.format("%s translate backup export", Constants.PLUGIN_NAME),
                        "Select a location to save the backup file", "csv");
                FileSaverDialog saveFileDialog = FileChooserFactory.getInstance().createSaveFileDialog(fsd, ProjectManagerEx.getInstance().getDefaultProject());
                VirtualFileWrapper virtualFileWrapper = saveFileDialog.save(Constants.PLUGIN_NAME + StrPool.UNDERLINE + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN));
                if (Objects.isNull(virtualFileWrapper)) {
                    return;
                }
                // 分页查询并组装csv数据(size=100)
                File file = virtualFileWrapper.getFile();
                CsvWriter csvWriter = CsvUtil.getWriter(file, CharsetUtil.CHARSET_UTF_8);
                csvWriter.writeHeaderLine(TABLE_NAMES.toArray(String[]::new));
                long page = BigDecimal.valueOf(totalCount).divide(BigDecimal.valueOf(Constants.NUM.HUNDRED), 0, RoundingMode.CEILING).longValue();
                String search = StringUtils.isBlank(getSearchSql()) ? StringUtils.EMPTY : getSearchSql();
                for (int i = 1; i <= page; i++) {
                    String pageSql = String.format(SqliteConstants.SQL.QUERY_TABLE, SqliteConstants.TABLE.BACKUP) + search + " order by create_time desc";
                    List<EventBusUtil.TranslateBackUpEvent> backUpDataList = sqliteHelper.page(pageSql, i, Constants.NUM.HUNDRED, BeanListHandler.create(EventBusUtil.TranslateBackUpEvent.class));
                    if (CollectionUtils.isNotEmpty(backUpDataList)) {
                        for (EventBusUtil.TranslateBackUpEvent backUp : backUpDataList) {
                            csvWriter.writeLine(backUp.getSource(), backUp.getTarget(), backUp.getChannel(), backUp.getCreateTime(), backUp.getModifiedTime(), backUp.getIde());
                        }
                    }
                }
                csvWriter.close();
                if (MessageConstants.OK == MessageUtil.showOkCancelDialog(String.format("成功导出【%s】条数据", totalCount), "打开文件夹", "取消")) {
                    try {
                        Desktop.getDesktop().open(file.getParentFile());
                    } catch (Exception ignore) {
                    }
                }
            }
        });
        previousButton.setIcon(AllIcons.Actions.PreviousOccurence);
        previousButton.addActionListener(e -> {
            if (currentPage <= Constants.NUM.ONE) {
                MessageUtil.showInfoMessage("当前已是第一页");
                return;
            }
            currentPage--;
            refreshTable(getSearchSql());
        });
        nextButton.setIcon(AllIcons.Actions.NextOccurence);
        nextButton.addActionListener(e -> {
            if (currentPage + 1 > totalPage) {
                MessageUtil.showInfoMessage("当前已是最后一页");
                return;
            }
            currentPage++;
            refreshTable(getSearchSql());
        });
        pageSizeComboBox.setSelectedItem(Constants.NUM.TWENTY);
        pageSizeComboBox.addActionListener(e -> {
            currentPage = Constants.NUM.ONE;
            refreshTable(getSearchSql());
        });
        EasyCommonUtil.addTableCellCopyListener(table);
        refreshTable(null);
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 950), Math.max(size.height, 560));
        return panel;
    }

    /**
     * 获取搜索SQL
     *
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/07/30 16:02
     */
    @Nullable
    private String getSearchSql() {
        return StringUtils.isBlank(searchTextField.getText()) || StringUtils.equals(searchTextField.getText(), "请输入源数据")
                ? null : " WHERE source LIKE '%" + searchTextField.getText() + "%'";
    }

    /**
     * 刷新表格数据
     *
     * @param searchSql 搜索SQL
     * @author mabin
     * @date 2024/07/30 14:47
     */
    private void refreshTable(@Nullable String searchSql) {
        SqliteHelper sqliteHelper = new SqliteHelper(translateConfig.getBackupFilePath());
        String search = StringUtils.isBlank(searchSql) ? StringUtils.EMPTY : searchSql;
        String countSql = String.format(SqliteConstants.SQL.QUERY_COUNT, SqliteConstants.TABLE.BACKUP) + search;
        Number countNumber = sqliteHelper.query(countSql, NumberHandler.create());
        String pageSql = String.format(SqliteConstants.SQL.QUERY_TABLE, SqliteConstants.TABLE.BACKUP) + search + " order by create_time desc";
        List<EventBusUtil.TranslateBackUpEvent> backUpEventList = sqliteHelper.page(pageSql, currentPage, Convert.toInt(pageSizeComboBox.getSelectedItem()),
                BeanListHandler.create(EventBusUtil.TranslateBackUpEvent.class));
        Vector<Vector<String>> rowVector = new Vector<>(backUpEventList.size());
        for (EventBusUtil.TranslateBackUpEvent backUp : backUpEventList) {
            Vector<String> row = new Vector<>(10);
            row.add(backUp.getSource());
            row.add(backUp.getTarget());
            row.add(backUp.getChannel());
            row.add(backUp.getCreateTime());
            row.add(backUp.getModifiedTime());
            row.add(backUp.getIde());
            rowVector.add(row);
        }
        table.setModel(new DefaultTableModel(rowVector, TABLE_NAMES));
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setCellEditor(new StringTableCellEditor(ProjectManagerEx.getInstance().getDefaultProject()) {
            @Override
            public boolean isCellEditable(EventObject e) {
                return false;
            }
        });
        table.setPreferredScrollableViewportSize(new Dimension(-1, table.getRowHeight() * table.getRowCount()));
        table.setFillsViewportHeight(true);
        totalCount = countNumber.longValue();
        totalPage = BigDecimal.valueOf(totalCount).divide(BigDecimal.valueOf(Convert.toLong(pageSizeComboBox.getSelectedItem())),
                0, RoundingMode.CEILING).longValue();
        pageSummaryLabel.setText(String.format("第%s页 共%s页 总%s条", currentPage, totalPage, totalCount));
    }

    /**
     * 更改备份
     *
     * @param translateBackUp 向上翻译
     * @author mabin
     * @date 2024/07/31 09:44
     */
    private void changeBackUp(EventBusUtil.TranslateBackUpEvent translateBackUp) {
        if (Objects.isNull(translateBackUp)) {
            return;
        }
        SqliteHelper sqliteHelper = new SqliteHelper(translateConfig.getBackupFilePath());
        if (Objects.nonNull(translateBackUp.getModifiedTime())) {
            String updateSql = String.format("""
                            UPDATE %s SET source = '%s', target = '%s', channel = '%s', modified_time = '%s' WHERE source = '%s' and channel = '%s'
                            """, SqliteConstants.TABLE.BACKUP, translateBackUp.getSource(), translateBackUp.getTarget(), translateBackUp.getChannel(),
                    translateBackUp.getModifiedTime(), translateBackUp.getSource(), translateBackUp.getChannel());
            sqliteHelper.update(updateSql);
            return;
        }
        String insertSql = String.format("""
                INSERT INTO %s(source, target, channel, create_time, modified_time, ide) VALUES ('%s', '%s', '%s',
                DATETIME(CURRENT_TIMESTAMP, 'localtime'), DATETIME(CURRENT_TIMESTAMP, 'localtime'), '%s');
                """, SqliteConstants.TABLE.BACKUP, translateBackUp.getSource(), translateBackUp.getTarget(), translateBackUp.getChannel(), ApplicationInfo.getInstance().getFullApplicationName());
        sqliteHelper.update(insertSql);
    }

    /**
     * 删除备份
     *
     * @param source  来源
     * @param channel 频道
     * @author mabin
     * @date 2024/07/31 09:56
     */
    private void removeBackUp(String source, String channel) {
        if (StringUtils.isAnyBlank(source, channel)) {
            return;
        }
        SqliteHelper sqliteHelper = new SqliteHelper(translateConfig.getBackupFilePath());
        String removeSql = String.format("DELETE FROM %s WHERE source = '%s' AND channel = '%s';", SqliteConstants.TABLE.BACKUP, source, channel);
        sqliteHelper.update(removeSql);
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{};
    }

}

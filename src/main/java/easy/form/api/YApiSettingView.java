package easy.form.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.OnOffButton;
import com.intellij.ui.table.JBTable;
import easy.api.model.yapi.YApiTableDTO;
import easy.api.sdk.yapi.model.ApiProject;
import easy.config.api.YApiConfig;
import easy.config.api.YApiConfigComponent;
import easy.enums.ApiDocTypeEnum;
import easy.handler.ServiceHelper;
import easy.icons.EasyIcons;
import easy.util.NotifyUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class YApiSettingView {
    private static final Logger log = Logger.getInstance(YApiSettingView.class);
    private YApiConfig yApiConfig = ServiceHelper.getService(YApiConfigComponent.class).getState();
    private static final Vector<String> COLUMN_NAMES = new Vector<>(CollUtil.newArrayList(StrUtil.SPACE, "项目ID", "项目名称", "项目Token"));

    private JPanel rootPanel;
    private JPanel centerPanel;
    private JPanel toolPanel;
    private JPanel basePanel;
    private JPanel mainPanel;
    private JLabel yapiIconLabel;
    private OnOffButton yapiEnableButton;
    private JLabel yapiServerUrlLabel;
    private JTextField yapiServerUrlTextField;
    private JTable yapiTable;
    private DefaultTableModel yapiTableModel;

    public YApiSettingView() {
        yapiIconLabel.setIcon(EasyIcons.ICON.YAPI);
        yapiEnableButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    NotifyUtil.notify(String.format("请注意: %s文档同步服务将不可用", ApiDocTypeEnum.YAPI.getTitle()), NotificationType.WARNING);
                }
            }
        });
    }

    private void createUIComponents() {
        yApiConfig = ServiceHelper.getService(YApiConfigComponent.class).getState();
        initProjectTable();
    }

    /**
     * 初始化YApi项目Token信息
     *
     * @author mabin
     * @date 2024/05/09 16:24
     */
    private void initProjectTable() {
        yapiTable = new JBTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        // 设置表格监听器
        setTableListener();
        // 设置表格刷新逻辑
        refreshYapiTable();
        // 设置带有工具栏的表格
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(yapiTable);
        toolbarDecorator.setAddAction(button -> {
            if (StringUtils.isBlank(yapiServerUrlTextField.getText()) || !Boolean.TRUE.equals(yapiEnableButton.isSelected())) {
                NotifyUtil.notify(String.format("请先配置%s服务器地址并开启", ApiDocTypeEnum.YAPI.getTitle()), NotificationType.ERROR);
                return;
            }
            YApiTokenAddDialog apiTokenAddDialog = new YApiTokenAddDialog(yapiServerUrlTextField.getText());
            if (apiTokenAddDialog.showAndGet()) {
                // 请求YApi获取项目信息
                ApiProject apiProject = apiTokenAddDialog.getApiProject();
                if (Objects.isNull(apiProject)) {
                    NotifyUtil.notify(String.format("获取%s项目信息异常, 请确认Token及服务器地址是否正确", ApiDocTypeEnum.YAPI.getTitle()), NotificationType.ERROR);
                    return;
                }
                String projectId = Integer.toString(apiProject.getId());
                yApiConfig.getYapiTableMap().put(projectId, new YApiTableDTO(projectId, apiProject.getName(), apiProject.getToken()));
                refreshYapiTable();
            }
        });
        toolbarDecorator.setRemoveAction(button -> {
            yApiConfig.getYapiTableMap().remove(yapiTable.getValueAt(yapiTable.getSelectedRow(), 1).toString());
            refreshYapiTable();
        });
        mainPanel = toolbarDecorator.createPanel();
    }

    /**
     * 设置表格监听器
     *
     * @author mabin
     * @date 2024/05/10 13:39
     */
    private void setTableListener() {
        // 设置表格第一列复选框鼠标点击监听器
        yapiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = yapiTable.rowAtPoint(e.getPoint());
                if (yapiTable.columnAtPoint(e.getPoint()) == 0) {
                    Map<String, YApiTableDTO> yapiTableMap = yApiConfig.getYapiTableMap();
                    if (!Convert.toBool(yapiTableModel.getValueAt(row, 0))) {
                        for (int i = 0; i < yapiTable.getRowCount(); i++) {
                            if (i != row) {
                                yapiTableModel.setValueAt(Boolean.FALSE, i, 0);
                                String key = Convert.toStr(yapiTableModel.getValueAt(i, 1));
                                YApiTableDTO yApiTableDTO = yapiTableMap.get(key);
                                yApiTableDTO.setSelect(Boolean.FALSE);
                                yapiTableMap.put(key, yApiTableDTO);
                            }
                        }
                        yapiTableModel.setValueAt(Boolean.TRUE, row, 0);
                        String key = Convert.toStr(yapiTableModel.getValueAt(row, 1));
                        YApiTableDTO yApiTableDTO = yapiTableMap.get(key);
                        yApiTableDTO.setSelect(Boolean.TRUE);
                        yapiTableMap.put(key, yApiTableDTO);
                    }
                }
            }
        });
        // 设置表格非首列单元格监听器(点击自动复制文本)
        yapiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = yapiTable.getSelectedRow();
                int selectedColumn = yapiTable.getSelectedColumn();
                if (selectedColumn != 0 && selectedColumn != -1 && selectedRow != -1) {
                    CopyPasteManager.getInstance().setContents(new StringSelection((String) yapiTable.getValueAt(selectedRow, selectedColumn)));
                }
            }
        });
    }


    /**
     * 自定义复选框渲染器
     *
     * @author mabin
     * @project EasyTool
     * @package easy.form.api.YApiSettingView
     * @date 2024/05/10 13:34
     */
    static class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer() {
            super();
            setHorizontalAlignment(JCheckBox.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            isSelected();
            setSelected((Boolean) value);
            return this;
        }
    }


    /**
     * 自定义复选框编辑器
     *
     * @author mabin
     * @project EasyTool
     * @package easy.form.api.YApiSettingView
     * @date 2024/05/10 13:34
     */
    static class CheckBoxEditor extends AbstractCellEditor implements TableCellEditor {
        private final JCheckBox checkBox = new JCheckBox();

        public CheckBoxEditor() {
            checkBox.setHorizontalAlignment(JCheckBox.CENTER);
            checkBox.setBorderPaintedFlat(true);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            checkBox.setSelected((Boolean) value);
            return checkBox;
        }

        @Override
        public Object getCellEditorValue() {
            return checkBox.isSelected();
        }
    }

    /**
     * 刷新自定义表格
     *
     * @author mabin
     * @date 2024/05/10 13:34
     */
    private void refreshYapiTable() {
        Vector<Vector<Object>> vectorRows = new Vector<>();
        for (Map.Entry<String, YApiTableDTO> entry : yApiConfig.getYapiTableMap().entrySet()) {
            YApiTableDTO rowDTO = entry.getValue();
            Vector<Object> row = new Vector<>(8);
            row.add(Convert.toBool(rowDTO.getSelect(), Boolean.FALSE));
            row.add(rowDTO.getProjectId());
            row.add(rowDTO.getProjectName());
            row.add(rowDTO.getProjectToken());
            vectorRows.add(row);
        }
        yapiTableModel = new DefaultTableModel(vectorRows, COLUMN_NAMES);
        yapiTable.setModel(yapiTableModel);
        yapiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumn column = yapiTable.getColumnModel().getColumn(0);
        column.setCellRenderer(new CheckBoxRenderer());
        column.setCellEditor(new CheckBoxEditor());
        int columnWidth = new JCheckBox().getPreferredSize().width;
        column.setResizable(false);
        column.setPreferredWidth(columnWidth + 30);
        column.setMaxWidth(columnWidth + 30);
        column.setMinWidth(columnWidth);
        TableColumn oneColumn = yapiTable.getColumnModel().getColumn(1);
        oneColumn.setPreferredWidth(Convert.toInt(yapiTable.getWidth() * 0.1));
    }

    public void reset() {
        setYapiEnableButton(yApiConfig.getApiEnable());
        setYapiServerUrlTextField(yApiConfig.getApiServerUrl());
    }

    public JComponent getComponent() {
        return rootPanel;
    }

    public JPanel getCenterPanel() {
        return centerPanel;
    }

    public void setCenterPanel(JPanel centerPanel) {
        this.centerPanel = centerPanel;
    }

    public JPanel getToolPanel() {
        return toolPanel;
    }

    public void setToolPanel(JPanel toolPanel) {
        this.toolPanel = toolPanel;
    }

    public JPanel getBasePanel() {
        return basePanel;
    }

    public void setBasePanel(JPanel basePanel) {
        this.basePanel = basePanel;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public OnOffButton getYapiEnableButton() {
        return yapiEnableButton;
    }

    public void setYapiEnableButton(Boolean yapiEnableButton) {
        this.yapiEnableButton.setSelected(yapiEnableButton);
    }

    public JLabel getYapiServerUrlLabel() {
        return yapiServerUrlLabel;
    }

    public void setYapiServerUrlLabel(JLabel yapiServerUrlLabel) {
        this.yapiServerUrlLabel = yapiServerUrlLabel;
    }

    public JTextField getYapiServerUrlTextField() {
        return yapiServerUrlTextField;
    }

    public void setYapiServerUrlTextField(String yapiServerUrlTextField) {
        this.yapiServerUrlTextField.setText(yapiServerUrlTextField);
    }

    public JTable getYapiTable() {
        return yapiTable;
    }

    public void setYapiTable(JTable yapiTable) {
        this.yapiTable = yapiTable;
    }

    public DefaultTableModel getYapiTableModel() {
        return yapiTableModel;
    }

    public void setYapiTableModel(DefaultTableModel yapiTableModel) {
        this.yapiTableModel = yapiTableModel;
    }
}

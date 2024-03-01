package easy.form.doc.template;

import com.google.common.collect.Maps;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocTemplateConfig.CustomValue;
import easy.settings.doc.template.AbstractJavaDocTemplateSettingView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class FieldSettingsView extends AbstractJavaDocTemplateSettingView {

    private JPanel panel;
    private JTextArea templateTextArea;
    private JPanel innerVariablePanel;
    private JPanel customVariablePanel;
    private JPanel templatePanel;
    private JRadioButton defaultRadioButton;
    private JRadioButton customRadioButton;
    private JTable innerTable;
    private JScrollPane innerScrollPane;
    private JTable customTable;
    private static Map<String, String> innerMap;

    static {
        innerMap = Maps.newHashMap();
        innerMap.put("$DOC$", "注释信息");
        innerMap.put("$SEE$", "字段类型");
    }

    private void createUIComponents() {
        // 初始化内置变量表格
        Vector<Vector<String>> innerData = new Vector<>(innerMap.size());
        for (Entry<String, String> entry : innerMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Vector<String> row = new Vector<>(2);
            row.add(key);
            row.add(value);
            innerData.add(row);
        }
        DefaultTableModel innerModel = new DefaultTableModel(innerData, innerNames);
        innerTable = new JBTable(innerModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        innerScrollPane = new JBScrollPane(innerTable);

        //设置表格显示的大小。
        innerTable.setPreferredScrollableViewportSize(new Dimension(-1, innerTable.getRowHeight() * innerTable.getRowCount()));
        innerTable.setFillsViewportHeight(true);

        customTable = new JBTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        refreshCustomTable();
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(customTable);
        toolbarDecorator.setAddAction(button -> {
            CustomTemplateAddView customTemplateAddView = new CustomTemplateAddView();
            if (customTemplateAddView.showAndGet()) {
                if (config != null) {
                    Entry<String, CustomValue> entry = customTemplateAddView.getEntry();
                    config.getJavaDocFieldTemplateConfig().getCustomMap().put(entry.getKey(), entry.getValue());
                    refreshCustomTable();
                }
            }
        });
        toolbarDecorator.setRemoveAction(anActionButton -> {
            if (config != null) {
                Map<String, CustomValue> customMap = config.getJavaDocFieldTemplateConfig().getCustomMap();
                customMap.remove(customTable.getValueAt(customTable.getSelectedRow(), 0).toString());
                refreshCustomTable();
            }
        });
        customVariablePanel = toolbarDecorator.createPanel();
    }

    public FieldSettingsView(JavaDocConfig config) {
        super(config);
        // 添加单选按钮事件
        defaultRadioButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            if (button.isSelected()) {
                customRadioButton.setSelected(false);
                templateTextArea.setEnabled(false);
                customTable.setEnabled(false);
                templatePanel.setEnabled(false);
                customVariablePanel.setEnabled(false);
                innerTable.setEnabled(false);
                innerScrollPane.setEnabled(false);
                innerVariablePanel.setEnabled(false);
            }
        });
        customRadioButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            if (button.isSelected()) {
                defaultRadioButton.setSelected(false);
                templateTextArea.setEnabled(true);
                customTable.setEnabled(true);
                templatePanel.setEnabled(true);
                customVariablePanel.setEnabled(true);
                innerTable.setEnabled(true);
                innerScrollPane.setEnabled(true);
                innerVariablePanel.setEnabled(true);
            }
        });
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    private void refreshCustomTable() {
        // 初始化自定义变量表格
        Map<String, CustomValue> customMap = Maps.newHashMap();
        if (config != null && config.getJavaDocFieldTemplateConfig() != null && config.getJavaDocFieldTemplateConfig().getCustomMap() != null) {
            customMap = config.getJavaDocFieldTemplateConfig().getCustomMap();
        }
        Vector<Vector<String>> customData = new Vector<>(customMap.size());
        for (Entry<String, CustomValue> entry : customMap.entrySet()) {
            String key = entry.getKey();
            CustomValue value = entry.getValue();
            Vector<String> row = new Vector<>(3);
            row.add(key);
            row.add(value.getJavaDocVariableTypeEnum().getDesc());
            row.add(value.getValue());
            customData.add(row);
        }
        DefaultTableModel customModel = new DefaultTableModel(customData, customNames);
        customTable.setModel(customModel);
        customTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customTable.getColumnModel().getColumn(0).setPreferredWidth((int) (customTable.getWidth() * 0.3));
    }

    public boolean isDefault() {
        return defaultRadioButton.isSelected();
    }

    public void setDefault(boolean isDefault) {
        if (isDefault) {
            defaultRadioButton.setSelected(true);
            customRadioButton.setSelected(false);
        } else {
            defaultRadioButton.setSelected(false);
            customRadioButton.setSelected(true);
        }
    }

    public String getTemplate() {
        return templateTextArea.getText();
    }

    public void setTemplate(String template) {
        templateTextArea.setText(template);
    }

}

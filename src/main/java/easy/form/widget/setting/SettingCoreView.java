package easy.form.widget.setting;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import easy.config.widget.WidgetConfig;
import easy.config.widget.WidgetConfigComponent;
import easy.enums.WidgetCoreTabEnum;
import easy.helper.ServiceHelper;
import easy.ui.AttributeItem;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class SettingCoreView extends CoreCommonView {
    private JPanel panel;
    private JLabel settingLabel;
    private JBList<AttributeItem> settingItemList;
    private JScrollPane settingScrollPane;
    private JButton selectAllButton;
    private JButton saveSettingButton;
    private final WidgetConfig widgetConfig = ServiceHelper.getService(WidgetConfigComponent.class).getState();

    public SettingCoreView() {
        // 设置提示
        settingLabel.setText("提示: 请选择 Widget Core View 视窗展示的Tab选项卡, 默认按字典顺序全部展示");
        settingLabel.setForeground(JBColor.RED);
        // 渲染列表
        settingItemList.setListData(getDisplayTabList().toArray(new AttributeItem[0]));
        settingItemList.setCellRenderer((list, attributeItem, index, isSelected, cellHasFocus) -> {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel itemLabel = new JLabel(attributeItem.getName());
            itemLabel.setIcon(attributeItem.getIcon());
            panel.add(itemLabel);
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(attributeItem.isSelected());
            checkBox.setOpaque(false);
            checkBox.setBorderPainted(false);
            checkBox.setFocusPainted(false);
            checkBox.setBackground(JBColor.background());
            checkBox.setForeground(JBColor.foreground());
            checkBox.addItemListener(e -> {
                attributeItem.setSelected(e.getStateChange() == ItemEvent.SELECTED);
                list.repaint();
            });
            panel.add(checkBox, BorderLayout.WEST);
            panel.setBorder(JBUI.Borders.empty(0, 5));
            panel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            itemLabel.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return panel;
        });
        settingItemList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = settingItemList.locationToIndex(e.getPoint());
                if (index != -1) {
                    AttributeItem item = settingItemList.getModel().getElementAt(index);
                    item.setSelected(!item.isSelected());
                    settingItemList.repaint();
                }
            }
        });
        // 全选
        selectAllButton.setIcon(AllIcons.Actions.CheckMulticaret);
        selectAllButton.addActionListener(e -> {
            for (int i = 0; i < settingItemList.getModel().getSize(); i++) {
                settingItemList.getModel().getElementAt(i).setSelected(true);
            }
            settingItemList.repaint();
        });
        // 保存
        saveSettingButton.setIcon(AllIcons.Actions.MenuSaveall);
        saveSettingButton.addActionListener(e -> {
            if (Objects.nonNull(widgetConfig)) {
                Set<String> selectedTabList = new HashSet<>(settingItemList.getModel().getSize());
                for (int i = 0; i < settingItemList.getModel().getSize(); i++) {
                    AttributeItem item = settingItemList.getModel().getElementAt(i);
                    if (item.isSelected()) {
                        selectedTabList.add(item.getName());
                    }
                }
                if (CollectionUtils.isEmpty(selectedTabList)) {
                    MessageUtil.showWarningDialog("请至少选择一个选项卡");
                    return;
                }
                widgetConfig.setWidgetCoreTabSet(selectedTabList);
                MessageUtil.showInfoMessage("保存成功, 重新打开View视窗生效");
            }
        });
    }

    /**
     * 获取显示选项卡列表
     *
     * @return {@link java.util.List<easy.ui.AttributeItem>}
     * @author mabin
     * @date 2024/07/04 09:34
     */
    private List<AttributeItem> getDisplayTabList() {
        List<AttributeItem> attributeItemList = new ArrayList<>();
        for (WidgetCoreTabEnum tabEnum : WidgetCoreTabEnum.values()) {
            if (StringUtils.equals(tabEnum.getTitle(), WidgetCoreTabEnum.WINDOWS_PROCESS.getTitle()) && !SystemInfo.isWindows) {
                continue;
            }
            if (Objects.nonNull(widgetConfig) && CollectionUtils.isNotEmpty(widgetConfig.getWidgetCoreTabSet())) {
                attributeItemList.add(new AttributeItem(tabEnum.getTitle(), null, AllIcons.Actions.PinTab,
                        null, widgetConfig.getWidgetCoreTabSet().contains(tabEnum.getTitle())));
            } else {
                attributeItemList.add(new AttributeItem(tabEnum.getTitle(), null, AllIcons.Actions.PinTab, null, false));
            }
        }
        return attributeItemList;
    }

    public JPanel getContent() {
        return panel;
    }

}

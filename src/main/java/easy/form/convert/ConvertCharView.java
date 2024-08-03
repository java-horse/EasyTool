package easy.form.convert;

import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import easy.base.Constants;
import easy.config.convert.ConvertCharConfig;
import easy.config.convert.ConvertCharConfigComponent;
import easy.helper.ServiceHelper;
import easy.util.BundleUtil;
import easy.util.MessageUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;

public class ConvertCharView {

    private ConvertCharConfig config = ServiceHelper.getService(ConvertCharConfigComponent.class).getState();

    private JPanel panel;
    private JButton resetButton;
    private JPanel charMappingPanel;
    private JList<Map.Entry<String, String>> charMappingList;

    public ConvertCharView() {
        resetButton.setIcon(AllIcons.Actions.Refresh);
        resetButton.addActionListener(e -> {
            if (MessageConstants.OK == MessageUtil.showOkCancelDialog("确认重置全局中英文字符映射？")) {
                config.getConvertCharMap().putAll(Constants.DEFAULT_CHAR_MAPPING);
                refreshCharMap();
            }
        });
        refreshCharMap();
    }

    /**
     * 刷新字符映射
     *
     * @author mabin
     * @date 2024/08/03 09:43
     */
    private void refreshCharMap() {
        if (Objects.nonNull(config) && Objects.nonNull(config.getConvertCharMap())) {
            charMappingList.setModel(new CollectionListModel<>(Lists.newArrayList(config.getConvertCharMap().entrySet())));
        }
    }

    private void createUIComponents() {
        charMappingList = new JBList<>(new CollectionListModel<>(Lists.newArrayList()));
        charMappingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        charMappingList.setSelectedIndex(Constants.NUM.ZERO);
        charMappingList.setCellRenderer(new SimpleListCellRenderer<>() {
            @Override
            public void customize(@NotNull JList<? extends Map.Entry<String, String>> list, Map.Entry<String, String> value, int index, boolean selected, boolean hasFocus) {
                setText(String.format("%s <--> %s", value.getKey(), value.getValue()));
                setFont(new Font("微软雅黑", Font.BOLD, 15));
            }
        });
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(charMappingList);
        decorator.disableUpDownActions();
        decorator.setAddAction(button -> {
            Map.Entry<String, String> selectedEntry = charMappingList.getSelectedValue();
            CharMappingEditView editView;
            if (Objects.isNull(selectedEntry)) {
                editView = new CharMappingEditView(null, null);
            } else {
                editView = new CharMappingEditView(selectedEntry.getKey(), selectedEntry.getValue());
            }
            if (editView.showAndGet()) {
                Map.Entry<String, String> newEntry = editView.getCharMapping();
                config.getConvertCharMap().put(newEntry.getKey(), newEntry.getValue());
                refreshCharMap();
            }
        });
        decorator.setRemoveAction(button -> {
            Map.Entry<String, String> selectedEntry = charMappingList.getSelectedValue();
            if (Objects.isNull(selectedEntry)) {
                return;
            }
            if (MessageUtil.showOkCancelDialog(String.format("确认移除【%s】映射数据?", selectedEntry.getKey())) == MessageConstants.OK) {
                config.getConvertCharMap().remove(selectedEntry.getKey());
                refreshCharMap();
            }
        });
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.addAction(new AnAction(() -> BundleUtil.getI18n("global.button.clear.text"), AllIcons.Actions.GC) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                if (MessageConstants.OK == MessageUtil.showOkCancelDialog("确认清空全局中英文字符映射?")) {
                    config.getConvertCharMap().clear();
                    refreshCharMap();
                }
            }
        });
        decorator.setActionGroup(actionGroup);
        charMappingPanel = decorator.createPanel();
    }

    public JComponent getComponent() {
        return panel;
    }

    public JList<Map.Entry<String, String>> getCharMappingList() {
        return charMappingList;
    }

}

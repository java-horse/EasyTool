package easy.ui;

import cn.hutool.core.util.StrUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import easy.enums.ExtraPackageNameEnum;
import easy.enums.WidgetAnnotationRuleEnum;
import easy.enums.WidgetAnnotationToolEnum;
import easy.util.BundleUtil;
import easy.util.MessageUtil;
import easy.util.PsiElementUtil;
import easy.widget.annotation.DoAnnotationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class WidgetToolViewDialog extends DialogWrapper {

    private static final String ADD = "Add";
    private static final String REMOVE = "Remove";

    private JBList<AttributeItem> attributesList;
    private List<AttributeItem> attributes;
    private Project project;
    private PsiFile psiFile;
    private PsiClass psiClass;
    private ButtonGroup operateButtonGroup;
    private ButtonGroup jsonButtonGroup;
    private ButtonGroup ruleButtonGroup;
    private JCheckBox allCheckbox;

    public WidgetToolViewDialog(Project project, PsiFile psiFile, PsiClass psiClass) {
        super(project);
        setTitle("Widget Annotation View");
        this.project = project;
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.attributes = getAttributes();
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        // 创建顶部工具按钮
        JPanel toolPanel = new JPanel(new BorderLayout());
        operateButtonGroup = new ButtonGroup();
        JRadioButton addRadioButton = new JRadioButton(ADD);
        addRadioButton.setSelected(true);
        JRadioButton removeRadioButton = new JRadioButton(REMOVE);
        removeRadioButton.setSelected(false);
        operateButtonGroup.add(addRadioButton);
        operateButtonGroup.add(removeRadioButton);
        JPanel operateRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        operateRadioPanel.add(addRadioButton);
        operateRadioPanel.add(removeRadioButton);
        toolPanel.add(operateRadioPanel, BorderLayout.NORTH);

        jsonButtonGroup = new ButtonGroup();
        String type = getSelectedJsonType(psiClass);
        JRadioButton jacksonRadioButton = new JRadioButton(WidgetAnnotationToolEnum.JACKSON.getName());
        jacksonRadioButton.setSelected(WidgetAnnotationToolEnum.JACKSON.getName().equals(type));
        JRadioButton fastJsonRadioButton = new JRadioButton(WidgetAnnotationToolEnum.FASTJSON.getName());
        fastJsonRadioButton.setSelected(WidgetAnnotationToolEnum.FASTJSON.getName().equals(type));
        JRadioButton gsonRadioButton = new JRadioButton(WidgetAnnotationToolEnum.GSON.getName());
        gsonRadioButton.setSelected(WidgetAnnotationToolEnum.GSON.getName().equals(type));
        JRadioButton easyExcelRadioButton = new JRadioButton(WidgetAnnotationToolEnum.EASY_EXCEL.getName());
        easyExcelRadioButton.setSelected(WidgetAnnotationToolEnum.EASY_EXCEL.getName().equals(type));
        jsonButtonGroup.add(jacksonRadioButton);
        jsonButtonGroup.add(fastJsonRadioButton);
        jsonButtonGroup.add(gsonRadioButton);
        jsonButtonGroup.add(easyExcelRadioButton);
        JPanel jsonRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jsonRadioPanel.add(jacksonRadioButton);
        jsonRadioPanel.add(fastJsonRadioButton);
        jsonRadioPanel.add(gsonRadioButton);
        jsonRadioPanel.add(easyExcelRadioButton);
        toolPanel.add(jsonRadioPanel, BorderLayout.CENTER);

        ruleButtonGroup = new ButtonGroup();
        JRadioButton camelCaseRadioButton = new JRadioButton(WidgetAnnotationRuleEnum.CAMEL_CASE.getRule());
        camelCaseRadioButton.setSelected(true);
        JRadioButton originalRadioButton = new JRadioButton(WidgetAnnotationRuleEnum.ORIGINAL.getRule());
        originalRadioButton.setSelected(false);
        JRadioButton underlineRadioButton = new JRadioButton(WidgetAnnotationRuleEnum.UNDERLINE.getRule());
        underlineRadioButton.setSelected(false);
        JRadioButton upperCaseRadioButton = new JRadioButton(WidgetAnnotationRuleEnum.UPPER_CASE.getRule());
        upperCaseRadioButton.setSelected(false);
        JRadioButton lowerCaseRadioButton = new JRadioButton(WidgetAnnotationRuleEnum.LOWER_CASE.getRule());
        lowerCaseRadioButton.setSelected(false);
        ruleButtonGroup.add(camelCaseRadioButton);
        ruleButtonGroup.add(originalRadioButton);
        ruleButtonGroup.add(underlineRadioButton);
        ruleButtonGroup.add(upperCaseRadioButton);
        ruleButtonGroup.add(lowerCaseRadioButton);
        JPanel ruleRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ruleRadioPanel.add(camelCaseRadioButton);
        ruleRadioPanel.add(originalRadioButton);
        ruleRadioPanel.add(underlineRadioButton);
        ruleRadioPanel.add(upperCaseRadioButton);
        ruleRadioPanel.add(lowerCaseRadioButton);
        toolPanel.add(ruleRadioPanel, BorderLayout.SOUTH);
        centerPanel.add(toolPanel, BorderLayout.NORTH);

        // 规则选项动态隐藏/显示
        removeRadioButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ruleRadioPanel.setVisible(false);
            }
        });
        easyExcelRadioButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ruleRadioPanel.setVisible(false);
            }
        });
        addRadioButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && (jacksonRadioButton.isSelected()
                    || fastJsonRadioButton.isSelected() || gsonRadioButton.isSelected())) {
                ruleRadioPanel.setVisible(true);
            }
        });
        jacksonRadioButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && addRadioButton.isSelected()) {
                ruleRadioPanel.setVisible(true);
            }
        });
        fastJsonRadioButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && addRadioButton.isSelected()) {
                ruleRadioPanel.setVisible(true);
            }
        });
        gsonRadioButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && addRadioButton.isSelected()) {
                ruleRadioPanel.setVisible(true);
            }
        });

        // 显示属性列表
        attributesList = new JBList<>(attributes);
        attributesList.setCellRenderer((list, attributeItem, index, isSelected, cellHasFocus) -> {
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
        attributesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = attributesList.locationToIndex(e.getPoint());
                if (index != -1) {
                    AttributeItem item = attributesList.getModel().getElementAt(index);
                    item.setSelected(!item.isSelected());
                    attributesList.repaint();
                }
            }
        });
        centerPanel.add(new JScrollPane(attributesList), BorderLayout.CENTER);

        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        allCheckbox = new JCheckBox("Select All");
        allCheckbox.addItemListener(e -> {
            for (int i = 0; i < attributesList.getModel().getSize(); i++) {
                attributesList.getModel().getElementAt(i).setSelected(e.getStateChange() == ItemEvent.SELECTED);
            }
            attributesList.repaint();
        });
        checkBoxPanel.add(allCheckbox);
        centerPanel.add(checkBoxPanel, BorderLayout.SOUTH);

        Dimension size = centerPanel.getPreferredSize();
        setSize(Math.max(size.width, 700), Math.max(size.height, 400));
        return centerPanel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

    @Override
    protected void doOKAction() {
        // 是否存在选择属性
        List<AttributeItem> selectedItemList = new ArrayList<>();
        for (int i = 0; i < attributesList.getModel().getSize(); i++) {
            AttributeItem item = attributesList.getModel().getElementAt(i);
            if (Objects.nonNull(item) && item.isSelected()) {
                selectedItemList.add(item);
            }
        }
        if (CollectionUtils.isEmpty(selectedItemList)) {
            MessageUtil.showInfoMessage(BundleUtil.getI18n("global.message.handle.unselected"));
            return;
        }
        // 操作类型
        String operate = StringUtils.EMPTY;
        for (Enumeration<AbstractButton> buttons = operateButtonGroup.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                operate = button.getText();
            }
        }
        // 注解类型
        WidgetAnnotationToolEnum typeEnum = null;
        for (Enumeration<AbstractButton> buttons = jsonButtonGroup.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                typeEnum = WidgetAnnotationToolEnum.getEnum(button.getText());
            }
        }
        // 处理规则
        WidgetAnnotationRuleEnum ruleEnum = null;
        for (Enumeration<AbstractButton> buttons = ruleButtonGroup.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                ruleEnum = WidgetAnnotationRuleEnum.getEnum(button.getText());
            }
        }
        if (Objects.isNull(typeEnum) || Objects.isNull(ruleEnum)) {
            return;
        }
        DoAnnotationService annotationService = typeEnum.getAnnotationService();
        for (AttributeItem item : selectedItemList) {
            switch (operate) {
                case ADD -> annotationService.addAnnotation(project, psiFile, item.getPsiElement(), ruleEnum.genName(item.getRealName()));
                case REMOVE -> annotationService.removeAnnotation(item.getPsiElement());
            }
        }
        super.doOKAction();
    }

    /**
     * 获取属性列表
     *
     * @return {@link java.util.List<easy.ui.AttributeItem>}
     * @author mabin
     * @date 2024/06/01 09:42
     */
    private List<AttributeItem> getAttributes() {
        if (Objects.isNull(psiClass)) {
            return Collections.emptyList();
        }
        LinkedHashSet<PsiClass> psiClassSet = new LinkedHashSet<>();
        psiClassSet.add(psiClass);
        psiClassSet.addAll(List.of(psiClass.getInnerClasses()));
        List<AttributeItem> attributeItemList = new ArrayList<>();
        for (PsiClass psiClass : psiClassSet) {
            if (!PsiElementUtil.isEntity(psiClass)) {
                continue;
            }
            String className = PsiElementUtil.getPsiElementNameIdentifierText(psiClass);
            PsiField[] psiFields = psiClass.getFields();
            if (ArrayUtils.isEmpty(psiFields)) {
                continue;
            }
            for (PsiField psiField : psiFields) {
                // 排除注入Spring容器的Bean
                if (Arrays.stream(psiField.getAnnotations()).anyMatch(annotation -> StringUtils.equalsAny(annotation.getQualifiedName(),
                        ExtraPackageNameEnum.RESOURCE.getName(), ExtraPackageNameEnum.AUTOWIRED.getName()))) {
                    continue;
                }
                // 属性是否物理存在的(可排除lombok动态生成的)
                if (!psiField.isPhysical()) {
                    continue;
                }
                String psiFieldName = PsiElementUtil.getPsiElementNameIdentifierText(psiField);
                attributeItemList.add(new AttributeItem(String.format(getBoldText(className), StrUtil.DOT + psiFieldName), psiFieldName, AllIcons.Nodes.Field, psiField, false));
            }
        }
        return attributeItemList;
    }

    /**
     * 获取粗体文本
     *
     * @param text 文本
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/05/30 10:46
     */
    private String getBoldText(String text) {
        if (StringUtils.isBlank(text)) {
            return StringUtils.EMPTY;
        }
        return String.format("<html><strong>%s</strong>", text) + "%s</html>";
    }

    /**
     * 获取选定的json类型
     *
     * @param psiClass psi等级
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/06/21 16:39
     */
    private String getSelectedJsonType(PsiClass psiClass) {
        if (Objects.isNull(psiClass)) {
            return WidgetAnnotationToolEnum.JACKSON.getName();
        }
        for (PsiField psiField : psiClass.getFields()) {
            if (Objects.nonNull(psiField.getAnnotation(ExtraPackageNameEnum.JSON_PROPERTY.getName()))) {
                return WidgetAnnotationToolEnum.JACKSON.getName();
            } else if (Objects.nonNull(psiField.getAnnotation(ExtraPackageNameEnum.JSON_FIELD.getName()))) {
                return WidgetAnnotationToolEnum.FASTJSON.getName();
            } else if (Objects.nonNull(psiField.getAnnotation(ExtraPackageNameEnum.SERIALIZED_NAME.getName()))) {
                return WidgetAnnotationToolEnum.GSON.getName();
            } else if (Objects.nonNull(psiField.getAnnotation(ExtraPackageNameEnum.EXCEL_PROPERTY.getName()))) {
                return WidgetAnnotationToolEnum.EASY_EXCEL.getName();
            }
        }
        return WidgetAnnotationToolEnum.JACKSON.getName();
    }

}

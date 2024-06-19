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
import easy.enums.WidgetToolEnum;
import easy.util.PsiElementUtil;
import easy.widget.annotation.DoAnnotationService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class WidgetToolViewDialog extends DialogWrapper {

    private JBList<AttributeItem> attributesList;
    private List<AttributeItem> attributes;
    private Project project;
    private PsiFile psiFile;
    private PsiClass psiClass;
    private ButtonGroup jsonButtonGroup;
    private ButtonGroup ruleButtonGroup;
    private JCheckBox allCheckbox;

    public WidgetToolViewDialog(Project project, PsiFile psiFile, PsiClass psiClass) {
        super(project);
        setTitle("Widget Tool Annotation View");
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
        jsonButtonGroup = new ButtonGroup();
        JRadioButton jacksonRadioButton = new JRadioButton(WidgetToolEnum.JACKSON.getName());
        jacksonRadioButton.setSelected(true);
        JRadioButton fastJsonRadioButton = new JRadioButton(WidgetToolEnum.FASTJSON.getName());
        fastJsonRadioButton.setSelected(false);
        JRadioButton gsonRadioButton = new JRadioButton(WidgetToolEnum.GSON.getName());
        gsonRadioButton.setSelected(false);
        JRadioButton easyExcelRadioButton = new JRadioButton(WidgetToolEnum.EASY_EXCEL.getName());
        easyExcelRadioButton.setSelected(false);
        jsonButtonGroup.add(jacksonRadioButton);
        jsonButtonGroup.add(fastJsonRadioButton);
        jsonButtonGroup.add(gsonRadioButton);
        jsonButtonGroup.add(easyExcelRadioButton);
        JPanel jsonRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jsonRadioPanel.add(jacksonRadioButton);
        jsonRadioPanel.add(fastJsonRadioButton);
        jsonRadioPanel.add(gsonRadioButton);
        jsonRadioPanel.add(easyExcelRadioButton);
        toolPanel.add(jsonRadioPanel, BorderLayout.NORTH);

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
        JScrollPane scrollPane = new JScrollPane(attributesList);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

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
        super.doOKAction();
        // 处理类型
        WidgetToolEnum typeEnum = null;
        for (Enumeration<AbstractButton> buttons = jsonButtonGroup.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                typeEnum = WidgetToolEnum.getEnum(button.getText());
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
        for (int i = 0; i < attributesList.getModel().getSize(); i++) {
            AttributeItem item = attributesList.getModel().getElementAt(i);
            if (item.isSelected()) {
                annotationService.addAnnotation(project, psiFile, item.getPsiElement(), ruleEnum.genName(item.getRealName()));
            }
        }
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
                if (Arrays.stream(psiField.getAnnotations()).anyMatch(annotation -> StringUtils.equalsAny(annotation.getQualifiedName(), Resource.class.getName(), ExtraPackageNameEnum.AUTOWIRED.getName()))) {
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

}

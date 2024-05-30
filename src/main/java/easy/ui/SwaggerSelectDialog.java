package easy.ui;

import cn.hutool.core.util.StrUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.*;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import easy.base.Constants;
import easy.enums.RequestAnnotationEnum;
import easy.enums.SwaggerAnnotationEnum;
import easy.enums.SwaggerServiceEnum;
import easy.swagger.SwaggerGenerateService;
import easy.util.PsiElementUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class SwaggerSelectDialog extends DialogWrapper {

    private JBList<AttributeItem> attributesList;
    private List<AttributeItem> attributes;
    private Project project;
    private PsiClass psiClass;
    private PsiFile psiFile;
    private JRadioButton swagger2RadioButton;
    private JRadioButton swagger3RadioButton;
    private ButtonGroup swaggerButtonGroup;
    private JCheckBox allCheckbox;

    public SwaggerSelectDialog(Project project, PsiClass psiClass, PsiFile psiFile) {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle(SwaggerServiceEnum.SWAGGER_VIEW.getName());
        this.project = project;
        this.psiClass = psiClass;
        this.psiFile = psiFile;
        this.attributes = getAttributes();
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());

        // 创建顶部Swagger版本单选框
        String selectedSwagger = getSelectedSwagger(psiClass);
        swaggerButtonGroup = new ButtonGroup();
        swagger2RadioButton = new JRadioButton(SwaggerServiceEnum.SWAGGER_2.getName());
        swagger2RadioButton.setSelected(StringUtils.equals(SwaggerServiceEnum.SWAGGER_2.getName(), selectedSwagger));
        swagger3RadioButton = new JRadioButton(SwaggerServiceEnum.SWAGGER_3.getName());
        swagger3RadioButton.setSelected(StringUtils.equals(SwaggerServiceEnum.SWAGGER_3.getName(), selectedSwagger));
        swaggerButtonGroup.add(swagger2RadioButton);
        swaggerButtonGroup.add(swagger3RadioButton);
        JPanel swaggerRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        swaggerRadioPanel.add(swagger2RadioButton);
        swaggerRadioPanel.add(swagger3RadioButton);
        centerPanel.add(swaggerRadioPanel, BorderLayout.NORTH);

        // 创建中心的方法或属性多选框
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

        // 创建是否全选复选框
        allCheckbox = new JCheckBox("Select All");
        allCheckbox.addItemListener(e -> {
            for (int i = 0; i < attributesList.getModel().getSize(); i++) {
                attributesList.getModel().getElementAt(i).setSelected(e.getStateChange() == ItemEvent.SELECTED);
            }
            attributesList.repaint();
        });
        centerPanel.add(allCheckbox, BorderLayout.SOUTH);

        Dimension size = centerPanel.getPreferredSize();
        setSize(Math.max(size.width, 600), Math.max(size.height, 300));
        return centerPanel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        // 获取选中swagger版本
        SwaggerServiceEnum swaggerServiceEnum = null;
        for (Enumeration<AbstractButton> buttons = swaggerButtonGroup.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                swaggerServiceEnum = SwaggerServiceEnum.getSwaggerAnnotationEnum(button.getText());
            }
        }
        if (Objects.isNull(swaggerServiceEnum)) {
            return;
        }
        SwaggerGenerateService swaggerGenerateService = swaggerServiceEnum.getSwaggerGenerateService();
        // 遍历生成选中属性swagger注解
        for (int i = 0; i < attributesList.getModel().getSize(); i++) {
            AttributeItem item = attributesList.getModel().getElementAt(i);
            if (item.isSelected()) {
                swaggerGenerateService.initSwaggerConfig(project, psiFile, psiClass, item.getRealName(), swaggerServiceEnum);
                swaggerGenerateService.doGenerate();
            }
        }
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

    /**
     * 获取属性列表
     *
     * @return {@link java.util.List<easy.ui.SwaggerSelectDialog.AttributeItem>}
     * @author mabin
     * @date 2024/04/27 17:24
     */
    private List<AttributeItem> getAttributes() {
        List<AttributeItem> attributeItemList = new ArrayList<>();
        if (Objects.isNull(psiClass)) {
            return attributeItemList;
        }
        String className = PsiElementUtil.getPsiElementNameIdentifierText(psiClass);
        if (PsiElementUtil.isController(psiClass)) {
            List<String> classSwaggerList = List.of(SwaggerAnnotationEnum.API.getClassPackage(), SwaggerAnnotationEnum.TAG.getClassPackage());
            attributeItemList.add(new AttributeItem(String.format(getBoldText(className), (StringUtils.isNotBlank(psiClass.getQualifiedName())
                    ? (" (" + psiClass.getQualifiedName() + ")") : StringUtils.EMPTY)), className,
                    getIcon(psiClass.getAnnotations(), classSwaggerList, AllIcons.Nodes.Controller), false));
            List<String> methodSwaggerList = List.of(SwaggerAnnotationEnum.API_OPERATION.getClassPackage(), SwaggerAnnotationEnum.OPERATION.getClassPackage());
            for (PsiMethod psiMethod : psiClass.getMethods()) {
                PsiAnnotation[] psiAnnotations = psiMethod.getAnnotations();
                for (PsiAnnotation psiAnnotation : psiAnnotations) {
                    if (Objects.isNull(RequestAnnotationEnum.getEnumByQualifiedName(psiAnnotation.getQualifiedName()))) {
                        continue;
                    }
                    List<String> paramList = new ArrayList<>();
                    for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {
                        paramList.add(psiParameter.getName() + ":" + StringUtils.split(psiParameter.getType().toString(), ":")[1]);
                    }
                    String psiMethodName = PsiElementUtil.getPsiElementNameIdentifierText(psiMethod);
                    attributeItemList.add(new AttributeItem(String.format(getBoldText(className), StrUtil.DOT + psiMethodName + "(" + StringUtils.trim(String.join(", ", paramList)) + ")"),
                            psiMethodName, getIcon(psiAnnotations, methodSwaggerList, AllIcons.Nodes.Method), false));
                    break;
                }
            }
        } else {
            List<String> classSwaggerList = List.of(SwaggerAnnotationEnum.API_MODEL.getClassPackage(), SwaggerAnnotationEnum.SCHEMA.getClassPackage());
            attributeItemList.add(new AttributeItem(String.format(getBoldText(className), (StringUtils.isNotBlank(psiClass.getQualifiedName())
                    ? (" (" + psiClass.getQualifiedName() + ")") : StringUtils.EMPTY)), className, getIcon(psiClass.getAnnotations(), classSwaggerList, AllIcons.Nodes.Class), false));
            List<String> fieldSwaggerList = List.of(SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassPackage(), SwaggerAnnotationEnum.SCHEMA.getClassPackage());
            for (PsiField psiField : psiClass.getFields()) {
                String psiFieldName = PsiElementUtil.getPsiElementNameIdentifierText(psiField);
                attributeItemList.add(new AttributeItem(String.format(getBoldText(className), StrUtil.DOT + psiFieldName), psiFieldName,
                        getIcon(psiField.getAnnotations(), fieldSwaggerList, StringUtils.equalsIgnoreCase(psiFieldName, Constants.UID)
                                ? AllIcons.Nodes.Constant : AllIcons.Nodes.Field), false));
            }
            PsiClass[] innerClasses = psiClass.getInnerClasses();
            if (ArrayUtils.isNotEmpty(innerClasses)) {
                for (PsiClass innerClass : innerClasses) {
                    String innerClassName = PsiElementUtil.getPsiElementNameIdentifierText(innerClass);
                    attributeItemList.add(new AttributeItem(String.format(getBoldText(innerClassName), (StringUtils.isNotBlank(innerClass.getQualifiedName())
                            ? (" (" + innerClass.getQualifiedName() + ")") : StringUtils.EMPTY)), innerClassName, getIcon(innerClass.getAnnotations(), classSwaggerList, AllIcons.Nodes.Class), false));
                    for (PsiField psiField : innerClass.getFields()) {
                        String psiFieldName = PsiElementUtil.getPsiElementNameIdentifierText(psiField);
                        attributeItemList.add(new AttributeItem(String.format(getBoldText(innerClassName), StrUtil.DOT + psiFieldName),
                                psiFieldName, getIcon(psiField.getAnnotations(), fieldSwaggerList, StringUtils.equalsIgnoreCase(Constants.UID, psiFieldName)
                                ? AllIcons.Nodes.Constant : AllIcons.Nodes.Field), false));
                    }
                }
            }
        }
        return attributeItemList;
    }

    /**
     * 识别并返回更贴切的图标
     *
     * @param psiAnnotations psi注释
     * @param swaggerList    招摇列表
     * @param defaultIcon    默认图标
     * @return {@link Icon}
     * @author mabin
     * @date 2024/05/30 09:51
     */
    private Icon getIcon(PsiAnnotation[] psiAnnotations, List<String> swaggerList, @NotNull Icon defaultIcon) {
        if (ArrayUtils.isEmpty(psiAnnotations)) {
            return defaultIcon;
        }
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (swaggerList.contains(psiAnnotation.getQualifiedName())) {
                return AllIcons.Nodes.AnonymousClass;
            }
        }
        return defaultIcon;
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
     * 识别并自动选中Swagger版本名称(简单识别, 仅支持2.0和3.0)
     *
     * @param psiClass psi级
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/28 10:38
     */
    private String getSelectedSwagger(PsiClass psiClass) {
        if (Objects.isNull(psiClass)) {
            return SwaggerServiceEnum.SWAGGER_2.getName();
        }
        if (Objects.nonNull(psiClass.getAnnotation(SwaggerAnnotationEnum.API.getClassPackage()))
                || Objects.nonNull(psiClass.getAnnotation(SwaggerAnnotationEnum.API_MODEL.getClassPackage()))) {
            return SwaggerServiceEnum.SWAGGER_2.getName();
        } else if (Objects.nonNull(psiClass.getAnnotation(SwaggerAnnotationEnum.TAG.getClassPackage()))
                || Objects.nonNull(psiClass.getAnnotation(SwaggerAnnotationEnum.SCHEMA.getClassPackage()))) {
            return SwaggerServiceEnum.SWAGGER_3.getName();
        }
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            if (Objects.nonNull(psiMethod.getAnnotation(SwaggerAnnotationEnum.API_OPERATION.getClassPackage()))
                    || Objects.nonNull(psiMethod.getAnnotation(SwaggerAnnotationEnum.API_IMPLICIT_PARAMS.getClassPackage()))
                    || Objects.nonNull(psiMethod.getAnnotation(SwaggerAnnotationEnum.API_IMPLICIT_PARAM.getClassPackage()))) {
                return SwaggerServiceEnum.SWAGGER_2.getName();
            } else if (Objects.nonNull(psiMethod.getAnnotation(SwaggerAnnotationEnum.OPERATION.getClassPackage()))
                    || Objects.nonNull(psiMethod.getAnnotation(SwaggerAnnotationEnum.PARAMETER.getClassPackage()))
                    || Objects.nonNull(psiMethod.getAnnotation(SwaggerAnnotationEnum.PARAMETERS.getClassPackage()))) {
                return SwaggerServiceEnum.SWAGGER_3.getName();
            }
        }
        for (PsiField psiField : psiClass.getFields()) {
            if (Objects.nonNull(psiField.getAnnotation(SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassPackage()))) {
                return SwaggerServiceEnum.SWAGGER_2.getName();
            } else if (Objects.nonNull(psiField.getAnnotation(SwaggerAnnotationEnum.SCHEMA.getClassPackage()))) {
                return SwaggerServiceEnum.SWAGGER_3.getName();
            }
        }
        return SwaggerServiceEnum.SWAGGER_2.getName();
    }

    /**
     * 属性项
     *
     * @author mabin
     * @project EasyTool
     * @package easy.ui.SwaggerSelectDialog
     * @date 2024/04/27 17:06
     */
    private static class AttributeItem {
        private String name;
        private String realName;
        private Icon icon;
        private boolean selected;

        public AttributeItem(String name, String realName, Icon icon, boolean selected) {
            this.name = name;
            this.realName = realName;
            this.icon = icon;
            this.selected = selected;
        }

        public Icon getIcon() {
            return icon;
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }
    }

}

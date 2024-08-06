package easy.ui;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.*;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import easy.base.Constants;
import easy.doc.service.JavaDocGenerateService;
import easy.doc.service.JavaDocWriterService;
import easy.enums.RequestAnnotationEnum;
import easy.enums.SwaggerAnnotationEnum;
import easy.enums.SwaggerServiceEnum;
import easy.helper.ServiceHelper;
import easy.swagger.SwaggerGenerateService;
import easy.util.BundleUtil;
import easy.util.MessageUtil;
import easy.util.PsiElementUtil;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class SwaggerViewDialog extends DialogWrapper {

    private final JavaDocGenerateService JavaDocGenerateService = ServiceHelper.getService(JavaDocGenerateService.class);
    private final JavaDocWriterService javaDocWriterService = ServiceHelper.getService(JavaDocWriterService.class);

    private JBList<AttributeItem> attributesList;
    private List<AttributeItem> attributes;
    private Project project;
    private PsiClass psiClass;
    private PsiFile psiFile;
    private JRadioButton swagger2RadioButton;
    private JRadioButton swagger3RadioButton;
    private ButtonGroup swaggerButtonGroup;
    private JCheckBox allCheckbox;
    private JCheckBox syncGenJavaDocCheckBox;

    public SwaggerViewDialog(Project project, PsiClass psiClass, PsiFile psiFile) {
        super(project);
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

        // 创建横向的复选框组
        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        // 创建是否全选复选框
        allCheckbox = new JCheckBox("Select All");
        allCheckbox.addItemListener(e -> {
            for (int i = 0; i < attributesList.getModel().getSize(); i++) {
                attributesList.getModel().getElementAt(i).setSelected(e.getStateChange() == ItemEvent.SELECTED);
            }
            attributesList.repaint();
        });
        checkBoxPanel.add(allCheckbox);
        // 创建是否同步生成JavaDoc复选框
        JPanel syncGenJavaDocPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        syncGenJavaDocCheckBox = new JCheckBox("Sync Gen JavaDoc");
        syncGenJavaDocPanel.add(syncGenJavaDocCheckBox);
        JLabel syncGenJavaDocTipLabel = new JLabel(AllIcons.General.ContextHelp);
        syncGenJavaDocTipLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        syncGenJavaDocTipLabel.setToolTipText("Both Swagger Annotation and JavaDoc notes will be generated");
        syncGenJavaDocPanel.add(syncGenJavaDocTipLabel);
        checkBoxPanel.add(syncGenJavaDocPanel);
        centerPanel.add(checkBoxPanel, BorderLayout.SOUTH);

        Dimension size = centerPanel.getPreferredSize();
        setSize(Math.max(size.width, 700), Math.max(size.height, 400));
        return centerPanel;
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
        // 获取选中swagger版本
        List<SwaggerServiceEnum> swaggerServiceEnums = new ArrayList<>();
        for (Enumeration<AbstractButton> buttons = swaggerButtonGroup.getElements(); buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                swaggerServiceEnums.add(SwaggerServiceEnum.getSwaggerAnnotationEnum(button.getText()));
                break;
            }
        }
        if (CollectionUtils.isEmpty(swaggerServiceEnums) || Objects.isNull(swaggerServiceEnums.get(0))) {
            return;
        }
        SwaggerGenerateService swaggerGenerateService = swaggerServiceEnums.get(0).getSwaggerGenerateService();
        ProgressManager.getInstance().run(new Task.Backgroundable(project, String.format("%s Generate Swagger and JavaDoc", Constants.PLUGIN_NAME), true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                int total = selectedItemList.size();
                for (int i = 0; i < total; i++) {
                    // 生成Swagger
                    AttributeItem item = selectedItemList.get(i);
                    swaggerGenerateService.initSwaggerConfig(project, psiFile, psiClass, item.getRealName(), swaggerServiceEnums.get(0), item.getPsiElement());
                    swaggerGenerateService.doGenerate();
                    // 生成JavaDoc
                    if (Boolean.TRUE.equals(syncGenJavaDocCheckBox.isSelected())) {
                        String comment = JavaDocGenerateService.generate(item.getPsiElement());
                        if (StringUtils.isNotBlank(comment)) {
                            javaDocWriterService.writeJavadoc(project, item.getPsiElement(), comment, Constants.NUM.ZERO);
                        }
                    }
                    if (indicator.isCanceled()) {
                        break;
                    }
                    double fraction = (double) (i + 1) / total;
                    indicator.setFraction(fraction);
                    indicator.setText(String.format("%s completed success %s", item.getName(), NumberUtil.formatPercent(fraction, 2)));
                }
            }
        });
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

    /**
     * 获取属性
     *
     * @return {@link List<AttributeItem>}
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
                    getIcon(psiClass.getAnnotations(), classSwaggerList, AllIcons.Nodes.Controller), psiClass, false));
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
                            psiMethodName, getIcon(psiAnnotations, methodSwaggerList, AllIcons.Nodes.Method), psiMethod, false));
                    break;
                }
            }
        } else {
            List<String> classSwaggerList = List.of(SwaggerAnnotationEnum.API_MODEL.getClassPackage(), SwaggerAnnotationEnum.SCHEMA.getClassPackage());
            attributeItemList.add(new AttributeItem(String.format(getBoldText(className), (StringUtils.isNotBlank(psiClass.getQualifiedName())
                    ? (" (" + psiClass.getQualifiedName() + ")") : StringUtils.EMPTY)), className, getIcon(psiClass.getAnnotations(), classSwaggerList, AllIcons.Nodes.Class), psiClass, false));
            List<String> fieldSwaggerList = List.of(SwaggerAnnotationEnum.API_MODEL_PROPERTY.getClassPackage(), SwaggerAnnotationEnum.SCHEMA.getClassPackage());
            for (PsiField psiField : psiClass.getFields()) {
                if (!psiField.isPhysical()) {
                    continue;
                }
                String psiFieldName = PsiElementUtil.getPsiElementNameIdentifierText(psiField);
                attributeItemList.add(new AttributeItem(String.format(getBoldText(className), StrUtil.DOT + psiFieldName), psiFieldName,
                        getIcon(psiField.getAnnotations(), fieldSwaggerList, StringUtils.equalsIgnoreCase(psiFieldName, CommonClassNames.SERIAL_VERSION_UID_FIELD_NAME)
                                ? AllIcons.Nodes.Constant : AllIcons.Nodes.Field), psiField, false));
            }
            PsiClass[] innerClasses = psiClass.getInnerClasses();
            if (ArrayUtils.isNotEmpty(innerClasses)) {
                for (PsiClass innerClass : innerClasses) {
                    String innerClassName = PsiElementUtil.getPsiElementNameIdentifierText(innerClass);
                    attributeItemList.add(new AttributeItem(String.format(getBoldText(innerClassName), (StringUtils.isNotBlank(innerClass.getQualifiedName())
                            ? (" (" + innerClass.getQualifiedName() + ")") : StringUtils.EMPTY)), innerClassName, getIcon(innerClass.getAnnotations(),
                            classSwaggerList, AllIcons.Nodes.Class), innerClass, false));
                    for (PsiField psiField : innerClass.getFields()) {
                        if (!psiField.isPhysical()) {
                            continue;
                        }
                        String psiFieldName = PsiElementUtil.getPsiElementNameIdentifierText(psiField);
                        attributeItemList.add(new AttributeItem(String.format(getBoldText(innerClassName), StrUtil.DOT + psiFieldName),
                                psiFieldName, getIcon(psiField.getAnnotations(), fieldSwaggerList, StringUtils.equalsIgnoreCase(CommonClassNames.SERIAL_VERSION_UID_FIELD_NAME, psiFieldName)
                                ? AllIcons.Nodes.Constant : AllIcons.Nodes.Field), psiField, false));
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

}

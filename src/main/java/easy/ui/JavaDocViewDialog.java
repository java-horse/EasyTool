package easy.ui;

import cn.hutool.core.util.StrUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import easy.base.Constants;
import easy.doc.service.JavaDocGenerateService;
import easy.doc.service.JavaDocWriterService;
import easy.enums.ExtraPackageNameEnum;
import easy.enums.SpringAnnotationEnum;
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
import java.util.List;
import java.util.*;

public class JavaDocViewDialog extends DialogWrapper {

    private final JavaDocGenerateService JavaDocGenerateService = ServiceHelper.getService(JavaDocGenerateService.class);
    private final JavaDocWriterService javaDocWriterService = ServiceHelper.getService(JavaDocWriterService.class);

    private Project project;
    private PsiClass psiClass;
    private PsiFile psiFile;
    private JBList<AttributeItem> attributesList;
    private List<AttributeItem> attributes;
    private JCheckBox allCheckbox;
    private JCheckBox syncGenSwagger2CheckBox;
    private JCheckBox syncGenSwagger3CheckBox;

    public JavaDocViewDialog(Project project, PsiClass psiClass, PsiFile psiFile) {
        super(project);
        setTitle("JavaDocView");
        this.project = project;
        this.psiClass = psiClass;
        this.psiFile = psiFile;
        this.attributes = getAttributes();
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());

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
        JPanel syncGenSwaggerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        syncGenSwagger2CheckBox = new JCheckBox(String.format("Sync Gen %s", SwaggerServiceEnum.SWAGGER_2.getName()));
        syncGenSwagger2CheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (syncGenSwagger3CheckBox.isSelected()) {
                    syncGenSwagger3CheckBox.setSelected(false);
                }
            } else {
                syncGenSwagger2CheckBox.setSelected(false);
            }
        });
        JLabel syncGenSwagger2TipLabel = new JLabel(AllIcons.General.ContextHelp);
        syncGenSwagger2TipLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        syncGenSwagger2TipLabel.setToolTipText(String.format("Both JavaDoc notes and %s Annotation will be generated", SwaggerServiceEnum.SWAGGER_2.getName()));
        syncGenSwaggerPanel.add(syncGenSwagger2CheckBox);
        syncGenSwaggerPanel.add(syncGenSwagger2TipLabel);
        syncGenSwagger3CheckBox = new JCheckBox(String.format("Sync Gen %s", SwaggerServiceEnum.SWAGGER_3.getName()));
        syncGenSwagger3CheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (syncGenSwagger2CheckBox.isSelected()) {
                    syncGenSwagger2CheckBox.setSelected(false);
                }
            } else {
                syncGenSwagger3CheckBox.setSelected(false);
            }
        });
        JLabel syncGenSwagger3TipLabel = new JLabel(AllIcons.General.ContextHelp);
        syncGenSwagger3TipLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        syncGenSwagger3TipLabel.setToolTipText(String.format("Both JavaDoc notes and %s Annotation will be generated", SwaggerServiceEnum.SWAGGER_3.getName()));
        syncGenSwaggerPanel.add(syncGenSwagger3CheckBox);
        syncGenSwaggerPanel.add(syncGenSwagger3TipLabel);

        // 是否符合生成Swagger注解的标准
        for (PsiAnnotation psiAnnotation : psiClass.getAnnotations()) {
            if (StringUtils.equalsAnyIgnoreCase(psiAnnotation.getQualifiedName(), SpringAnnotationEnum.SERVICE.getName(),
                    SpringAnnotationEnum.COMPONENT.getName(), SpringAnnotationEnum.REPOSITORY.getName(),
                    SpringAnnotationEnum.SPRING_BOOT_APPLICATION.getName(), SpringAnnotationEnum.SPRING_BOOT_TEST.getName(),
                    ExtraPackageNameEnum.RUN_WITH.getName(), SpringAnnotationEnum.CONFIGURATION.getName())) {
                syncGenSwagger2CheckBox.setEnabled(false);
                syncGenSwagger3CheckBox.setEnabled(false);
            }
        }

        checkBoxPanel.add(syncGenSwaggerPanel);
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
        // 获取swagger版本
        SwaggerServiceEnum swaggerServiceEnum = null;
        if (syncGenSwagger2CheckBox.isSelected()) {
            swaggerServiceEnum = SwaggerServiceEnum.SWAGGER_2;
        } else if (syncGenSwagger3CheckBox.isSelected()) {
            swaggerServiceEnum = SwaggerServiceEnum.SWAGGER_3;
        }
        for (AttributeItem item : selectedItemList) {
            // 生成JavaDoc
            String comment = JavaDocGenerateService.generate(item.getPsiElement());
            if (StringUtils.isNotBlank(comment)) {
                javaDocWriterService.writeJavadoc(project, item.getPsiElement(), comment, Constants.NUM.ZERO);
            }
            // 生成Swagger
            if (Objects.nonNull(swaggerServiceEnum)) {
                SwaggerGenerateService swaggerGenerateService = swaggerServiceEnum.getSwaggerGenerateService();
                swaggerGenerateService.initSwaggerConfig(project, psiFile, psiClass, item.getRealName(), swaggerServiceEnum);
                swaggerGenerateService.doGenerate();
            }
        }
        super.doOKAction();
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
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
            // 类
            String className = PsiElementUtil.getPsiElementNameIdentifierText(psiClass);
            attributeItemList.add(new AttributeItem(String.format(getBoldText(className), (StringUtils.isNotBlank(psiClass.getQualifiedName()) ? (" (" + psiClass.getQualifiedName() + ")") : StringUtils.EMPTY)), className,
                    Objects.isNull(psiClass.getDocComment()) ? (PsiElementUtil.isController(psiClass) ? AllIcons.Nodes.Controller : AllIcons.Nodes.Class)
                            : AllIcons.Nodes.AnonymousClass, psiClass, false));
            // 属性
            PsiField[] psiFields = psiClass.getFields();
            if (ArrayUtils.isNotEmpty(psiFields)) {
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
                    attributeItemList.add(new AttributeItem(String.format(getBoldText(className), StrUtil.DOT + psiFieldName), psiFieldName,
                            Objects.isNull(psiField.getDocComment()) ? ((psiField.hasModifierProperty(PsiModifier.STATIC) || psiField.hasModifierProperty(PsiModifier.FINAL))) ? AllIcons.Nodes.Constant : AllIcons.Nodes.Field : AllIcons.Nodes.AnonymousClass, psiField, false));
                }
            }
            // 方法
            PsiMethod[] psiMethods = psiClass.getMethods();
            if (ArrayUtils.isNotEmpty(psiMethods)) {
                for (PsiMethod psiMethod : psiMethods) {
                    if (!psiMethod.isPhysical()) {
                        continue;
                    }
                    List<String> paramList = Arrays.stream(psiMethod.getParameterList().getParameters()).map(psiParameter -> psiParameter.getName() + ":" + StringUtils.split(psiParameter.getType().toString(), ":")[1]).toList();
                    String psiMethodName = PsiElementUtil.getPsiElementNameIdentifierText(psiMethod);
                    attributeItemList.add(new AttributeItem(String.format(getBoldText(className), StrUtil.DOT + psiMethodName + "(" + StringUtils.trim(String.join(", ", paramList)) + ")"), psiMethodName, Objects.isNull(psiMethod.getDocComment()) ? AllIcons.Nodes.Method : AllIcons.Nodes.AnonymousClass, psiMethod, false));
                }
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

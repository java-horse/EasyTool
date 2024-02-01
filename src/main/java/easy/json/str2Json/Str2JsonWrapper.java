package easy.json.str2Json;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.CommonActionsManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.DimensionService;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import easy.action.base.*;
import easy.base.Constants;
import easy.handler.Str2JsonHandler;
import easy.util.BundleUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * @project: EasyTool
 * @package: easy.json.str2Json
 * @author: mabin
 * @date: 2024/01/18 17:10:40
 */
public class Str2JsonWrapper extends DialogWrapper {

    private JPanel rootPanel;
    private JPanel strPanel;
    private JPanel strToolBar;
    private JPanel jsonPanel;
    private JPanel jsonToolBar;
    private JButton convertButton;
    private JButton clearButton;
    private JBLabel lengthLabel;
    private JBLabel lineLabel;
    private JBLabel cursorLabel;
    private JBLabel selectLabel;

    public Str2JsonWrapper(@NotNull Project project) {
        super(project);
        // 初始化UI面板
        setupUI();
        init();
        setTitle(Constants.PLUGIN_NAME + " String To Json");
        // 配置左侧面板编辑器
        EditorFactory editorFactory = EditorFactory.getInstance();
        Editor strEditor = editorFactory.createEditor(editorFactory.createDocument(StringUtils.EMPTY), project, PlainTextLanguage.INSTANCE.getAssociatedFileType(), false);
        configureStrEditor(strEditor, project);
        // 配置右侧面板
        ConsoleViewImpl consoleView = new ConsoleViewImpl(project, true);
        configureJsonPanel(consoleView);
        // 设置主面板尺寸
        DimensionService dimensionService = DimensionService.getInstance();
        Dimension size = dimensionService.getSize("#" + this.getClass().getName(), project);
        if (Objects.nonNull(size)) {
            rootPanel.setPreferredSize(size);
        } else {
            setSize(1000, 800);
        }
        // 设置按钮监听器
        convertButton.addActionListener(e -> {
            Document document = strEditor.getDocument();
            String text = document.getText();
            if (StringUtils.isBlank(text)) {
                return;
            }
            String json = Str2JsonHandler.toJSON(text);
            if (StringUtils.isBlank(json)) {
                return;
            }
            consoleView.clear();
            consoleView.print(json, ConsoleViewContentType.USER_INPUT);
        });
        clearButton.addActionListener(e -> {
            Document document = strEditor.getDocument();
            WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(0, document.getTextLength(), StringUtils.EMPTY));
            consoleView.clear();
        });
    }

    /**
     * 配置右侧面板
     *
     * @param consoleView
     * @return void
     * @author mabin
     * @date 2024/1/19 9:12
     */
    private void configureJsonPanel(ConsoleViewImpl consoleView) {
        CommonActionsManager commonActionsManager = CommonActionsManager.getInstance();
        AnAction prevAction = commonActionsManager.createPrevOccurenceAction(consoleView);
        prevAction.getTemplatePresentation().setText(consoleView.getPreviousOccurenceActionName());
        AnAction nextAction = commonActionsManager.createNextOccurenceAction(consoleView);
        nextAction.getTemplatePresentation().setText(consoleView.getNextOccurenceActionName());

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new ClearConsoleAction(consoleView));
        actionGroup.add(new CopyConsoleAction(consoleView));
        actionGroup.add(new CompressJsonConsoleAction(consoleView));
        actionGroup.add(new DecompressionJsonConsoleAction(consoleView));
        actionGroup.add(new DownloadJsonConsoleAction(consoleView));
        actionGroup.addSeparator();
        actionGroup.add(prevAction);
        actionGroup.add(nextAction);
        ToggleUseSoftWrapsToolbarAction toggleUseSoftWrapsToolbarAction = new ToggleUseSoftWrapsToolbarAction(SoftWrapAppliancePlaces.CONSOLE);
        toggleUseSoftWrapsToolbarAction.getTemplatePresentation().putClientProperty(ToggleUseSoftWrapsToolbarAction.SELECTED_PROPERTY, true);
        actionGroup.add(toggleUseSoftWrapsToolbarAction);
        actionGroup.add(ActionManager.getInstance().getAction("Print"));

        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar(this.getClass().getSimpleName() + "-json-actions", actionGroup, true);
        actionToolbar.setTargetComponent(consoleView);
        jsonToolBar.add(actionToolbar.getComponent());
        jsonPanel.add(consoleView.getComponent(), BorderLayout.CENTER);
    }

    /**
     * 配置左侧面板编辑器
     *
     * @param strEditor
     * @param project
     * @return void
     * @author mabin
     * @date 2024/1/18 18:16
     */
    private void configureStrEditor(Editor strEditor, Project project) {
        EditorSettings settings = strEditor.getSettings();
        settings.setLineNumbersShown(true);
        settings.setRefrainFromScrolling(false);
        settings.setLineMarkerAreaShown(true);
        settings.setUseSoftWraps(true);
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new ClearEditAction(strEditor, project));
        actionGroup.add(new CopyEditAction(strEditor));

        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar(this.getClass().getSimpleName() + "-str-actions", actionGroup, true);
        actionToolbar.setTargetComponent(strEditor.getComponent());
        strToolBar.add(actionToolbar.getComponent());
        JBScrollPane logScrollPane = new JBScrollPane(strEditor.getComponent());
        strPanel.add(logScrollPane, BorderLayout.CENTER);
        // 设置编辑器文本监听器
        Document document = strEditor.getDocument();
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                lengthLabel.setText("length: " + document.getTextLength() + StringUtils.SPACE);
                lineLabel.setText("line: " + document.getLineCount() + StringUtils.SPACE);
            }
        });
        CaretModel caretModel = strEditor.getCaretModel();
        caretModel.addCaretListener(new CaretListener() {
            @Override
            public void caretPositionChanged(@NotNull CaretEvent event) {
                Caret primaryCaret = caretModel.getPrimaryCaret();

                int cursorPosition = primaryCaret.getOffset();
                int cursorLine = document.getLineNumber(cursorPosition);
                int cursorCol = cursorPosition - document.getLineStartOffset(cursorLine);
                cursorLabel.setText("Ln: " + (cursorLine + 1) + StringUtils.SPACE + "Col: " + cursorCol + StringUtils.SPACE);

                SelectionModel selectionModel = strEditor.getSelectionModel();
                String selectedText = selectionModel.getSelectedText();
                if (StringUtils.isBlank(selectedText)) {
                    selectLabel.setText(StringUtils.EMPTY);
                    return;
                }
                int selectLine = document.getLineNumber(primaryCaret.getSelectionEnd()) - document.getLineNumber(primaryCaret.getSelectionStart()) + 1;
                selectLabel.setText("sel: " + selectedText.length() + StringUtils.SPACE + "|" + StringUtils.SPACE + selectLine);
            }
        });
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.rootPanel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{};
    }

    /**
     * 初始化UI
     *
     * @param
     * @return void
     * @author mabin
     * @date 2024/1/18 17:22
     */
    private void setupUI() {
        // 设置主面板
        rootPanel = new JPanel();
        JBInsets margin = JBUI.emptyInsets();
        rootPanel.setLayout(new GridLayoutManager(Constants.NUM.ONE, Constants.NUM.ONE, margin, -1, -1, false, false));
        Dimension dimension = new Dimension(800, 500);
        rootPanel.setMinimumSize(dimension);
        rootPanel.setPreferredSize(dimension);
        // 设置整个面板
        JPanel wholePanel = new JPanel();
        wholePanel.setLayout(new GridLayoutManager(Constants.NUM.ONE, Constants.NUM.THREE, margin, -1, -1, false, false));
        rootPanel.add(wholePanel, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        // 设置左侧面板
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayoutManager(1, 1, margin, -1, -1, false, false));
        wholePanel.add(leftPanel, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        // 设置左侧滚动面板
        JBScrollPane leftScrollPane = new JBScrollPane();
        leftScrollPane.setHorizontalScrollBarPolicy(30);
        leftScrollPane.setVerticalScrollBarPolicy(20);
        leftPanel.add(leftScrollPane, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        // 设置左侧滚动面板里的内容面板
        strPanel = new JPanel();
        strPanel.setLayout(new BorderLayout(Constants.NUM.ZERO, Constants.NUM.ZERO));
        Dimension strDimension = new Dimension(350, -1);
        strPanel.setMinimumSize(strDimension);
        strPanel.setPreferredSize(strDimension);
        leftScrollPane.setViewportView(strPanel);
        // 设置左侧工具栏
        strToolBar = new JPanel();
        strToolBar.setLayout(new BorderLayout(Constants.NUM.ZERO, Constants.NUM.ZERO));
        strPanel.add(strToolBar, SpringLayout.NORTH);
        // 设置左侧尾部统计标签
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        lengthLabel = new JBLabel();
        statusPanel.add(lengthLabel, BorderLayout.WEST);
        lineLabel = new JBLabel();
        statusPanel.add(lineLabel, BorderLayout.WEST);
        cursorLabel = new JBLabel();
        statusPanel.add(cursorLabel, BorderLayout.WEST);
        selectLabel = new JBLabel();
        statusPanel.add(selectLabel, BorderLayout.WEST);
        strPanel.add(statusPanel, BorderLayout.SOUTH);

        // 设置右侧面板吧
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayoutManager(1, 1, margin, -1, -1, false, false));
        wholePanel.add(rightPanel, new GridConstraints(0, 2, 1, 1, 0, 3, 3, 3, null, null, null));
        // 设置右侧滚动面板
        JBScrollPane rightScrollPane = new JBScrollPane();
        rightPanel.add(rightScrollPane, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        // 设置右侧滚动面板里的内容面板
        jsonPanel = new JPanel();
        jsonPanel.setLayout(new BorderLayout(Constants.NUM.ZERO, Constants.NUM.ZERO));
        Dimension rightDimension = new Dimension(350, -1);
        jsonPanel.setMinimumSize(rightDimension);
        jsonPanel.setPreferredSize(rightDimension);
        rightScrollPane.setViewportView(jsonPanel);
        // 设置右侧工具栏
        jsonToolBar = new JPanel();
        jsonToolBar.setLayout(new BorderLayout(Constants.NUM.ZERO, Constants.NUM.ZERO));
        jsonPanel.add(jsonToolBar, SpringLayout.NORTH);
        // 设置居中面板
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        centerPanel.setAutoscrolls(false);
        wholePanel.add(centerPanel, new GridConstraints(0, 1, 1, 1, 0, 3, 0, 3, new Dimension(60, -1), new Dimension(60, -1), new Dimension(60, -1)));
        // 设置功能按钮
        convertButton = new JButton(BundleUtil.getI18n("global.button.convert.text"));
        convertButton.setHorizontalTextPosition(Constants.NUM.ZERO);
        Dimension buttonDimension = new Dimension(58, 38);
        convertButton.setMaximumSize(buttonDimension);
        convertButton.setMinimumSize(buttonDimension);
        convertButton.setPreferredSize(buttonDimension);
        centerPanel.add(convertButton);
        clearButton = new JButton(BundleUtil.getI18n("global.button.clear.text"));
        clearButton.setHorizontalTextPosition(Constants.NUM.ZERO);
        clearButton.setMaximumSize(buttonDimension);
        clearButton.setMinimumSize(buttonDimension);
        clearButton.setPreferredSize(buttonDimension);
        clearButton.setInheritsPopupMenu(false);
        clearButton.setOpaque(false);
        clearButton.setRequestFocusEnabled(true);
        clearButton.setRolloverEnabled(true);
        clearButton.setVerifyInputWhenFocusTarget(true);
        clearButton.setVerticalTextPosition(0);
        centerPanel.add(clearButton);
    }

}

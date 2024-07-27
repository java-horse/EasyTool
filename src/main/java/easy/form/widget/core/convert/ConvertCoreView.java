package easy.form.widget.core.convert;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.CommonActionsManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.util.Key;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import easy.action.base.*;
import easy.base.Constants;
import easy.handler.Str2JsonHandler;
import easy.mybatis.log.format.SqlHelper;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @project: EasyTool
 * @package: easy.json.str2Json
 * @author: mabin
 * @date: 2024/01/18 17:10:40
 */
public class ConvertCoreView extends CoreCommonView {
    private JPanel panel;
    private JPanel leftPanel;
    private JPanel leftToolBar;
    private JPanel rightPanel;
    private JPanel rightToolBar;
    private JBLabel lengthLabel;
    private JBLabel lineLabel;
    private JBLabel cursorLabel;
    private JBLabel selectLabel;
    private JButton str2JsonButton;
    private JButton log2SqlButton;
    private JButton clearButton;

    private final Editor strEditor;

    public ConvertCoreView() {
        setupUI();
        Project project = ProjectManagerEx.getInstanceEx().getDefaultProject();
        // 配置左侧面板编辑器
        EditorFactory editorFactory = EditorFactory.getInstance();
        strEditor = editorFactory.createEditor(editorFactory.createDocument(StringUtils.EMPTY),
                project, PlainTextLanguage.INSTANCE.getAssociatedFileType(), false);
        configureLeftEditor(strEditor, project);
        // 配置右侧面板
        ConsoleViewImpl consoleView = new ConsoleViewImpl(project, true);
        configureRightPanel(consoleView);
        // 设置按钮监听器
        str2JsonButton.addActionListener(e -> {
            String text = strEditor.getDocument().getText();
            if (StringUtils.isBlank(text)) {
                MessageUtil.showWarningDialog("请输入要转换的文本");
                return;
            }
            String json = Str2JsonHandler.toJSON(text);
            if (StringUtils.isBlank(json)) {
                return;
            }
            consoleView.clear();
            consoleView.print(json, ConsoleViewContentType.USER_INPUT);
        });
        log2SqlButton.addActionListener(e -> {
            String text = strEditor.getDocument().getText();
            if (StringUtils.isBlank(text)) {
                MessageUtil.showWarningDialog("请输入要转换的文本");
                return;
            }
            for (String rowLog : text.split(StringUtils.LF)) {
                SqlHelper.parseSqlByRow(rowLog, consoleView);
            }
        });
        clearButton.addActionListener(e -> {
            Document document = strEditor.getDocument();
            if (StringUtils.isNotBlank(document.getText())) {
                WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(0, document.getTextLength(), StringUtils.EMPTY));
            }
            if (StringUtils.isNotBlank(consoleView.getText())) {
                consoleView.clear();
            }
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
    private void configureRightPanel(ConsoleViewImpl consoleView) {
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
        Presentation presentation = toggleUseSoftWrapsToolbarAction.getTemplatePresentation();
        presentation.putClientProperty(Key.create("selected"), Boolean.TRUE);
        actionGroup.add(toggleUseSoftWrapsToolbarAction);
        actionGroup.add(new ScrollToTheEndToolbarAction(strEditor));
        actionGroup.add(ActionManager.getInstance().getAction("Print"));

        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar(this.getClass().getSimpleName() + "-convert-actions", actionGroup, true);
        actionToolbar.setTargetComponent(consoleView);
        rightToolBar.add(actionToolbar.getComponent());
        rightPanel.add(consoleView.getComponent(), BorderLayout.CENTER);
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
    private void configureLeftEditor(Editor strEditor, Project project) {
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
        leftToolBar.add(actionToolbar.getComponent());
        JBScrollPane logScrollPane = new JBScrollPane(strEditor.getComponent());
        leftPanel.add(logScrollPane, BorderLayout.CENTER);
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
        panel = new JPanel();
        JBInsets margin = JBUI.emptyInsets();
        panel.setLayout(new GridLayoutManager(Constants.NUM.ONE, Constants.NUM.ONE, margin, -1, -1, false, false));
        Dimension dimension = new Dimension(800, 500);
        panel.setMinimumSize(dimension);
        panel.setPreferredSize(dimension);
        // 设置整个面板
        JPanel wholePanel = new JPanel();
        wholePanel.setLayout(new GridLayoutManager(Constants.NUM.ONE, Constants.NUM.THREE, margin, -1, -1, false, false));
        panel.add(wholePanel, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        // 设置左侧面板
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayoutManager(1, 1, margin, -1, -1, false, false));
        wholePanel.add(leftPanel, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        // 设置左侧滚动面板
        JBScrollPane leftScrollPane = new JBScrollPane();
        leftScrollPane.setHorizontalScrollBarPolicy(30);
        leftScrollPane.setVerticalScrollBarPolicy(20);
        leftPanel.add(leftScrollPane, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        // 设置左侧滚动面板里的内容面板
        leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout(Constants.NUM.ZERO, Constants.NUM.ZERO));
        Dimension strDimension = new Dimension(350, -1);
        leftPanel.setMinimumSize(strDimension);
        leftPanel.setPreferredSize(strDimension);
        leftScrollPane.setViewportView(leftPanel);
        // 设置左侧工具栏
        leftToolBar = new JPanel();
        leftToolBar.setLayout(new BorderLayout(Constants.NUM.ZERO, Constants.NUM.ZERO));
        leftPanel.add(leftToolBar, SpringLayout.NORTH);
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
        leftPanel.add(statusPanel, BorderLayout.SOUTH);

        // 设置右侧面板吧
        rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayoutManager(1, 1, margin, -1, -1, false, false));
        wholePanel.add(rightPanel, new GridConstraints(0, 2, 1, 1, 0, 3, 3, 3, null, null, null));
        // 设置右侧滚动面板
        JBScrollPane rightScrollPane = new JBScrollPane();
        rightPanel.add(rightScrollPane, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        // 设置右侧滚动面板里的内容面板
        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout(Constants.NUM.ZERO, Constants.NUM.ZERO));
        Dimension rightDimension = new Dimension(350, -1);
        rightPanel.setMinimumSize(rightDimension);
        rightPanel.setPreferredSize(rightDimension);
        rightScrollPane.setViewportView(rightPanel);
        // 设置右侧工具栏
        rightToolBar = new JPanel();
        rightToolBar.setLayout(new BorderLayout(Constants.NUM.ZERO, Constants.NUM.ZERO));
        rightPanel.add(rightToolBar, SpringLayout.NORTH);
        // 设置居中面板
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        centerPanel.setAutoscrolls(false);
        Dimension minimumSize = new Dimension(70, -1);
        wholePanel.add(centerPanel, new GridConstraints(0, 1, 1, 1, 0, 3, 0, 3, minimumSize, minimumSize, minimumSize));
        // 设置功能按钮
        Dimension buttonDimension = new Dimension(70, 30);
        str2JsonButton = new JButton("Str2Json");
        str2JsonButton.setMaximumSize(buttonDimension);
        str2JsonButton.setMinimumSize(buttonDimension);
        str2JsonButton.setPreferredSize(buttonDimension);
        centerPanel.add(str2JsonButton);
        log2SqlButton = new JButton("Log2Sql");
        log2SqlButton.setMaximumSize(buttonDimension);
        log2SqlButton.setMinimumSize(buttonDimension);
        log2SqlButton.setPreferredSize(buttonDimension);
        centerPanel.add(log2SqlButton);
        clearButton = new JButton("Clear");
        clearButton.setMaximumSize(buttonDimension);
        clearButton.setMinimumSize(buttonDimension);
        clearButton.setPreferredSize(buttonDimension);
        centerPanel.add(clearButton);
    }

    public JPanel getContent() {
        return this.panel;
    }

}

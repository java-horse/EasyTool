package easy.mybatis.log.ui;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.ide.CommonActionsManager;
import com.intellij.openapi.actionSystem.*;
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
import com.intellij.openapi.util.Key;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import easy.action.base.ClearConsoleAction;
import easy.action.base.ClearEditAction;
import easy.action.base.CopyConsoleAction;
import easy.action.base.CopyEditAction;
import easy.base.Constants;
import easy.mybatis.log.format.SqlHelper;
import easy.util.BundleUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * SQL格式化弹窗
 *
 * @project: EasyTool
 * @package: easy.mybatis.log.ui
 * @author: mabin
 * @date: 2024/01/18 09:36:50
 */
public class MyBatisLogFormatWrapper extends DialogWrapper {
    private JPanel rootPanel;
    private JPanel logPanel;
    private JPanel sqlPanel;
    private JPanel sqlPanelBar;
    private JPanel logToolBar;
    private JButton formatButton;
    private JButton clearAllButton;
    private JBLabel lengthLabel;
    private JBLabel lineLabel;
    private JBLabel cursorLabel;
    private JBLabel selectLabel;

    public MyBatisLogFormatWrapper(@NotNull Project project) {
        super(project);
        setupUI();
        init();
        setTitle(Constants.PLUGIN_NAME + " Log To SQL");
        EditorFactory factory = EditorFactory.getInstance();
        Editor logEditor = factory.createEditor(factory.createDocument(StringUtils.EMPTY), project, PlainTextLanguage.INSTANCE.getAssociatedFileType(), false);
        // 配置日志编辑器
        configureLogEditor(logEditor, project);
        // 配置SQL面板
        ConsoleViewImpl consoleView = new ConsoleViewImpl(project, true);
        configureSqlPanel(consoleView);

        Dimension size = DimensionService.getInstance().getSize("#" + this.getClass().getName(), project);
        if (Objects.nonNull(size)) {
            rootPanel.setPreferredSize(size);
        } else {
            setSize(1000, 800);
        }
        // 设置按钮监听器
        formatButton.addActionListener(e -> {
            String text = logEditor.getDocument().getText();
            String[] rowLogs = text.split(StringUtils.LF);
            for (String rowLog : rowLogs) {
                SqlHelper.parseSqlByRow(rowLog, consoleView);
            }
        });
        clearAllButton.addActionListener(e -> {
            Document document = logEditor.getDocument();
            WriteCommandAction.runWriteCommandAction(project, () -> document.replaceString(0, document.getTextLength(), StringUtils.EMPTY));
            consoleView.clear();
        });
    }

    /**
     * 创建SQL日志编辑器
     *
     * @param logEditor
     * @param project
     * @return void
     * @author mabin
     * @date 2024/1/18 11:12
     */
    private void configureLogEditor(Editor logEditor, Project project) {
        EditorSettings settings = logEditor.getSettings();
        settings.setLineNumbersShown(true);
        settings.setRefrainFromScrolling(false);
        settings.setLineMarkerAreaShown(true);
        settings.setUseSoftWraps(true);
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new ClearEditAction(logEditor, project));
        actionGroup.add(new CopyEditAction(logEditor));
        ActionToolbar actionToolbar = createActionToolbar(this.getClass().getSimpleName() + "-sql-log-actions", actionGroup, true);
        actionToolbar.setTargetComponent(logEditor.getComponent());
        logToolBar.add(actionToolbar.getComponent());
        JBScrollPane logScrollPane = new JBScrollPane(logEditor.getComponent());
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        // 设置编辑器文本监听器
        Document document = logEditor.getDocument();
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                lengthLabel.setText("length: " + document.getTextLength() + StringUtils.SPACE);
                lineLabel.setText("line: " + document.getLineCount() + StringUtils.SPACE);
            }
        });
        CaretModel caretModel = logEditor.getCaretModel();
        caretModel.addCaretListener(new CaretListener() {
            @Override
            public void caretPositionChanged(@NotNull CaretEvent event) {
                Caret primaryCaret = caretModel.getPrimaryCaret();
                int cursorPosition = primaryCaret.getOffset();
                int cursorLine = document.getLineNumber(cursorPosition);
                int cursorCol = cursorPosition - document.getLineStartOffset(cursorLine);
                cursorLabel.setText("Ln: " + (cursorLine + 1) + StringUtils.SPACE + "Col: " + cursorCol + StringUtils.SPACE);

                SelectionModel selectionModel = logEditor.getSelectionModel();
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
     * 创建SQL面板
     *
     * @param consoleView
     * @return void
     * @author mabin
     * @date 2024/1/18 11:12
     */
    private void configureSqlPanel(ConsoleViewImpl consoleView) {
        CommonActionsManager actionsManager = CommonActionsManager.getInstance();
        AnAction prevAction = actionsManager.createPrevOccurenceAction(consoleView);
        prevAction.getTemplatePresentation().setText(consoleView.getPreviousOccurenceActionName());
        AnAction nextAction = actionsManager.createNextOccurenceAction(consoleView);
        nextAction.getTemplatePresentation().setText(consoleView.getNextOccurenceActionName());

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new ClearConsoleAction(consoleView));
        actionGroup.add(new CopyConsoleAction(consoleView));
        actionGroup.addSeparator();
        actionGroup.add(prevAction);
        actionGroup.add(nextAction);
        ToggleUseSoftWrapsToolbarAction toggleUseSoftWrapsToolbarAction = new ToggleUseSoftWrapsToolbarAction(SoftWrapAppliancePlaces.CONSOLE);
        Presentation presentation = toggleUseSoftWrapsToolbarAction.getTemplatePresentation();
        presentation.putClientProperty(Key.create("selected"), Boolean.TRUE);
        actionGroup.add(toggleUseSoftWrapsToolbarAction);
        actionGroup.add(ActionManager.getInstance().getAction("Print"));
        ActionToolbar sqlConsoleToolBar = createActionToolbar(this.getClass().getSimpleName() + "-sql-console-actions", actionGroup, true);
        sqlConsoleToolBar.setTargetComponent(consoleView);
        sqlPanelBar.add(sqlConsoleToolBar.getComponent());
        sqlPanel.add(consoleView.getComponent(), BorderLayout.CENTER);
    }

    private static ActionToolbar createActionToolbar(String place, DefaultActionGroup group, boolean horizontal) {
        return ActionManager.getInstance().createActionToolbar(place, group, horizontal);
    }

    /**
     * 设置弹窗UI布局
     *
     * @param
     * @return void
     * @author mabin
     * @date 2024/1/18 11:11
     */
    private void setupUI() {
        JPanel var1 = new JPanel();
        this.rootPanel = var1;
        var1.setLayout(new GridLayoutManager(1, 1, JBUI.emptyInsets(), -1, -1, false, false));
        var1.setMinimumSize(new Dimension(800, 500));
        var1.setPreferredSize(new Dimension(800, 500));
        JPanel var2 = new JPanel();
        var2.setLayout(new GridLayoutManager(1, 3, JBUI.emptyInsets(), -1, -1, false, false));
        var1.add(var2, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        JPanel var3 = new JPanel();
        var3.setLayout(new GridLayoutManager(1, 1, JBUI.emptyInsets(), -1, -1, false, false));
        var2.add(var3, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        JBScrollPane var4 = new JBScrollPane();
        var4.setHorizontalScrollBarPolicy(30);
        var4.setVerticalScrollBarPolicy(20);
        var3.add(var4, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        JPanel var5 = new JPanel();
        this.logPanel = var5;
        var5.setLayout(new BorderLayout(0, 0));
        var5.setMinimumSize(new Dimension(350, -1));
        var5.setPreferredSize(new Dimension(350, -1));
        var4.setViewportView(var5);
        JPanel var6 = new JPanel();
        this.logToolBar = var6;
        var6.setLayout(new BorderLayout(0, 0));
        var5.add(var6, SpringLayout.NORTH);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        lengthLabel = new JBLabel();
        statusPanel.add(lengthLabel, BorderLayout.WEST);
        lineLabel = new JBLabel();
        statusPanel.add(lineLabel, BorderLayout.WEST);
        cursorLabel = new JBLabel();
        statusPanel.add(cursorLabel, BorderLayout.WEST);
        selectLabel = new JBLabel();
        statusPanel.add(selectLabel, BorderLayout.WEST);
        logPanel.add(statusPanel, BorderLayout.SOUTH);
        JPanel var7 = new JPanel();
        var7.setLayout(new GridLayoutManager(1, 1, JBUI.emptyInsets(), -1, -1, false, false));
        var2.add(var7, new GridConstraints(0, 2, 1, 1, 0, 3, 3, 3, null, null, null));
        JBScrollPane var8 = new JBScrollPane();
        var7.add(var8, new GridConstraints(0, 0, 1, 1, 0, 3, 7, 7, null, null, null));
        JPanel var9 = new JPanel();
        this.sqlPanel = var9;
        var9.setLayout(new BorderLayout(0, 0));
        var9.setMinimumSize(new Dimension(350, -1));
        var9.setPreferredSize(new Dimension(350, -1));
        var8.setViewportView(var9);
        JPanel var10 = new JPanel();
        this.sqlPanelBar = var10;
        var10.setLayout(new BorderLayout(0, 0));
        var9.add(var10, SpringLayout.NORTH);
        JPanel var11 = new JPanel();
        var11.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        var11.setAutoscrolls(false);
        var2.add(var11, new GridConstraints(0, 1, 1, 1, 0, 3, 0, 3, new Dimension(60, -1), new Dimension(60, -1), new Dimension(60, -1)));
        JButton var12 = new JButton();
        this.formatButton = var12;
        var12.setHorizontalTextPosition(0);
        var12.setMaximumSize(new Dimension(58, 38));
        var12.setMinimumSize(new Dimension(58, 38));
        var12.setPreferredSize(new Dimension(58, 38));
        var12.setText(BundleUtil.getI18n("global.button.format.text"));
        var11.add(var12);
        JButton var13 = new JButton();
        this.clearAllButton = var13;
        var13.setHideActionText(false);
        var13.setHorizontalTextPosition(0);
        var13.setInheritsPopupMenu(false);
        var13.setMaximumSize(new Dimension(58, 38));
        var13.setMinimumSize(new Dimension(58, 38));
        var13.setOpaque(false);
        var13.setPreferredSize(new Dimension(58, 38));
        var13.setRequestFocusEnabled(true);
        var13.setRolloverEnabled(true);
        var13.setText(BundleUtil.getI18n("global.button.clear.text"));
        var13.setVerifyInputWhenFocusTarget(true);
        var13.setVerticalTextPosition(0);
        var11.add(var13);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.rootPanel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{};
    }

}

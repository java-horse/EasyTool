package easy.action.mybatis.log.console;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import easy.base.Constants;
import easy.icons.EasyIcons;
import easy.mybatis.log.MyBatisLogConsoleFilter;
import easy.mybatis.log.ui.MyBatisLogManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MyBatisLogAction extends DumbAwareAction {

    public MyBatisLogAction() {
        super("Mybatis Log", Constants.PLUGIN_NAME + "Mybatis Log", EasyIcons.ICON.MYBATIS);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(project) || Objects.isNull(editor)) {
            return;
        }
        if (!project.isOpen() || !project.isInitialized()) {
            return;
        }
        String selectedText = editor.getSelectionModel().getSelectedText();
        if ("EditorPopup".equals(e.getPlace())) {
            MyBatisLogManager manager = MyBatisLogManager.getInstance(project);
            if (Objects.nonNull(manager) && manager.getToolWindow().isAvailable()) {
                if (!manager.isRunning()) {
                    manager.run();
                }
                manager.getToolWindow().activate(null);
                parseSQLSelectedText(selectedText, project);
                return;
            }
        }
        rerun(project);
        parseSQLSelectedText(selectedText, project);
    }

    /**
     * 解析复制SQL文本
     *
     * @param selectedText
     * @param project
     * @return void
     * @author mabin
     * @date 2024/1/10 10:17
     */
    private void parseSQLSelectedText(String selectedText, Project project) {
        MyBatisLogManager manager = MyBatisLogManager.getInstance(project);
        if (StringUtils.isBlank(selectedText) || Objects.isNull(manager)) {
            return;
        }
        if (!StringUtils.containsIgnoreCase(selectedText, manager.getPreparing()) || !StringUtils.containsIgnoreCase(selectedText, manager.getParameters())) {
            return;
        }
        String[] selectedRowText = StringUtils.split(selectedText, StringUtils.LF);
        if (selectedRowText.length < Constants.NUM.TWO) {
            return;
        }
        MyBatisLogConsoleFilter consoleFilter = new MyBatisLogConsoleFilter(project);
        for (String row : selectedRowText) {
            consoleFilter.applyFilter(row, row.length());
        }
    }

    public void rerun(Project project) {
        MyBatisLogManager manager = MyBatisLogManager.getInstance(project);
        if (Objects.nonNull(manager)) {
            Disposer.dispose(manager);
        }
        MyBatisLogManager.createInstance(project).run();
    }

}

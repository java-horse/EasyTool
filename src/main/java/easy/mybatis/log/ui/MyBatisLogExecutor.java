package easy.mybatis.log.ui;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import easy.base.Constants;
import easy.icons.EasyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class MyBatisLogExecutor extends Executor {

    public static final String TOOL_WINDOW_ID = Constants.PLUGIN_NAME + " MyBatis Log";

    @Override
    public @NotNull String getToolWindowId() {
        return TOOL_WINDOW_ID;
    }

    @Override
    public @NotNull Icon getToolWindowIcon() {
        return getIcon();
    }

    @Override
    public @NotNull Icon getIcon() {
        return EasyIcons.ICON.MYBATIS;
    }

    @Override
    public Icon getDisabledIcon() {
        return getIcon();
    }

    @Override
    public String getDescription() {
        return "MyBatis Log";
    }

    @NotNull
    @Override
    public String getActionName() {
        return getDescription();
    }

    @NotNull
    @Override
    public String getId() {
        return TOOL_WINDOW_ID;
    }

    @NotNull
    @Override
    public String getStartActionText() {
        return getDescription();
    }

    @Override
    public String getContextActionId() {
        return getDescription();
    }

    @Override
    public String getHelpId() {
        return TOOL_WINDOW_ID;
    }

    public static Executor getInstance() {
        return ExecutorRegistry.getInstance().getExecutorById(TOOL_WINDOW_ID);
    }

}
package easy.action;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import easy.handler.ConvertHandler;
import easy.listener.AppActiveListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * EasyChar中文字符实时替换Action
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/04/24 10:31
 **/

public class ConvertAction extends AnAction {

    /**
     * 初始化处理
     *
     * @param
     * @return null
     * @author mabin
     * @date 2023/9/2 14:19
     **/
    public ConvertAction() {
        super();
        TypedAction typedAction = TypedAction.getInstance();
        typedAction.setupRawHandler(new ConvertHandler(typedAction.getRawHandler()));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}


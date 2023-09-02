package easy.action;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import easy.handler.ConvertHandler;
import easy.listener.AppActiveListener;
import org.jetbrains.annotations.NotNull;

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

        // 设置转换处理
        TypedAction typedAction = TypedAction.getInstance();
        typedAction.setupRawHandler(new ConvertHandler(typedAction.getRawHandler()));

        // 设置消息监听
        AppActiveListener appActiveListener = new AppActiveListener();
        Application application = ApplicationManager.getApplication();
        Disposable disposable = Disposer.newDisposable();
        Disposer.register(application, disposable);
        MessageBusConnection connect = application.getMessageBus().connect(disposable);
        connect.subscribe(ApplicationActivationListener.TOPIC, appActiveListener);
        appActiveListener.activate();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

}


package easy.init;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import easy.action.ConvertAction;
import easy.config.emoji.GitEmojiConfig;
import easy.config.emoji.GitEmojiConfigComponent;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.git.emoji.convert.EmojiConverter;
import easy.listener.AppActiveListener;
import easy.listener.TabHighlighterFileEditorListener;
import easy.service.TranslateService;
import javassist.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Tab标签高亮初始化
 *
 * @project: EasyTool
 * @package: easy.init
 * @author: mabin
 * @date: 2023/12/20 11:24:00
 */
public class EasyToolApplicationInit implements StartupActivity, DumbAware {

    @Override
    public void runActivity(@NotNull Project project) {
        Application application = ApplicationManager.getApplication();
        notifyInit(application);
        translateServiceInit(application);
        convertInit(application);
        activeTabHighlightInit(project, application);
        gitEmojiCommitLogInit(application);
        System.out.println("6666666666");
    }

    /**
     * 消息监听器初始化
     *
     * @param application
     * @return void
     * @author mabin
     * @date 2024/1/2 9:37
     */
    private void notifyInit(@NotNull Application application) {
        AppActiveListener appActiveListener = new AppActiveListener();
        Disposable disposable = Disposer.newDisposable();
        Disposer.register(application, disposable);
        MessageBusConnection connect = application.getMessageBus().connect(disposable);
        connect.subscribe(ApplicationActivationListener.TOPIC, appActiveListener);
        appActiveListener.activate();
    }

    /**
     * 翻译引擎服务初始化
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/9/4 21:21
     **/
    private void translateServiceInit(@NotNull Application application) {
        TranslateConfig translateConfig = application.getService(TranslateConfigComponent.class).getState();
        TranslateService translateService = application.getService(TranslateService.class);
        if (Objects.isNull(translateService) || Objects.isNull(translateConfig)) {
            return;
        }
        translateService.init(translateConfig);
    }

    /**
     * 中英字符映射功能初始化
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/12/20 14:07
     */
    private void convertInit(@NotNull Application application) {
        application.invokeLater(ConvertAction::new);
    }

    /**
     * tab标签高亮功能初始化
     *
     * @param project
     * @param application
     * @return void
     * @author mabin
     * @date 2023/12/20 14:10
     */
    private void activeTabHighlightInit(@NotNull Project project, @NotNull Application application) {
        MessageBus messageBus = application.getMessageBus();
        MessageBusConnection messageBusConnection = messageBus.connect();
        TabHighlighterFileEditorListener tabHighlighterFileEditorListener = new TabHighlighterFileEditorListener(project);
        messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, tabHighlighterFileEditorListener);
        messageBusConnection.subscribe(TabHighlighterFileEditorListener.CHANGE_HIGHLIGHTER_TOPIC, tabHighlighterFileEditorListener);
    }

    private void gitEmojiCommitLogInit(@NotNull Application application) {
        System.out.println("yyy");
        application.getMessageBus().connect().subscribe(AppLifecycleListener.TOPIC, new AppLifecycleListener() {
            @Override
            public void appFrameCreated(@NotNull List<String> commandLineArgs) {
                // GitEmojiConfig gitEmojiConfig = ApplicationManager.getApplication().getService(GitEmojiConfigComponent.class).getState();
                // if (Objects.isNull(gitEmojiConfig) || Boolean.FALSE.equals(gitEmojiConfig.getRenderCommitLogCheckBox())) {
                //     return;
                // }
                System.out.println("xxx");
                // 初始化ClassPool，用于操作字节码
                ClassPool classPool = ClassPool.getDefault();
                // 添加EmojiConverter类到ClassPool中以便查找和修改
                classPool.insertClassPath(new ClassClassPath(EmojiConverter.class));
                // 获取GraphCommitCell类的字节码表示
                try {
                    CtClass ctClass = classPool.get("com.intellij.vcs.log.ui.render.GraphCommitCell");
                    // 若找到该类，则进行解冻并继续操作
                    if (ctClass != null) {
                        ctClass.defrost();
                        // 获取EmojiConverter类中的convert方法的字节码表示
                        CtMethod converterMethod = classPool.get("easy.git.emoji.convert.EmojiConverter").getDeclaredMethod("convert");
                        // 将convert方法复制并添加到GraphCommitCell类中
                        ctClass.addMethod(CtNewMethod.copy(converterMethod, ctClass, null));
                        // 获取GraphCommitCell类的构造方法字节码表示
                        CtConstructor constructor = ctClass.getConstructor("(Ljava/lang/String;Ljava/util/Collection;Ljava/util/Collection;)V");
                        // 若找到构造方法，则在构造方法执行前插入将commit message通过convert方法转换为emoji格式的代码
                        if (constructor != null) {
                            // 使用Javassist语法将第一个参数（commit message）转换为emoji
                            constructor.insertBefore("\\$1 = convert(\\$1);");
                            try {
                                // 将修改后的字节码转换回Java类
                                ctClass.toClass();
                            } catch (CannotCompileException e) {
                                // 处理编译异常
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}

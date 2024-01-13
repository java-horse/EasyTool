package easy.git.emoji.convert;

import com.intellij.ide.AppLifecycleListener;
import javassist.*;

import java.util.List;

public class EmojiCommitLogALL implements AppLifecycleListener {
    // private GitEmojiConfig gitEmojiConfig = ApplicationManager.getApplication().getService(GitEmojiConfigComponent.class).getState();

    @Override
    public void appFrameCreated(List<String> commandLineArgs) {
        // 检查配置是否允许渲染带emoji的提交日志，若不允许则直接返回
        // if (Boolean.FALSE.equals(gitEmojiConfig.getRenderCommitLogCheckBox())) {
        //     return;
        // }
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
                    constructor.insertBefore("{ $_ = $0.convert($_); }"); // 使用Javassist语法将第一个参数（commit message）转换为emoji
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

}


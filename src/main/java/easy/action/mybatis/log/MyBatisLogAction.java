package easy.action.mybatis.log;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import easy.base.Constants;
import easy.icons.EasyIcons;
import easy.mybatis.log.ui.MyBatisLogManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MyBatisLogAction extends DumbAwareAction {

    public MyBatisLogAction() {
        super("Mybatis Log", Constants.PLUGIN_NAME + "Mybatis Log", EasyIcons.ICON.MYBATIS);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        if (!project.isOpen() || !project.isInitialized()) {
            return;
        }
        if ("EditorPopup".equals(e.getPlace())) {
            final MyBatisLogManager manager = MyBatisLogManager.getInstance(project);
            if (Objects.nonNull(manager) && manager.getToolWindow().isAvailable()) {
                if (!manager.isRunning()) {
                    manager.run();
                }
                manager.getToolWindow().activate(null);
                return;
            }
        }

        rerun(project);
    }

    public void rerun(final Project project) {
        final MyBatisLogManager manager = MyBatisLogManager.getInstance(project);
        if (Objects.nonNull(manager)) {
            Disposer.dispose(manager);
        }
        MyBatisLogManager.createInstance(project).run();
    }

}

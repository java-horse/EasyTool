package easy.action.mybatis.log;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import easy.form.MybatisLogSettingView;
import easy.mybatis.log.ui.MyBatisLogManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class SettingsAction extends AnAction {
    private final MyBatisLogManager manager;

    public SettingsAction(MyBatisLogManager manager) {
        super("Settings", "Settings", AllIcons.General.GearPlain);
        this.manager = manager;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (Objects.isNull(e.getProject())) {
            return;
        }
        new MybatisLogSettingView(e.getProject(), manager).showAndGet();
    }

}

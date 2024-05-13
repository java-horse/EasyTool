package easy.action.api;

import com.google.common.collect.Maps;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import easy.config.api.YApiConfig;
import easy.config.api.YApiConfigComponent;
import easy.enums.ApiDocTypeEnum;
import easy.handler.ServiceHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class ApiDocDynamicActionGroup extends DefaultActionGroup {

    private final YApiConfig yApiConfig = ServiceHelper.getService(YApiConfigComponent.class).getState();
    private static final Map<String, AnAction> ACTION_MAP = Maps.newLinkedHashMap();

    static {
        ACTION_MAP.put(ApiDocTypeEnum.SETTING.getTitle(), ApiDocTypeEnum.SETTING.getAction());
    }

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        return ACTION_MAP.values().toArray(new AnAction[0]);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Module module = e.getData(LangDataKeys.MODULE);
        if (Objects.isNull(project) || Objects.isNull(module)) {
            e.getPresentation().setVisible(false);
            return;
        }
        // 动态设置Action
        if (Objects.nonNull(yApiConfig)) {
            if (Boolean.TRUE.equals(yApiConfig.getApiEnable())) {
                ACTION_MAP.putIfAbsent(ApiDocTypeEnum.YAPI.getTitle(), ApiDocTypeEnum.YAPI.getAction());
            } else {
                ACTION_MAP.remove(ApiDocTypeEnum.YAPI.getTitle());
            }
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}

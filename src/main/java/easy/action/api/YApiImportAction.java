package easy.action.api;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import easy.api.model.yapi.YApiTableDTO;
import easy.api.sdk.yapi.YApiClient;
import easy.api.sdk.yapi.model.Response;
import easy.config.api.YApiConfig;
import easy.config.api.YApiConfigComponent;
import easy.enums.ApiDocTypeEnum;
import easy.form.api.YApiImportView;
import easy.helper.ServiceHelper;
import easy.util.EasyCommonUtil;
import easy.util.NotifyUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class YApiImportAction extends AnAction {

    private YApiConfig yApiConfig = ServiceHelper.getService(YApiConfigComponent.class).getState();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(project) || Objects.isNull(editor)) {
            return;
        }
        YApiTableDTO apiTableDTO = getValidYApiTableDTO();
        if (Objects.isNull(apiTableDTO) || StringUtils.isBlank(apiTableDTO.getProjectToken())) {
            NotifyUtil.notify(String.format("请先配置[%s]服务的项目Token信息", ApiDocTypeEnum.YAPI.getTitle()),
                    NotificationType.WARNING, EasyCommonUtil.getPluginSettingAction(project));
            return;
        }
        // 加载YApiImportView弹窗
        YApiClient apiClient = new YApiClient(yApiConfig.getApiServerUrl(), apiTableDTO.getProjectToken());
        YApiImportView apiImportView = new YApiImportView();
        if (apiImportView.showAndGet() && Objects.nonNull(apiImportView.getImportDataRequest())) {
            Response<?> response = apiClient.importApiInterface(apiImportView.getImportDataRequest());
            if (response.isSuccess()) {
                String projectUrl = apiClient.genProjectUrl(Integer.parseInt(apiTableDTO.getProjectId()));
                NotifyUtil.notify(String.format("%s, </br><a href=\"%s\">%s</a>", response.getErrorMessage(), projectUrl, projectUrl));
                return;
            }
            NotifyUtil.notify(String.format("%s", response.getErrorMessage()), NotificationType.ERROR);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setText(ApiDocTypeEnum.YAPI_IMPORT.getTitle());
        e.getPresentation().setIcon(ApiDocTypeEnum.YAPI_IMPORT.getIcon());
        super.update(e);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

    /**
     * 获取有效的YApi项目配置
     *
     * @return {@link easy.api.model.yapi.YApiTableDTO}
     * @author mabin
     * @date 2024/05/11 16:29
     */
    private YApiTableDTO getValidYApiTableDTO() {
        if (Objects.isNull(yApiConfig) || MapUtils.isEmpty(yApiConfig.getYapiTableMap())) {
            return null;
        }
        return yApiConfig.getYapiTableMap().values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getSelect()))
                .findFirst().orElse(null);
    }

}

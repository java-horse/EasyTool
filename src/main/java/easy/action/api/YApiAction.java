package easy.action.api;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import easy.api.model.common.Api;
import easy.api.model.yapi.YApiTableDTO;
import easy.api.process.yapi.YapiUploader;
import easy.api.sdk.yapi.YApiClient;
import easy.api.sdk.yapi.model.ApiInterface;
import easy.config.api.YApiConfig;
import easy.config.api.YApiConfigComponent;
import easy.enums.ApiDocTypeEnum;
import easy.helper.ServiceHelper;
import easy.util.EasyCommonUtil;
import easy.util.NotifyUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class YApiAction extends AbstractAction {

    private YApiConfig yApiConfig = ServiceHelper.getService(YApiConfigComponent.class).getState();

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setText(ApiDocTypeEnum.YAPI.getTitle());
        presentation.setIcon(ApiDocTypeEnum.YAPI.getIcon());
    }

    /**
     * ApiDoc前置检查处理
     *
     * @param event 事件
     * @return boolean
     * @author mabin
     * @date 2024/05/11 14:55
     */
    @Override
    public boolean before(AnActionEvent event) {
        if (Objects.isNull(yApiConfig) || StringUtils.isBlank(yApiConfig.getApiServerUrl())) {
            NotifyUtil.notify(String.format("请先完善[%s]基础信息", event.getPresentation().getText()),
                    NotificationType.WARNING, EasyCommonUtil.getPluginSettingAction(event.getProject()));
            return false;
        }
        if (!Boolean.TRUE.equals(yApiConfig.getApiEnable())) {
            NotifyUtil.notify(String.format("请先启用[%s]功能服务", event.getPresentation().getText()),
                    NotificationType.WARNING, EasyCommonUtil.getPluginSettingAction(event.getProject()));
            return false;
        }
        YApiTableDTO apiTableDTO = getValidYApiTableDTO();
        if (Objects.isNull(apiTableDTO) || StringUtils.isBlank(apiTableDTO.getProjectToken())) {
            NotifyUtil.notify(String.format("请先配置[%s]服务的项目Token信息", event.getPresentation().getText()),
                    NotificationType.WARNING, EasyCommonUtil.getPluginSettingAction(event.getProject()));
            return false;
        }
        YApiClient apiClient = new YApiClient(yApiConfig.getApiServerUrl(), apiTableDTO.getProjectToken());
        return Objects.nonNull(apiClient.getProject());
    }

    /**
     * ApiDoc文档分发处理
     *
     * @param event 事件
     * @param apis  原料药
     * @author mabin
     * @date 2024/05/11 14:56
     */
    @Override
    public void handle(AnActionEvent event, List<Api> apis) {
        Project project = event.getData(CommonDataKeys.PROJECT);
        if (Objects.isNull(project)) {
            return;
        }
        YApiTableDTO apiTableDTO = getValidYApiTableDTO();
        if (Objects.isNull(apiTableDTO) || StringUtils.isBlank(apiTableDTO.getProjectToken())) {
            return;
        }
        YApiClient apiClient = new YApiClient(yApiConfig.getApiServerUrl(), apiTableDTO.getProjectToken());
        YapiUploader uploader = new YapiUploader(apiClient);
        int projectId = Integer.parseInt(apiTableDTO.getProjectId());
        super.handleUploadAsync(project, apis, event.getPresentation().getText(),
                api -> {
                    ApiInterface yapi = uploader.upload(projectId, api);
                    ApiUploadResult result = new ApiUploadResult();
                    result.setCategoryUrl(apiClient.genCatUrl(projectId, yapi.getCatId()));
                    if (yapi.getId() != null) {
                        result.setApiUrl(apiClient.genInterfaceUrl(projectId, yapi.getId()));
                    } else {
                        result.setApiUrl(result.getCategoryUrl());
                    }
                    return result;
                }, null);
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

package easy.restful.every;

import com.intellij.ide.actions.searcheverywhere.AbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.FoundItemDescriptor;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.util.Processor;
import easy.restful.api.ApiService;
import easy.restful.api.ApiServices;
import easy.restful.search.GotoRequestProvider;
import easy.restful.search.RestServiceItem;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class RestfulSearchGotoSEContributor extends AbstractGotoSEContributor {
    private final List<RestServiceItem> restServiceItemList = new ArrayList<>();
    private final Project project;

    public RestfulSearchGotoSEContributor(@NotNull AnActionEvent event) {
        super(event);
        this.project = event.getProject();
    }

    @Override
    protected @NotNull FilteringGotoByModel<?> createModel(@NotNull Project project) {
        return null;
    }


    @Override
    public boolean processSelectedItem(@NotNull Object selected, int modifiers, @NotNull String searchText) {
        return super.processSelectedItem(selected, modifiers, searchText);
    }

    @Override
    public Object getDataForItem(@NotNull Object element, @NotNull String dataId) {
        return null;
    }

    @Override
    public @NotNull String getSearchProviderId() {
        return RestfulSearchGotoSEContributor.class.getSimpleName();
    }

    @Override
    public boolean isEmptyPatternSupported() {
        return true;
    }

    @Override
    public boolean showInFindResults() {
        return false;
    }

    @Override
    public boolean isDumbAware() {
        return DumbService.isDumb(project);
    }

    @Override
    public void fetchWeightedElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super FoundItemDescriptor<Object>> consumer) {
        if (isDumbAware()) {
            return;
        }
        progressIndicator.start();
        if (CollectionUtils.isEmpty(restServiceItemList)) {
            try {
                Map<String, List<ApiService>> apiServiceMap = ApplicationManager.getApplication().runReadAction((ThrowableComputable<Map<String, List<ApiService>>, Throwable>)
                        () -> ApiServices.getApis(project));
                for (Map.Entry<String, List<ApiService>> entry : apiServiceMap.entrySet()) {
                    entry.getValue().forEach(apiService -> restServiceItemList.add(new RestServiceItem(apiService.getPsiElement(), apiService.getMethod(), apiService.getPath())));
                }
            } catch (Throwable e) {
            }
        }
        if (CollectionUtils.isEmpty(restServiceItemList)) {
            return;
        }
        restServiceItemList.sort(Comparator.comparing(RestServiceItem::getUrl));
        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + GotoRequestProvider.removeRedundancyMarkup(pattern) + "*", NameUtil.MatchingCaseSensitivity.NONE);
        for (RestServiceItem restServiceItem : restServiceItemList) {
            if (Objects.nonNull(restServiceItem.getName()) && matcher.matches(restServiceItem.getName())) {
                if (!consumer.process(new FoundItemDescriptor<>(restServiceItem, 0))) {
                    return;
                }
            }
        }
    }

    @Override
    public boolean isShownInSeparateTab() {
        return true;
    }

    @Override
    public @NotNull @Nls String getGroupName() {
        return "Api";
    }

    @Override
    public int getSortWeight() {
        return 999;
    }

}

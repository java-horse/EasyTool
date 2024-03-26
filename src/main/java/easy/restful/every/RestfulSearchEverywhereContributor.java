package easy.restful.every;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Restful search everywhere贡献者
 *
 * @author mabin
 * @project EasyTool
 * @package easy.restful.every
 * @date 2024/03/24 17:46
 */
public class RestfulSearchEverywhereContributor implements SearchEverywhereContributorFactory {
    @Override
    public @NotNull SearchEverywhereContributor createContributor(@NotNull AnActionEvent initEvent) {
        return new RestfulSearchGotoSEContributor(initEvent);
    }


}

package easy.restful.search;

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import easy.restful.api.HttpMethod;
import org.jetbrains.annotations.NotNull;

@State(name = "RestfulSearchConfiguration", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class RestfulSearchConfiguration extends ChooseByNameFilterConfiguration<HttpMethod> {

    public static RestfulSearchConfiguration getInstance(@NotNull Project project) {
        return project.getService(RestfulSearchConfiguration.class);
    }

    @Override
    protected String nameForElement(@NotNull HttpMethod type) {
        return type.name();
    }
}

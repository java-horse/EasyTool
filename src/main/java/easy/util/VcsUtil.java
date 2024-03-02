package easy.util;

import com.intellij.dvcs.repo.Repository;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class VcsUtil {


    private VcsUtil() {
    }

    public static String getCurrentBranch(Project project) {
        VcsRepositoryManager manager = VcsRepositoryManager.getInstance(project);
        Optional<Repository> firstRepository = manager.getRepositories().stream().findFirst();
        if (firstRepository.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return firstRepository.get().getCurrentBranchName();
    }

}

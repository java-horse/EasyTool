package easy.util;

import com.intellij.dvcs.repo.Repository;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.VcsUser;
import git4idea.GitUserRegistry;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class VcsUtil {


    private VcsUtil() {
    }

    /**
     * 获取当前分支
     *
     * @param project 项目
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/07/20 10:25
     */
    public static String getCurrentBranch(@NotNull Project project) {
        VcsRepositoryManager manager = VcsRepositoryManager.getInstance(project);
        Optional<Repository> firstRepository = manager.getRepositories().stream().findFirst();
        if (firstRepository.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return firstRepository.get().getCurrentBranchName();
    }

    /**
     * 获取VCS用户 (默认Git用户)
     *
     * @param project 项目
     * @return {@link com.intellij.vcs.log.VcsUser}
     * @author mabin
     * @date 2024/07/20 10:54
     */
    public static VcsUser getVcsUser(@NotNull Project project) {
        GitRepositoryManager gitRepositoryManager = GitRepositoryManager.getInstance(project);
        GitRepository gitRepository = gitRepositoryManager.getRepositories().stream().findFirst().orElse(null);
        if (Objects.nonNull(gitRepository)) {
            VcsUser vcsUser = GitUserRegistry.getInstance(project).getOrReadUser(gitRepository.getRoot());
            if (Objects.nonNull(vcsUser)) {
                return vcsUser;
            }
        }
        return null;
    }


}

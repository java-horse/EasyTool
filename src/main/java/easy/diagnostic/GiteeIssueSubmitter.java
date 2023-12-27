package easy.diagnostic;

import org.jetbrains.annotations.NotNull;

public class GiteeIssueSubmitter extends AbstractGiteeErrorReportSubmitter {

    /**
     * 设置用户名
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/25 15:02
     */
    @Override
    protected String getGiteeOwner() {
        return "milubin";
    }

    /**
     * 设置仓库名
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/25 15:03
     */
    @NotNull
    protected String getGiteeRepo() {
        return "easy-tool-plugin";
    }

    /**
     * 设置token访问令牌
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/25 15:03
     */
    @Override
    protected String getGiteeToken() {
        return "0f733ec73740318bfbd21ec4cf2c612d";
    }

}

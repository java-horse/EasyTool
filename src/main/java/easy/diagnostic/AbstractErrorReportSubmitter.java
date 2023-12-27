package easy.diagnostic;

import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.util.Consumer;
import easy.base.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public abstract class AbstractErrorReportSubmitter extends ErrorReportSubmitter {
    private static final Logger log = Logger.getInstance(AbstractErrorReportSubmitter.class);

    @Override
    @Nullable
    public String getPrivacyNoticeText() {
        return String.format("请在上报异常时, 在上方输入框内填入您的联系信息, 或 Issue 生成后<a href='%s'>点击进入</a>页面留言以获得 issue 进展通知<br/>"
                + "请在下方按钮选择 %s, 先不要 Clear all, 这样上报后可以点击弹窗最上方链接进入该 Issue", getIssueListPageUrl(), getReportActionText());
    }

    @Override
    public @NotNull String getReportActionText() {
        return "Report To " + Constants.PLUGIN_NAME;
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo, @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {
        try {
            IdeaLoggingEvent event = events[0];
            String throwableText = event.getThrowableText();
            if (StringUtils.isBlank(throwableText)) {
                return false;
            }
            String message = event.getMessage();
            if (StringUtils.isBlank(message)) {
                message = throwableText.substring(0, throwableText.indexOf("\r\n"));
            }
            if (StringUtils.isBlank(additionalInfo)) {
                additionalInfo = StringUtils.EMPTY;
            }
            SubmittedReportInfo reportInfo;
            String issueId = findIssueByMd5(DigestUtils.md5Hex(throwableText).toUpperCase());
            if (StringUtils.isBlank(issueId)) {
                issueId = newIssue(throwableText, message, additionalInfo);
                reportInfo = new SubmittedReportInfo(generateUrlByIssueId(issueId), generateTextByIssueId(issueId), SubmittedReportInfo.SubmissionStatus.NEW_ISSUE);
            } else {
                reportInfo = new SubmittedReportInfo(generateUrlByIssueId(issueId), generateTextByIssueId(issueId), SubmittedReportInfo.SubmissionStatus.DUPLICATE);
            }
            consumer.consume(reportInfo);
            return true;
        } catch (Exception e) {
            consumer.consume(new SubmittedReportInfo(StringUtils.EMPTY, "error: " + e.getMessage(), SubmittedReportInfo.SubmissionStatus.FAILED));
            return false;
        }
    }

    /**
     * 根据错误信息新建Issue
     *
     * @param throwableText
     * @param message
     * @param additionalInfo 用户输入信息
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/25 15:29
     */
    protected abstract String newIssue(String throwableText, String message, String additionalInfo);

    /**
     * 根据错误栈信息MD5值查询Issue
     *
     * @param throwableMd5
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/25 14:57
     */
    @NotNull
    protected abstract String findIssueByMd5(String throwableMd5);

    /**
     * 获取Issue列表页链接
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/25 14:59
     */
    protected abstract String getIssueListPageUrl();

    /**
     * 生成Issue展示文字
     *
     * @param issueId
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/25 15:00
     */
    @NotNull
    protected abstract String generateTextByIssueId(String issueId);

    /**
     * 生成Issue链接
     *
     * @param issueId
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/25 15:00
     */
    @NotNull
    protected abstract String generateUrlByIssueId(String issueId);

}

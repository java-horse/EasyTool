package easy.diagnostic;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.util.SystemInfo;
import easy.util.EasyCommonUtil;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import easy.util.NotifyUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Gitee issue 报告基类
 *
 * @author mabin
 * @date 2023/12/23 16:33
 */
public abstract class AbstractGiteeErrorReportSubmitter extends AbstractErrorReportSubmitter {

    private static final Logger log = Logger.getInstance(AbstractGiteeErrorReportSubmitter.class);
    private static final String API_BASE_URL = "https://gitee.com/";

    protected abstract String getGiteeOwner();

    protected abstract String getGiteeRepo();

    protected abstract String getGiteeToken();

    @Override
    protected String getIssueListPageUrl() {
        return API_BASE_URL + getGiteeOwner() + "/" + getGiteeRepo() + "/issues";
    }

    @NotNull
    @Override
    protected String generateTextByIssueId(String issueId) {
        return "Gitee Issue#" + issueId;
    }

    @NotNull
    @Override
    protected String generateUrlByIssueId(String issueId) {
        return getIssueListPageUrl() + "/" + issueId;
    }

    @Override
    protected String newIssue(String throwableText, String message, String additionalInfo) {
        ApplicationInfoEx appInfo = ApplicationInfoEx.getInstanceEx();
        PluginDescriptor pluginDescriptor = getPluginDescriptor();
        Properties systemProperties = System.getProperties();
        String title = "[Report From Jetbrains IDE] " + message;
        String body = String.format(":warning:_`[Report From Jetbrains IDE]-=%s=-`_\n" +
                        "\n" +
                        "**错误信息**\n" +
                        "%s\n" +
                        "\n" +
                        "**错误日志**\n" +
                        "```\n" +
                        "%s\n" +
                        "```\n" +
                        "\n" +
                        "**运行环境**\n" +
                        "Plugin: `%s V%s`\n" +
                        "%s %s\n" +
                        "Build %s, built on %s\n" +
                        "Runtime version: %s %s\n" +
                        "VM: %s by %s\n" +
                        "Operating system: %s\n" +
                        "JVM `file.encoding` : %s\n" +
                        "\n" +
                        "**附加信息**\n" +
                        "%s", DigestUtils.md5Hex(throwableText).toUpperCase(), message, throwableText, pluginDescriptor.getName(), pluginDescriptor.getVersion(), appInfo.getFullApplicationName(), ApplicationNamesInfo.getInstance().getEditionName()
                , appInfo.getBuild().asString(), DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(appInfo.getBuildDate().getTime()), systemProperties.getProperty("java.runtime.version", systemProperties.getProperty("java.version", StringUtils.EMPTY))
                , systemProperties.getProperty("os.arch", StringUtils.EMPTY), systemProperties.getProperty("java.vm.name", StringUtils.EMPTY), systemProperties.getProperty("java.vendor", StringUtils.EMPTY),
                SystemInfo.getOsNameAndVersion(), Charset.defaultCharset().displayName(), additionalInfo);
        return createIssue(title, body);
    }

    /**
     * 提交Issue
     *
     * @param title
     * @param body
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/25 15:38
     */
    private String createIssue(String title, String body) {
        Map<String, Object> paramsMap = new HashMap<>(16);
        paramsMap.put("access_token", getGiteeToken());
        paramsMap.put("owner", getGiteeOwner());
        paramsMap.put("repo", getGiteeRepo());
        paramsMap.put("title", title);
        paramsMap.put("issue_type", "任务");
        paramsMap.put("body", body);
        paramsMap.put("assignee", API_BASE_URL + getGiteeOwner());
        paramsMap.put("labels", "bug");
        String response = HttpUtil.doPost(API_BASE_URL + String.format("api/v5/repos/%s/issues", getGiteeOwner()), JsonUtil.toJson(paramsMap));
        if (StringUtils.isBlank(response)) {
            return StringUtils.EMPTY;
        }
        return JsonUtil.fromObject(response).get("number").getAsString();
    }

    @NotNull
    @Override
    protected String findIssueByMd5(String throwableMd5) {
        Map<String, String> paramsMap = new HashMap<>(16);
        paramsMap.put("access_token", getGiteeToken());
        paramsMap.put("state", "all");
        paramsMap.put("labels", "bug");
        paramsMap.put("sort", "created");
        paramsMap.put("direction", "desc");
        paramsMap.put("per_page", "100");
        int page = 1;
        Map<String, String> issueNumberMap = new HashMap<>(16);
        while (true) {
            paramsMap.put("page", Integer.toString(page));
            String response = HttpUtil.doGet(API_BASE_URL + String.format("api/v5/repos/%s/%s/issues", getGiteeOwner(), getGiteeRepo()), paramsMap);
            JsonArray resArray = JsonUtil.fromArray(response);
            if (Objects.isNull(resArray) || resArray.isEmpty()) {
                break;
            }
            for (JsonElement element : resArray) {
                JsonObject resObject = element.getAsJsonObject();
                issueNumberMap.put(StringUtils.substringBetween(resObject.get("body").getAsString(), "="), resObject.get("number").getAsString());
            }
            page++;
            try {
                ThreadUtils.sleep(Duration.ofMillis(500));
            } catch (InterruptedException e) {
                log.error("sleep error", e);
            }
        }
        String issueId = issueNumberMap.get(throwableMd5);
        return StringUtils.isBlank(issueId) ? StringUtils.EMPTY : issueId;
    }

    /**
     * 提交成功通知
     *
     * @param issueId
     * @return void
     * @author mabin
     * @date 2024/1/5 10:51
     */
    @Override
    protected void submitNotify(String issueId) {
        if (StringUtils.isBlank(issueId)) {
            return;
        }
        AnAction issueAction = new NotificationAction(issueId) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                EasyCommonUtil.confirmOpenLink(generateUrlByIssueId(issueId));
            }
        };
        NotifyUtil.notify("Submitted success. Thank you for your feedback!", issueAction);
    }

}

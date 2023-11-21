package easy.util;

import com.google.gson.JsonObject;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.SystemInfo;
import easy.base.Constants;
import easy.base.DingBotParam;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * 消息发送处理
 *
 * @project: EasyChar
 * @package: easy.util
 * @author: mabin
 * @date: 2023/11/13 15:39:00
 */
public class MessageUtil {

    private static final Logger log = Logger.getInstance(MessageUtil.class);

    private static final String DING_SECRET = "SECc6869ee3d87b149d875c432684c5ad075fb0ecb6c9e00c62b47db08c129db414";
    private static final String DING_WEBHOOK = "https://oapi.dingtalk.com/robot/send?access_token=495bbb7bedff1ac45016d378fed17bcea222f905403d1e2734f76d082e11f587";

    private MessageUtil() {
    }

    /**
     * 发送钉钉消息
     *
     * @param msgJson
     * @return void
     * @author mabin
     * @date 2023/11/15 10:42
     */
    public static void sendDingMessage(String msgJson) {
        long timestamp = System.currentTimeMillis();
        String sign = sign(timestamp, DING_SECRET);
        String url = DING_WEBHOOK + "&timestamp=" + timestamp + "&sign=" + sign;
        HttpUtil.doPost(url, msgJson);
    }

    /**
     * 异步发送Action钉钉消息
     *
     * @param e
     * @return void
     * @author mabin
     * @date 2023/11/15 10:43
     */
    public static void sendActionDingMessage(AnActionEvent e) {
        ExecutorUtil.getInstance().getExecutorService().submit(() -> {
            log.warn("current thread: " + Thread.currentThread().getName());
            DingBotParam dingBotParam = new DingBotParam();
            dingBotParam.setMsgtype("actionCard");
            DingBotParam.ActionCardVO actionCardVO = new DingBotParam.ActionCardVO();
            actionCardVO.setTitle(Constants.PLUGIN_NAME + " 通知");
            actionCardVO.setBtnOrientation("1");
            DingBotParam.ActionCardVO.BtnsVO btnsVO = new DingBotParam.ActionCardVO.BtnsVO();
            btnsVO.setTitle(Constants.PLUGIN_NAME + " Plugin");
            btnsVO.setActionURL(Constants.JETBRAINS_URL);
            actionCardVO.setBtns(Collections.singletonList(btnsVO));
            IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId("easy.char"));
            ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
            String text = String.format("![1](https://z1.ax1x.com/2023/11/15/piY8VCn.jpg) \n" +
                            "#### **%s** \n" +
                            "---  \n" +
                            "**主机系统：** %s \n\n" +
                            "**软件版本：** %s (%s) \n\n" +
                            "**地理位置：** %s \n\n" +
                            "**运行插件：** %s v%s (%s) \n\n" +
                            "**触发时间：** %s \n\n", Constants.PLUGIN_NAME + " 动态", SystemInfo.getOsNameAndVersion(), applicationInfo.getFullApplicationName(),
                    applicationInfo.getBuild().asString(), getIpRegion(), plugin.getName(), plugin.getVersion(), e.getPresentation().getText(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            actionCardVO.setText(text);
            dingBotParam.setActionCard(actionCardVO);
            sendDingMessage(JsonUtil.toJson(dingBotParam));
        });
    }

    /**
     * 返回ip区域相关信息
     * 格式: IP 国家-地区-城市 (经度-纬度)
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/17 17:23
     */
    private static String getIpRegion() {
        String ip = NetUtil.getIp();
        if (StringUtils.isBlank(ip)) {
            return null;
        }
        String regionJson = NetUtil.getIpRegion(ip);
        if (StringUtils.isBlank(regionJson)) {
            return ip;
        }
        JsonObject resObject = JsonUtil.fromObject(regionJson);
        String country = resObject.get("country").getAsString();
        String regionName = resObject.get("regionName").getAsString();
        String city = resObject.get("city").getAsString();
        double lon = resObject.get("lon").getAsDouble();
        double lat = resObject.get("lat").getAsDouble();
        StringBuilder builder = new StringBuilder(ip);
        if (StringUtils.isNotBlank(country)) {
            builder.append(" ").append(country);
        }
        if (StringUtils.isNotBlank(regionName)) {
            builder.append("-").append(regionName);
        }
        if (StringUtils.isNotBlank(city)) {
            builder.append("-").append(city);
        }
        builder.append(" (").append(lon).append("-").append(lat).append(")");
        return builder.toString();
    }


    /**
     * HmacSHA256算法计算签名
     *
     * @param timestamp
     * @param secret
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/13 15:56
     */
    private static String sign(long timestamp, String secret) {
        String sign = StringUtils.EMPTY;
        try {
            String stringToSign = timestamp + StringUtils.LF + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            sign = URLEncoder.encode(Base64.toBase64String(signData), StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            log.warn("钉钉机器人发送消息，签名异常: " + e.getMessage());
        }
        return sign;
    }

}

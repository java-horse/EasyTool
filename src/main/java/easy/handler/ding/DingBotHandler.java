package easy.handler.ding;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import easy.util.HttpUtil;
import easy.util.JsonUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DingBotHandler {
    private static final Logger log = Logger.getInstance(DingBotHandler.class);
    private static final String SECRET = "SECc6869ee3d87b149d875c432684c5ad075fb0ecb6c9e00c62b47db08c129db414";
    private static final String WEB_BOOK = "https://oapi.dingtalk.com/robot/send?access_token=495bbb7bedff1ac45016d378fed17bcea222f905403d1e2734f76d082e11f587";


    /**
     * 发送消息
     *
     * @param model 型号
     * @return {@link java.lang.Boolean}
     * @author mabin
     * @date 2024/07/15 17:24
     */
    public static Boolean send(DingBotModel model) {
        if (Objects.isNull(model)) {
            return false;
        }
        DingBotParam dingBotParam = new DingBotParam();
        DingBotParam.AtVO atVO = new DingBotParam.AtVO();
        if (CollUtil.isNotEmpty(model.getReceiver())) {
            if (StrUtil.equals(CollUtil.getFirst(model.getReceiver()), "@all")) {
                atVO.setAtAll(Boolean.TRUE);
            } else {
                atVO.setAtUserIds(new ArrayList<>(model.getReceiver()));
            }
        }
        dingBotParam.setAt(atVO);
        dingBotParam.setMsgtype(model.getSendType());
        if (StrUtil.equals(DingTypeEnum.TEXT.getType(), model.getSendType())) {
            DingBotParam.TextVO textVO = new DingBotParam.TextVO();
            textVO.setContent(model.getContent());
            dingBotParam.setText(textVO);
        } else if (StrUtil.equals(DingTypeEnum.MARKDOWN.getType(), model.getSendType())) {
            DingBotParam.MarkdownVO markdownVO = new DingBotParam.MarkdownVO();
            markdownVO.setTitle(model.getTitle());
            markdownVO.setText(model.getContent());
            dingBotParam.setMarkdown(markdownVO);
        } else if (StrUtil.equals(DingTypeEnum.LINK.getType(), model.getSendType())) {
            DingBotParam.LinkVO linkVO = new DingBotParam.LinkVO();
            linkVO.setTitle(model.getTitle());
            linkVO.setText(model.getContent());
            linkVO.setMessageUrl(model.getUrl());
            linkVO.setPicUrl(model.getPicUrl());
            dingBotParam.setLink(linkVO);
        } else if (StrUtil.equals(DingTypeEnum.NEWS.getType(), model.getSendType())) {
            DingBotParam.FeedCardVO feedCardVO = new DingBotParam.FeedCardVO();
            feedCardVO.setLinks(JsonUtil.fromJson(model.getFeedCards(), new TypeToken<List<DingBotParam.FeedCardVO.LinksVO>>() {
            }.getType()));
            dingBotParam.setFeedCard(feedCardVO);
        } else if (StrUtil.equals(DingTypeEnum.ACTION_CARD.getType(), model.getSendType())) {
            DingBotParam.ActionCardVO actionCardVO = new DingBotParam.ActionCardVO();
            actionCardVO.setTitle(model.getTitle());
            actionCardVO.setBtnOrientation(model.getBtnOrientation());
            actionCardVO.setText(model.getContent());
            actionCardVO.setBtns(JsonUtil.fromJson(model.getBtns(),
                    new TypeToken<List<DingBotParam.ActionCardVO.BtnVO>>() {
                    }.getType()));
            dingBotParam.setActionCard(actionCardVO);
        }
        // 组装URL并签名
        long timestamp = System.currentTimeMillis();
        String response = HttpUtil.doPost(String.format("%s&timestamp=%s&sign=%s", WEB_BOOK, timestamp, sign(timestamp, SECRET)),
                JsonUtil.toJson(dingBotParam));
        if (StrUtil.isBlank(response)) {
            return false;
        }
        JsonObject resObject = JsonUtil.fromObject(response);
        return Objects.nonNull(resObject) && resObject.get("errcode").getAsInt() == 0;
    }

    /**
     * 使用HmacSHA256算法计算签名
     *
     * @param timestamp 时间戳
     * @param secret    秘密
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/07/15 17:24
     */
    private static String sign(long timestamp, String secret) {
        String sign = StrUtil.EMPTY;
        try {
            String stringToSign = timestamp + StrUtil.LF + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            sign = URLEncoder.encode(Base64.encode(signData), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("钉钉机器人发送消息，签名异常: {}", e.getMessage());
        }
        return sign;
    }

}

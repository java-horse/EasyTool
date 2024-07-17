package easy.handler.ding;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class DingBotModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 4205208433388803645L;

    /**
     * 接收者（@all: 代表@所有人）
     */
    private Set<String> receiver;

    /**
     * 发送类型(例如: MessageTypeEnum.TEXT.getCode())
     */
    private String sendType;

    /**
     * 钉钉机器人：【文本消息】内容，【markdown消息】内容，【ActionCard消息】内容
     */
    private String content;

    /**
     * 钉钉机器人：【markdown消息】标题，【FeedCard消息】标题，【ActionCard消息】标题
     */
    private String title;

    /**
     * 钉钉机器人：【ActionCard消息】按钮布局
     */
    private String btnOrientation;

    /**
     * 钉钉机器人：【ActionCard消息】按钮的文案和跳转链接的json
     * [{\"title\":\"别点我\",\"actionURL\":\"https://www.baidu.com/\"},{\"title\":\"没关系，还是点我把\",\"actionURL\":\"https://www.baidu.com/\\t\"}]
     */
    private String btns;


    /**
     * 钉钉机器人：【链接消息】点击消息跳转的URL，【FeedCard消息】点击消息跳转的URL
     */
    private String url;

    /**
     * 钉钉机器人：【链接消息】图片URL，【FeedCard消息】图片URL
     */
    private String picUrl;


    /**
     * 钉钉机器人：【FeedCard类型】 消息体
     * "[{\"picUrl\":\"https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png\",\"title\":\"{$title1}\",\"url\":\"https://www.dingtalk.com/\"},{\"picUrl\":\"https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png\\t\",\"title\":\"时代的火车向前开2\",\"url\":\"https://www.dingtalk.com/\"}]"}
     */
    private String feedCards;

    public String getSendType() {
        return sendType;
    }

    public Set<String> getReceiver() {
        return receiver;
    }

    public void setReceiver(Set<String> receiver) {
        this.receiver = receiver;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBtnOrientation() {
        return btnOrientation;
    }

    public void setBtnOrientation(String btnOrientation) {
        this.btnOrientation = btnOrientation;
    }

    public String getBtns() {
        return btns;
    }

    public void setBtns(String btns) {
        this.btns = btns;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getFeedCards() {
        return feedCards;
    }

    public void setFeedCards(String feedCards) {
        this.feedCards = feedCards;
    }

}

package easy.handler.ding;


import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DingBotParam implements Serializable {

    @Serial
    private static final long serialVersionUID = -8998540123559401895L;

    /**
     * at
     */
    private AtVO at;
    /**
     * text
     */
    private TextVO text;
    /**
     * link
     */
    private LinkVO link;
    /**
     * markdown
     */
    private MarkdownVO markdown;
    /**
     * actionCard
     */
    private ActionCardVO actionCard;
    /**
     * feedCard
     */
    private FeedCardVO feedCard;
    /**
     * msgtype
     */
    private String msgtype;

    public AtVO getAt() {
        return at;
    }

    public void setAt(AtVO at) {
        this.at = at;
    }

    public TextVO getText() {
        return text;
    }

    public void setText(TextVO text) {
        this.text = text;
    }

    public LinkVO getLink() {
        return link;
    }

    public void setLink(LinkVO link) {
        this.link = link;
    }

    public MarkdownVO getMarkdown() {
        return markdown;
    }

    public void setMarkdown(MarkdownVO markdown) {
        this.markdown = markdown;
    }

    public ActionCardVO getActionCard() {
        return actionCard;
    }

    public void setActionCard(ActionCardVO actionCard) {
        this.actionCard = actionCard;
    }

    public FeedCardVO getFeedCard() {
        return feedCard;
    }

    public void setFeedCard(FeedCardVO feedCard) {
        this.feedCard = feedCard;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public static class AtVO implements Serializable {
        @Serial
        private static final long serialVersionUID = -7518313869636391236L;
        /**
         * atMobiles
         */
        private List<String> atMobiles;
        /**
         * atUserIds
         */
        private List<String> atUserIds;
        /**
         * isAtAll
         */
        private Boolean isAtAll;

        public List<String> getAtMobiles() {
            return atMobiles;
        }

        public void setAtMobiles(List<String> atMobiles) {
            this.atMobiles = atMobiles;
        }

        public List<String> getAtUserIds() {
            return atUserIds;
        }

        public void setAtUserIds(List<String> atUserIds) {
            this.atUserIds = atUserIds;
        }

        public Boolean getAtAll() {
            return isAtAll;
        }

        public void setAtAll(Boolean atAll) {
            isAtAll = atAll;
        }
    }

    public static class TextVO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1167678946548595220L;
        /**
         * content
         */
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class LinkVO implements Serializable {
        @Serial
        private static final long serialVersionUID = -6923949807737555488L;
        /**
         * text
         */
        private String text;
        /**
         * title
         */
        private String title;
        /**
         * picUrl
         */
        private String picUrl;
        /**
         * messageUrl
         */
        private String messageUrl;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getMessageUrl() {
            return messageUrl;
        }

        public void setMessageUrl(String messageUrl) {
            this.messageUrl = messageUrl;
        }
    }

    public static class MarkdownVO implements Serializable {
        @Serial
        private static final long serialVersionUID = 8085779308073489486L;
        /**
         * title
         */
        private String title;
        /**
         * text
         */
        private String text;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class ActionCardVO implements Serializable {
        @Serial
        private static final long serialVersionUID = -9155292336800192580L;
        /**
         * title
         */
        private String title;
        /**
         * text
         */
        private String text;
        /**
         * btnOrientation
         */
        private String btnOrientation;
        /**
         * btns
         */
        private List<BtnVO> btns;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getBtnOrientation() {
            return btnOrientation;
        }

        public void setBtnOrientation(String btnOrientation) {
            this.btnOrientation = btnOrientation;
        }

        public List<BtnVO> getBtns() {
            return btns;
        }

        public void setBtns(List<BtnVO> btns) {
            this.btns = btns;
        }

        public static class BtnVO implements Serializable {
            @Serial
            private static final long serialVersionUID = -614812580186600701L;
            /**
             * title
             */
            private String title;
            /**
             * actionURL
             */
            private String actionURL;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getActionURL() {
                return actionURL;
            }

            public void setActionURL(String actionURL) {
                this.actionURL = actionURL;
            }
        }
    }

    public static class FeedCardVO implements Serializable {
        @Serial
        private static final long serialVersionUID = 228317357305016449L;
        /**
         * links
         */
        private List<LinksVO> links;

        public List<LinksVO> getLinks() {
            return links;
        }

        public void setLinks(List<LinksVO> links) {
            this.links = links;
        }

        public static class LinksVO implements Serializable {
            @Serial
            private static final long serialVersionUID = 2650731304407117469L;
            /**
             * title
             */
            private String title;
            /**
             * messageURL
             */
            private String messageURL;
            /**
             * picURL
             */
            private String picURL;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getMessageURL() {
                return messageURL;
            }

            public void setMessageURL(String messageURL) {
                this.messageURL = messageURL;
            }

            public String getPicURL() {
                return picURL;
            }

            public void setPicURL(String picURL) {
                this.picURL = picURL;
            }
        }
    }

}

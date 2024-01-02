package easy.postfix.util;

public class CommentInfoTag extends CommentInfo {
    
    /**
     * 是否优先级更高
     */
    private boolean important = false;

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

}

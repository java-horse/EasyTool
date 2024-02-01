package easy.git.commit;

import java.io.Serializable;

/**
 * 消息模板
 *
 * @project: EasyTool
 * @package: easy.git.commit
 * @author: mabin
 * @date: 2024/01/24 17:07:06
 */
public class GitCommitMessageTemplate implements Serializable {
    private static final long serialVersionUID = -6336457801654352119L;

    private String type;
    private String scope;
    private String subject;
    private String body;
    private String changes;
    private String closes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public String getCloses() {
        return closes;
    }

    public void setCloses(String closes) {
        this.closes = closes;
    }

    @Override
    public String toString() {
        return "GitCommitMessageTemplate{" +
                "type='" + type + '\'' +
                ", scope='" + scope + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", changes='" + changes + '\'' +
                ", closes='" + closes + '\'' +
                '}';
    }
}

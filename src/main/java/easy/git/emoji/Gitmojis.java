package easy.git.emoji;

import com.intellij.openapi.util.SystemInfo;

import java.util.List;

/**
 * 设置Git Commit Message
 *
 * @project: EasyTool
 * @package: easy.git.emoji
 * @author: mabin
 * @date: 2024/01/12 15:14:15
 */
public class Gitmojis {

    private List<Gitmoji> gitmojis;

    public Gitmojis(List<Gitmoji> gitmojis) {
        this.gitmojis = gitmojis;
    }

    public List<Gitmoji> getGitmojis() {
        return gitmojis;
    }

    public void setGitmojis(List<Gitmoji> gitmojis) {
        this.gitmojis = gitmojis;
    }

    public static class Gitmoji {
        private final String emoji;
        private final String code;
        private final String description;

        public Gitmoji(String emoji, String code, String description) {
            this.emoji = emoji;
            this.code = code;
            this.description = description;
        }

        public String getEmoji() {
            return emoji;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "Gitmoji{" +
                    "emoji='" + emoji + '\'' +
                    ", code='" + code + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    /**
     * 静态工具类方法：默认是否显示表情符
     */
    public static boolean defaultDisplayEmoji() {
        return !SystemInfo.isWindows;
    }

    /**
     * 在目标字符串指定位置插入新字符串的方法
     *
     * @param target   目标字符串
     * @param position 插入位置（0表示在开头插入）
     * @param insert   要插入的新字符串
     * @return 插入后的新字符串
     */
    public static String insertAt(String target, int position, String insert) {
        // 获取目标字符串长度
        int targetLen = target.length();
        // 检查插入位置是否有效
        if (position < 0 || position > targetLen) {
            throw new IllegalArgumentException("插入位置无效：position=" + position);
        }
        // 如果要插入的字符串为空，则直接返回原目标字符串
        if (insert.isEmpty()) {
            return target;
        }
        // 创建一个字符数组来构建新的字符串
        char[] buffer = new char[targetLen + insert.length()];
        // 根据插入位置的不同进行不同的处理
        if (position == 0) {
            // 在字符串起始位置插入
            System.arraycopy(insert.toCharArray(), 0, buffer, 0, insert.length());
            System.arraycopy(target.toCharArray(), 0, buffer, insert.length(), target.length());
        } else if (position == targetLen) {
            // 在字符串末尾插入
            System.arraycopy(target.toCharArray(), 0, buffer, 0, target.length());
            System.arraycopy(insert.toCharArray(), 0, buffer, target.length(), insert.length());
        } else {
            // 在字符串中间插入
            System.arraycopy(target.toCharArray(), 0, buffer, 0, position);
            System.arraycopy(insert.toCharArray(), 0, buffer, position, insert.length());
            System.arraycopy(target.toCharArray(), position, buffer, position + insert.length(), targetLen - position);
        }
        return new String(buffer);
    }

}


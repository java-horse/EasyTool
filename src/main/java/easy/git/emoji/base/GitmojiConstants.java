package easy.git.emoji.base;

/**
 * xx
 *
 * @project: EasyTool
 * @package: easy.git.emoji
 * @author: mabin
 * @date: 2024/01/12 15:30:40
 */
public class GitmojiConstants {

    // 插件ID
    public static final String PLUGIN_ID = "com.h3110w0r1d.gitmoji";

    // 渲染提交日志的配置键
    public static final String CONFIG_RENDER_COMMIT_LOG = PLUGIN_ID + ".render-commit-log";

    // 是否使用Unicode而非文本版本（例如：code：）
    public static final String CONFIG_USE_UNICODE = PLUGIN_ID + ".use-unicode";

    // 是否显示表情而非列表中的图标（在IntelliJ Windows中可能错误或以黑白显示表情）
    public static final String CONFIG_DISPLAY_EMOJI = PLUGIN_ID + ".display-emoji";

    // Emoji之后的字符配置键
    public static final String CONFIG_AFTER_EMOJI = PLUGIN_ID + ".text-after-emoji";

    // 在光标位置插入的配置键
    public static final String CONFIG_INSERT_IN_CURSOR_POSITION = PLUGIN_ID + ".insert-in-cursor-position";

    // 是否包含gitmoji描述的配置键
    public static final String CONFIG_INCLUDE_GITMOJI_DESCRIPTION = PLUGIN_ID + ".include-gitmoji-description";

    // 语言配置键
    public static final String CONFIG_LANGUAGE = PLUGIN_ID + ".language";

}

package easy.enums;

public enum GitEmojiLanguageEnum {

    ENGLISH("English", "en"),
    CHINESE_SIMPLE("Chinese Simple", "zh-CN"),
    CHINESE_TRADITION("Chinese Tradition", "zh-TW"),
    JAPANESE("Japanese", "ja");

    private final String language;
    private final String code;

    GitEmojiLanguageEnum(String language, String code) {
        this.language = language;
        this.code = code;
    }

    public static String getCode(String language) {
        if (language == null || language.isBlank()) {
            return null;
        }
        for (GitEmojiLanguageEnum gitEmojiLanguageEnum : values()) {
            if (gitEmojiLanguageEnum.getLanguage().equals(language)) {
                return gitEmojiLanguageEnum.getCode();
            }
        }
        return null;
    }

    public static String getLanguage(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        for (GitEmojiLanguageEnum gitEmojiLanguageEnum : values()) {
            if (gitEmojiLanguageEnum.getCode().equals(code)) {
                return gitEmojiLanguageEnum.getLanguage();
            }
        }
        return null;
    }

    public String getLanguage() {
        return language;
    }

    public String getCode() {
        return code;
    }

}

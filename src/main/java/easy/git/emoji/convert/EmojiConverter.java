package easy.git.emoji.convert;

import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;

public class EmojiConverter {

    // 定义一个静态映射表，将emoji别名转换为对应的Unicode表情符号
    private static final Map<String, String> CONVERTER_MAP = new HashMap<>();
    static {
        CONVERTER_MAP.put(":art:", "🎨");
        CONVERTER_MAP.put(":zap:", "⚡️");
        CONVERTER_MAP.put(":fire:", "🔥");
        CONVERTER_MAP.put(":bug:", "🐛");
        CONVERTER_MAP.put(":ambulance:", "🚑️");
        CONVERTER_MAP.put(":sparkles:", "✨");
        CONVERTER_MAP.put(":memo:", "📝");
        CONVERTER_MAP.put(":rocket:", "🚀");
        CONVERTER_MAP.put(":lipstick:", "💄");
        CONVERTER_MAP.put(":tada:", "🎉");
        CONVERTER_MAP.put(":white_check_mark:", "✅");
        CONVERTER_MAP.put(":lock:", "🔒️");
        CONVERTER_MAP.put(":closed_lock_with_key:", "🔐");
        CONVERTER_MAP.put(":bookmark:", "🔖");
        CONVERTER_MAP.put(":rotating_light:", "🚨");
        CONVERTER_MAP.put(":construction:", "🚧");
        CONVERTER_MAP.put(":green_heart:", "💚");
        CONVERTER_MAP.put(":arrow_down:", "⬇️");
        CONVERTER_MAP.put(":arrow_up:", "⬆️");
        CONVERTER_MAP.put(":pushpin:", "📌");
        CONVERTER_MAP.put(":construction_worker:", "👷");
        CONVERTER_MAP.put(":chart_with_upwards_trend:", "📈");
        CONVERTER_MAP.put(":recycle:", "♻️");
        CONVERTER_MAP.put(":heavy_plus_sign:", "➕");
        CONVERTER_MAP.put(":heavy_minus_sign:", "➖");
        CONVERTER_MAP.put(":wrench:", "🔧");
        CONVERTER_MAP.put(":hammer:", "🔨");
        CONVERTER_MAP.put(":globe_with_meridians:", "🌐");
        CONVERTER_MAP.put(":pencil2:", "✏️ ");
        CONVERTER_MAP.put(":poop:", "💩");
        CONVERTER_MAP.put(":rewind:", "⏪️");
        CONVERTER_MAP.put(":twisted_rightwards_arrows:", "🔀");
        CONVERTER_MAP.put(":package:", "📦️");
        CONVERTER_MAP.put(":alien:", "👽️");
        CONVERTER_MAP.put(":truck:", "🚚");
        CONVERTER_MAP.put(":page_facing_up:", "📄");
        CONVERTER_MAP.put(":boom:", "💥");
        CONVERTER_MAP.put(":bento:", "🍱");
        CONVERTER_MAP.put(":wheelchair:", "♿️");
        CONVERTER_MAP.put(":bulb:", "💡");
        CONVERTER_MAP.put(":beers:", "🍻");
        CONVERTER_MAP.put(":speech_balloon:", "💬");
        CONVERTER_MAP.put(":card_file_box:", "🗃️");
        CONVERTER_MAP.put(":loud_sound:", "🔊");
        CONVERTER_MAP.put(":mute:", "🔇");
        CONVERTER_MAP.put(":busts_in_silhouette:", "👥");
        CONVERTER_MAP.put(":children_crossing:", "🚸");
        CONVERTER_MAP.put(":building_construction:", "🏗️");
        CONVERTER_MAP.put(":iphone:", "📱");
        CONVERTER_MAP.put(":clown_face:", "🤡");
        CONVERTER_MAP.put(":egg:", "🥚");
        CONVERTER_MAP.put(":see_no_evil:", "🙈");
        CONVERTER_MAP.put(":camera_flash:", "📸");
        CONVERTER_MAP.put(":alembic:", "⚗️");
        CONVERTER_MAP.put(":mag:", "🔍️");
        CONVERTER_MAP.put(":label:", "🏷️");
        CONVERTER_MAP.put(":seedling:", "🌱");
        CONVERTER_MAP.put(":triangular_flag_on_post:", "🚩");
        CONVERTER_MAP.put(":goal_net:", "🥅");
        CONVERTER_MAP.put(":dizzy:", "💫");
        CONVERTER_MAP.put(":wastebasket:", "🗑️");
        CONVERTER_MAP.put(":passport_control:", "🛂");
        CONVERTER_MAP.put(":adhesive_bandage:", "🩹");
        CONVERTER_MAP.put(":monocle_face:", "🧐");
        CONVERTER_MAP.put(":coffin:", "⚰️");
        CONVERTER_MAP.put(":test_tube:", "🧪");
        CONVERTER_MAP.put(":necktie:", "👔");
        CONVERTER_MAP.put(":stethoscope:", "🩺");
        CONVERTER_MAP.put(":bricks:", "🧱");
        CONVERTER_MAP.put(":technologist:", "🧑‍💻");
        CONVERTER_MAP.put(":money_with_wings:", "💸");
        CONVERTER_MAP.put(":thread:", "🧵");
        CONVERTER_MAP.put(":safety_vest:", "🦺");
    }

    /**
     * 将包含emoji别名的字符串转换为实际的Unicode表情符号
     *
     * @param value 包含emoji别名的字符串
     * @return 替换后的真实Unicode表情符号字符串
     */
    public static String convert(String value) {
        if (!value.contains(":")) {
            return value;
        }
        StringBuilder sb = new StringBuilder(value);
        for (Map.Entry<String, String> entry : CONVERTER_MAP.entrySet()) {
            int index = sb.indexOf(entry.getKey());
            while (index != -1) {
                sb.replace(index, index + entry.getKey().length(), entry.getValue());
                index = sb.indexOf(entry.getKey());
            }
        }
        return sb.toString();
    }

}



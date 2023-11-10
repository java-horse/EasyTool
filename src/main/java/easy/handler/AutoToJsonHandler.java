package easy.handler;

import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * string自动转json处理
 *
 * @project: EasyChar
 * @package: easy.handler
 * @author: mabin
 * @date: 2023/11/09 11:48:40
 */
public class AutoToJsonHandler {

    private AutoToJsonHandler() {
    }

    private static final Pattern DATE_PATTERN = Pattern.compile("^[a-zA-Z]{3} [a-zA-Z]{3} [0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2} CST ((19|20)\\d{2})$");
    private static final Pattern NUM_PATTERN = Pattern.compile("^-?[0-9]+\\.?[0-9]*$");
    private static final Pattern OBJECT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\.]+\\(.+\\)$");
    private static final Pattern LIST_PATTERN = Pattern.compile("^\\[.*\\]$");
    private static final Pattern MAP_PATTERN = Pattern.compile("^\\{.*\\}$");
    private static final Pattern SUPPER_PATTERN = Pattern.compile("^super=[a-zA-Z0-9\\.]+\\(.+\\)$");
    private static final Map<Character, Character> tokenMap = new HashMap<>(16);

    static {
        tokenMap.put(')', '(');
        tokenMap.put('}', '{');
        tokenMap.put(']', '[');
    }

    public static String toJSON(String toString) throws ParseException {
        return JsonUtil.toJson(toMap(toString));
    }

    private static Map<String, Object> toMap(String toString) throws ParseException {
        if (StringUtils.isBlank(toString)) {
            return Collections.emptyMap();
        }
        toString = StringUtils.substringAfter(toString, "(").trim();
        toString = StringUtils.substringBeforeLast(toString, ")").trim();
        String token;
        Map<String, Object> map = new HashMap<>(16);
        while (StringUtils.isNotBlank(toString) && StringUtils.isNotBlank(token = splitToken(toString))) {
            toString = StringUtils.removeStart(StringUtils.removeStart(toString, token).trim(), ",").trim();
            // 如果带"super="(lombok的@ToString(callSuper=true)引入)，按照当前层继续处理
            if (SUPPER_PATTERN.matcher(token).matches()) {
                token = token.substring(token.indexOf("(") + 1, token.length() - 1);
                toString = String.format("%s,%s", token, toString);
                continue;
            }
            Pair<String, String> keyValue = parseToken(token);
            map.put(keyValue.getKey(), buildTypeValue(keyValue.getValue()));
        }
        return map;
    }

    private static Object buildTypeValue(String value) throws ParseException {
        if (StringUtils.isBlank(value) || StringUtils.equals(value, "null")) {
            return null;
        }
        if (DATE_PATTERN.matcher(value).matches()) {
            return new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us")).parse(value).getTime();
        }
        if (NUM_PATTERN.matcher(value).matches()) {
            return value;
        }
        if (LIST_PATTERN.matcher(value).matches()) {
            return buildListValue(value);
        }
        if (MAP_PATTERN.matcher(value).matches()) {
            return buildMapValue(value);
        }
        if (OBJECT_PATTERN.matcher(value).matches()) {
            return toMap(value);
        }
        return value;
    }

    private static Object buildListValue(String value) throws ParseException {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        List<Object> result = new ArrayList<>();
        value = value.substring(1, value.length() - 1).trim();
        if (StringUtils.isBlank(value)) {
            return result;
        }
        String token;
        while (StringUtils.isNotBlank(value) && StringUtils.isNotBlank(token = splitToken(value))) {
            result.add(buildTypeValue(token));
            value = StringUtils.removeStart(StringUtils.removeStart(value, token).trim(), ",").trim();
        }
        return result;
    }

    private static Map<Object, Object> buildMapValue(String value) throws ParseException {
        Map<Object, Object> result = new HashMap<>(16);
        value = value.substring(1, value.length() - 1).trim();
        if (StringUtils.isEmpty(value)) {
            return result;
        }
        String token;
        while (StringUtils.isNotBlank(token = splitToken(value))) {
            Pair<String, String> keyValue = parseToken(token);
            result.put(buildTypeValue(keyValue.getKey()), buildTypeValue(keyValue.getValue()));
            value = StringUtils.removeStart(StringUtils.removeStart(value, token).trim(), ",").trim();
        }
        return result;
    }

    private static String splitToken(String toString) {
        if (StringUtils.isBlank(toString)) {
            return toString;
        }
        Deque<Character> deque = new LinkedList<>();
        for (int i = 0; i < toString.length(); i++) {
            Character c = toString.charAt(i);
            if (tokenMap.containsValue(c)) {
                deque.push(c);
            } else if (tokenMap.containsKey(c) && Objects.equals(deque.peek(), tokenMap.get(c))) {
                deque.pop();
            } else if ((c == ',') && deque.isEmpty()) {
                return toString.substring(0, i);
            }
        }
        return deque.isEmpty() ? toString : null;
    }

    private static Pair<String, String> parseToken(String token) {
        assert Objects.nonNull(token) && token.contains("=");
        int pos = token.indexOf("=");
        return Pair.of(token.substring(0, pos), token.substring(pos + 1));
    }

}

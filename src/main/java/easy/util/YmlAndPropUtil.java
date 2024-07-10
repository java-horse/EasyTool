package easy.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.yaml.YamlUtil;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;


/**
 * Yaml转Properties处理
 *
 * @author mabin
 * @project EasyTool
 * @package easy.util
 * @date 2024/06/29 15:19
 */
public class YmlAndPropUtil {

    private YmlAndPropUtil() {
    }

    public static String convertYml(String propStr) {
        if (Objects.isNull(propStr) || propStr.isBlank()) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> yamlData = parseProperties(propStr);
        DumperOptions options = new DumperOptions();
        // 使用漂亮的流样式
        options.setPrettyFlow(false);
        // 设置默认流样式为块样式
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        // 允许Unicode字符
        options.setAllowUnicode(true);
        // 设置缩进宽度
        options.setIndent(4);
        return new Yaml(options).dumpAsMap(yamlData);
    }

    public static String convertProp(String ymlStr) {
        if (Objects.isNull(ymlStr) || ymlStr.isBlank()) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> ymlMap = YamlUtil.load(new StringReader(ymlStr), Map.class);
        Properties props = recursiveYaml2Prop(ymlMap);
        try (Writer writer = new StringWriter()) {
            props.store(writer, " Automatically converts yml file to properties file.");
            return writer.toString();
        } catch (IOException ignore) {
        }
        return StringUtils.EMPTY;
    }

    private static Map<String, Object> parseProperties(String propertiesData) {
        Map<String, Object> data = new HashMap<>(16);
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(propertiesData));
        } catch (IOException ignore) {
        }
        for (String key : properties.stringPropertyNames()) {
            parseKey(data, key, properties.getProperty(key));
        }
        return data;
    }

    private static void parseKey(Map<String, Object> data, String key, String value) {
        value = StrUtil.trim(value);
        key = StrUtil.trim(key);
        String[] parts = key.split("\\.");
        Map<String, Object> current = data;
        for (int i = 0; i < parts.length - 1; i++) {
            if (i == parts.length - 2 && StrUtil.isNumeric(parts[parts.length - 1])) {
                addObj2MapList(current, parts[i], value);
                return;
            }
            String part = parts[i];
            if (!current.containsKey(part)) {
                current.put(part, new HashMap<>());
            }
            current = (Map<String, Object>) current.get(part);
        }
        String lastPart = parts[parts.length - 1];

        if (lastPart.matches("\\w+\\[\\d+\\]")) {
            String listKey = lastPart.substring(0, lastPart.indexOf('['));
            // 将list类型key转成map
            addObj2MapList(current, listKey, value);
        } else {
            current.put(lastPart, value);
        }
    }

    private static void addObj2MapList(Map<String, Object> map, String key, Object value) {
        map.compute(key, (k, v) -> {
            if (v == null) {
                return CollUtil.newArrayList(value);
            } else {
                List<Object> list = Convert.toList(Object.class, v);
                list.add(value);
                return list;
            }
        });
    }

    private static Properties recursiveYaml2Prop(Map<String, Object> ymlMap) {
        Properties props = new Properties();
        for (Map.Entry<String, Object> entry : ymlMap.entrySet()) {
            Object value = entry.getValue();
            // 空值处理
            if (ObjectUtil.isEmpty(value)) {
                props.setProperty(entry.getKey(), StrUtil.SPACE);
            }
            if (value instanceof Map) {
                // 递归处理嵌套的Map
                Properties nestedProps = recursiveYaml2Prop((Map<String, Object>) value);
                for (String key : nestedProps.stringPropertyNames()) {
                    props.setProperty(entry.getKey() + StrUtil.DOT + key, nestedProps.getProperty(key));
                }
            } else if (value instanceof List) {
                // list类型处理，用逗号拼接
                props.setProperty(entry.getKey(), StrUtil.join(StrUtil.COMMA, (List) value));
            } else {
                // 其他
                props.setProperty(entry.getKey(), StrUtil.toString(value));
            }
        }
        return props;
    }

}

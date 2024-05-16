package easy.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesUtil {

    private static final Map<String, Properties> CACHE = new ConcurrentHashMap<>();

    private PropertiesUtil() {
    }


    /**
     * 获取properties文件
     *
     * @param file 锉
     * @return {@link java.util.Properties}
     * @author mabin
     * @date 2024/05/12 10:59
     */
    public static Properties getProperties(String file) {
        return CACHE.computeIfAbsent(file, key -> load(file));
    }

    /**
     * 加载properties文件
     *
     * @param file 锉
     * @return {@link java.util.Properties}
     * @author mabin
     * @date 2024/05/12 10:58
     */
    public static Properties load(String file) {
        Properties properties = new Properties();
        try (InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(file)) {
            if (Objects.isNull(is)) {
                return properties;
            }
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            properties.load(bf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

}

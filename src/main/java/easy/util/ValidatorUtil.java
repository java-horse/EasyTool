package easy.util;

import com.intellij.openapi.options.ConfigurationException;
import easy.base.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 统一校验异常处理
 *
 * @project: EasyChar
 * @package: easy.util
 * @author: mabin
 * @date: 2023/10/09 16:56:18
 */
public class ValidatorUtil {

    private ValidatorUtil() {
    }

    public static void isTrue(boolean condition, String message) throws ConfigurationException {
        isTrue(condition, message, Constants.PLUGIN_NAME);
    }

    public static void isTrue(boolean condition, String message, String title) throws ConfigurationException {
        if (!condition) {
            if (Objects.isNull(title)) {
                throw new ConfigurationException(message);
            } else {
                throw new ConfigurationException(message, title);
            }
        }
    }

    public static void notTrue(boolean condition, String message) throws ConfigurationException {
        notTrue(condition, message, Constants.PLUGIN_NAME);
    }

    public static void notTrue(boolean condition, String message, String title) throws ConfigurationException {
        if (condition) {
            if (Objects.isNull(title)) {
                throw new ConfigurationException(message);
            } else {
                throw new ConfigurationException(message, title);
            }
        }
    }

    public static void notBlank(String str, String message) throws ConfigurationException {
        notBlank(str, message, null);
    }

    public static void notBlank(String str, String message, String title) throws ConfigurationException {
        if (StringUtils.isBlank(str)) {
            if (Objects.isNull(title)) {
                throw new ConfigurationException(message);
            } else {
                throw new ConfigurationException(message, title);
            }
        }
    }


}

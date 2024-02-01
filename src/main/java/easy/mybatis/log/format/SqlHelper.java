package easy.mybatis.log.format;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import easy.base.Constants;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL格式化处理
 *
 * @project: EasyTool
 * @package: easy.mybatis.log.format
 * @author: mabin
 * @date: 2024/01/18 14:54:04
 */
public class SqlHelper {

    private static final Logger log = Logger.getInstance(SqlHelper.class);
    private static final Map<Integer, ConsoleViewContentType> consoleViewContentTypes = new ConcurrentHashMap<>();
    private static final BasicFormatter FORMATTER = new BasicFormatter();
    private static final char MARK = '?';
    private static final Set<String> NEED_BRACKETS;
    private static String sql = null;

    static {
        Set<String> types = new HashSet<>(16);
        types.add("String");
        types.add("Date");
        types.add("Time");
        types.add("LocalDate");
        types.add("LocalTime");
        types.add("LocalDateTime");
        types.add("BigDecimal");
        types.add("Timestamp");
        NEED_BRACKETS = Collections.unmodifiableSet(types);
    }

    /**
     * 解析&打印SQL
     *
     * @param originSql
     * @param consoleView
     * @return void
     * @author mabin
     * @date 2024/1/18 14:58
     */
    public static void parseSqlByRow(String originSql, ConsoleViewImpl consoleView) {
        if (StringUtils.isBlank(originSql) || Objects.isNull(consoleView)) {
            return;
        }
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        String preparing = propertiesComponent.getValue(Constants.Persistence.MYBATIS_LOG.PREPARING_KEY, "preparing: ");
        String parameters = propertiesComponent.getValue(Constants.Persistence.MYBATIS_LOG.PARAMETERS_KEY, "parameters: ");
        if (originSql.contains(preparing)) {
            sql = originSql;
            return;
        }
        if (StringUtils.isNotBlank(sql) && !originSql.contains(parameters)) {
            return;
        }
        if (StringUtils.isBlank(sql)) {
            return;
        }
        String wholeSql = StringUtils.trim(parseSql(StringUtils.substringAfter(sql, preparing), parseParams(StringUtils.substringAfter(originSql, parameters))).toString());
        if (StringUtils.isBlank(wholeSql)) {
            return;
        }
        int rgb = getRgb(wholeSql);
        ConsoleViewContentType consoleViewContentType = consoleViewContentTypes.computeIfAbsent(rgb, k -> new ConsoleViewContentType(Integer.toString(rgb),
                new TextAttributes(new JBColor(rgb, rgb), null, null, null, Font.PLAIN)));
        consoleView.print("--------------------------------BEGIN-----------------------------------" + StringUtils.LF, ConsoleViewContentType.USER_INPUT);
        consoleView.print(FORMATTER.format(wholeSql) + ";" + StringUtils.LF, consoleViewContentType);
        consoleView.print("--------------------------------END-------------------------------------" + StringUtils.LF, ConsoleViewContentType.USER_INPUT);
    }

    private static StringBuilder parseSql(String sql, Queue<Map.Entry<String, String>> params) {
        StringBuilder sb = new StringBuilder(sql);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) != MARK) {
                continue;
            }
            Map.Entry<String, String> entry = params.poll();
            if (Objects.isNull(entry)) {
                continue;
            }
            sb.deleteCharAt(i);
            if (NEED_BRACKETS.contains(entry.getValue())) {
                sb.insert(i, String.format("'%s'", entry.getKey()));
            } else {
                sb.insert(i, entry.getKey());
            }
        }
        return sb;
    }

    private static Queue<Map.Entry<String, String>> parseParams(String line) {
        line = StringUtils.removeEnd(line, StringUtils.LF);
        String[] strings = StringUtils.splitByWholeSeparator(line, ", ");
        Queue<Map.Entry<String, String>> queue = new ArrayDeque<>(strings.length);
        for (String s : strings) {
            String value = StringUtils.substringBeforeLast(s, "(");
            String type = StringUtils.substringBetween(s, "(", ")");
            if (StringUtils.isEmpty(type)) {
                queue.offer(new AbstractMap.SimpleEntry<>(value, null));
            } else {
                queue.offer(new AbstractMap.SimpleEntry<>(value, type));
            }
        }
        return queue;
    }

    private static int getRgb(String wholeSql) {
        int defaultRgb = ConsoleViewContentType.ERROR_OUTPUT.getAttributes().getForegroundColor().getRGB();
        if (StringUtils.isBlank(wholeSql)) {
            return defaultRgb;
        }
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        if (StringUtils.startsWithIgnoreCase(wholeSql, "insert")) {
            return propertiesComponent.getInt(Constants.Persistence.MYBATIS_LOG.INSERT_SQL_COLOR_KEY, defaultRgb);
        } else if (StringUtils.startsWithIgnoreCase(wholeSql, "delete")) {
            return propertiesComponent.getInt(Constants.Persistence.MYBATIS_LOG.DELETE_SQL_COLOR_KEY, defaultRgb);
        } else if (StringUtils.startsWithIgnoreCase(wholeSql, "update")) {
            return propertiesComponent.getInt(Constants.Persistence.MYBATIS_LOG.UPDATE_SQL_COLOR_KEY, defaultRgb);
        } else if (StringUtils.startsWithIgnoreCase(wholeSql, "select")) {
            return propertiesComponent.getInt(Constants.Persistence.MYBATIS_LOG.SELECT_SQL_COLOR_KEY, defaultRgb);
        } else {
            return defaultRgb;
        }
    }

}

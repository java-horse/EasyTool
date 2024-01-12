package easy.mybatis.log;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import easy.base.Constants;
import easy.mybatis.log.ui.MyBatisLogManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MyBatisLogConsoleFilter implements Filter {

    private static final char MARK = '?';
    private static final Set<String> NEED_BRACKETS;
    private final Project project;
    private String sql = null;

    static {
        Set<String> types = new HashSet<>(8);
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

    public MyBatisLogConsoleFilter(Project project) {
        this.project = project;
    }

    @Override
    public @Nullable Result applyFilter(@NotNull String line, int entireLength) {
        MyBatisLogManager manager = MyBatisLogManager.getInstance(project);
        if (Objects.isNull(manager)) {
            return null;
        }
        if (!manager.isRunning()) {
            return null;
        }
        String preparing = manager.getPreparing();
        String parameters = manager.getParameters();
        List<String> keywords = manager.getKeywords();
        if (CollectionUtils.isNotEmpty(keywords)) {
            for (String keyword : keywords) {
                if (line.contains(keyword)) {
                    sql = null;
                    return null;
                }
            }
        }
        if (line.contains(preparing)) {
            sql = line;
            return null;
        }
        if (StringUtils.isNotBlank(sql) && !line.contains(parameters)) {
            return null;
        }
        if (StringUtils.isBlank(sql)) {
            return null;
        }
        String logPrefix = StringUtils.substringBefore(sql, preparing);
        String wholeSql = parseSql(StringUtils.substringAfter(sql, preparing), parseParams(StringUtils.substringAfter(line, parameters))).toString();
        String key;
        if (StringUtils.startsWithIgnoreCase(wholeSql, "insert")) {
            key = Constants.Persistence.MYBATIS_LOG.INSERT_SQL_COLOR_KEY;
        } else if (StringUtils.startsWithIgnoreCase(wholeSql, "delete")) {
            key = Constants.Persistence.MYBATIS_LOG.DELETE_SQL_COLOR_KEY;
        } else if (StringUtils.startsWithIgnoreCase(wholeSql, "update")) {
            key = Constants.Persistence.MYBATIS_LOG.UPDATE_SQL_COLOR_KEY;
        } else if (StringUtils.startsWithIgnoreCase(wholeSql, "select")) {
            key = Constants.Persistence.MYBATIS_LOG.SELECT_SQL_COLOR_KEY;
        } else {
            key = "unknown";
        }
        if (StringUtils.isNotBlank(wholeSql)) {
            manager.println(logPrefix, StringUtils.trim(wholeSql), PropertiesComponent.getInstance().getInt(key, ConsoleViewContentType.ERROR_OUTPUT.getAttributes().getForegroundColor().getRGB()));
        }
        return null;
    }

    static StringBuilder parseSql(String sql, Queue<Map.Entry<String, String>> params) {
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

    static Queue<Map.Entry<String, String>> parseParams(String line) {
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

}

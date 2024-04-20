package easy.util;

import cn.hutool.core.date.DatePattern;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * cron工具
 *
 * @author mabin
 * @project EasyTool
 * @package easy.util
 * @date 2024/04/20 15:19
 */
public class CronUtil {

    private CronUtil() {
    }

    /**
     * 是否有效的cron表达式
     *
     * @param cron
     * @return boolean
     * @author mabin
     * @date 2024/1/20 14:35
     */
    public static boolean isCron(String cron) {
        if (StringUtils.isBlank(cron)) {
            return false;
        }
        try {
            CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));
            cronParser.parse(cron);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 返回近 nextCount 次执行时间
     *
     * @param cronExpression
     * @param nextCount
     * @return java.util.List<java.lang.String>
     * @author mabin
     * @date 2024/1/20 14:38
     */
    public static List<String> nextExecutionTime(String cronExpression, int nextCount) {
        if (StringUtils.isBlank(cronExpression)) {
            return Collections.emptyList();
        }
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));
        Cron cron = parser.parse(cronExpression);
        ExecutionTime time = ExecutionTime.forCron(cron);
        ZonedDateTime now = ZonedDateTime.now();
        List<String> cronList = new ArrayList<>(nextCount);
        ZonedDateTime next = getNext(time, now);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        cronList.add(formatter.format(next));
        for (int i = 1; i < nextCount; i++) {
            next = getNext(time, next);
            cronList.add(formatter.format(next));
        }
        return cronList;
    }

    private static ZonedDateTime getNext(ExecutionTime time, ZonedDateTime current) {
        return time.nextExecution(current).get();
    }

}

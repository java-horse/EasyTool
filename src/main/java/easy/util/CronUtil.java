package easy.util;

import cn.hutool.core.date.DatePattern;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import easy.helper.EmptyHelper;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


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
     * @param cron     cron
     * @param cronType cron类型
     * @return boolean
     * @author mabin
     * @date 2024/06/29 13:48
     */
    public static boolean isCron(String cron, CronType cronType) {
        if (StringUtils.isBlank(cron)) {
            return false;
        }
        try {
            CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(cronType));
            cronParser.parse(cron);
            return true;
        } catch (Exception e) {
            return false;
        }
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
        return isCron(cron, CronType.SPRING);
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

    /**
     * 获取示例cron映射示例
     *
     * @return {@link java.util.Map<java.lang.String,java.lang.String>}
     * @author mabin
     * @date 2024/06/29 14:21
     */
    public static Map<String, String> getExampleCronMap() {
        Map<String, String> cronMap = new LinkedHashMap<>(16);
        cronMap.put("每天凌晨的12:30", "0 30 0 * * ?");
        cronMap.put("周一到周五每天上午10:15", "0 15 10 ? * MON-FRI");
        cronMap.put("每天上午10点，下午2点，4点", "0 0 10,14,16 * * ?");
        cronMap.put("每天9点到5点期间的每30分钟", "0 0/30 9-17 * * ?");
        cronMap.put("每个星期三中午12点", "0 0 12 ? * WED");
        cronMap.put("每天中午12点", "0 0 12 * * ?");
        cronMap.put("每天上午10:15", "0 15 10 * * ?");
        cronMap.put("每天下午2点到下午4点期间的每1分钟", "0 * 14-16 * * ?");
        cronMap.put("每天下午2点到下午4点期间的每5分钟", "0 0/5 14-16 * * ?");
        cronMap.put("每月的1日的凌晨2点", "0 0 2 1 * ?");
        cronMap.put("每月15日上午10:15", "0 15 10 15 * ?");
        cronMap.put("每月的第三个星期五上午10:15", "0 15 10 ? * 6#3");
        cronMap.put("每年三月的星期三的下午2:10和2:44", "0 10,44 14 ? 3 WED");
        cronMap.put("每天下午2点到4点期间和下午6点到8点期间的每5分钟", "0 0/5 14-16,18-20 * * ?");
        return cronMap;
    }

    private static ZonedDateTime getNext(ExecutionTime time, ZonedDateTime current) {
        return EmptyHelper.of(time.nextExecution(current)).get().orElse(null);
    }

}

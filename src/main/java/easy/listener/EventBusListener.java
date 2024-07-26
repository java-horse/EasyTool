package easy.listener;

import cn.hutool.db.handler.BeanHandler;
import com.google.common.eventbus.Subscribe;
import com.intellij.openapi.application.ApplicationInfo;
import easy.base.SqliteConstants;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.helper.ServiceHelper;
import easy.helper.SqliteHelper;
import easy.util.EventBusUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class EventBusListener {

    private final TranslateConfig translateConfig = ServiceHelper.getService(TranslateConfigComponent.class).getState();

    /**
     * 翻译备份事件
     *
     * @param event 事件
     * @author mabin
     * @date 2024/07/25 13:47
     */
    @Subscribe
    public void event(EventBusUtil.TranslateBackUpEvent event) {
        if (Objects.isNull(event) || StringUtils.isAnyBlank(event.getChannel(), event.getSource(), event.getTarget())
                || Objects.isNull(translateConfig) || StringUtils.isBlank(translateConfig.getBackupFilePath())) {
            return;
        }
        SqliteHelper sqliteHelper = new SqliteHelper(translateConfig.getBackupFilePath());
        String querySql = String.format("""
                SELECT id, source, channel FROM %s WHERE source = '%s' and channel = '%s' LIMIT 1;
                """, SqliteConstants.TABLE.BACKUP, event.getSource(), event.getChannel());
        EventBusUtil.TranslateBackUpEvent backup = sqliteHelper.query(querySql, BeanHandler.create(EventBusUtil.TranslateBackUpEvent.class));
        if (Objects.nonNull(backup)) {
            String updateSql = String.format("""
                    UPDATE %s SET target = '%s', modified_time = DATETIME(CURRENT_TIMESTAMP, 'localtime') WHERE source = '%s' AND channel = '%s' AND id = %s;
                    """, SqliteConstants.TABLE.BACKUP, event.getTarget(), backup.getSource(), backup.getChannel(), backup.getId());
            sqliteHelper.update(updateSql);
            return;
        }
        String insertSql = String.format("""
                INSERT INTO %s(source, target, channel, create_time, modified_time, ide) VALUES ('%s', '%s', '%s',
                DATETIME(CURRENT_TIMESTAMP, 'localtime'), DATETIME(CURRENT_TIMESTAMP, 'localtime'), '%s');
                """, SqliteConstants.TABLE.BACKUP, event.getSource(), event.getTarget(), event.getChannel(), ApplicationInfo.getInstance().getFullApplicationName());
        sqliteHelper.update(insertSql);
    }

}

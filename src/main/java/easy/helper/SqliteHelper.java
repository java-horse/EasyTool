package easy.helper;

import cn.hutool.core.date.StopWatch;
import cn.hutool.db.handler.BeanListHandler;
import cn.hutool.db.handler.NumberHandler;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.sql.SqlExecutor;
import com.intellij.openapi.diagnostic.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import easy.base.SqliteConstants;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * sqlite数据库操作处理
 *
 * @author mabin
 * @project EasyTool
 * @package easy.helper
 * @date 2024/07/24 17:47
 */
public class SqliteHelper {
    private static final Logger log = Logger.getInstance(SqliteHelper.class);
    private static final String Driver = "org.sqlite.JDBC";
    private static HikariDataSource dataSource;
    private Connection connection;

    /**
     * 构造函数
     *
     * @param dbFilePath db文件路径
     * @author mabin
     * @date 2024/07/24 17:09
     */
    public SqliteHelper(String dbFilePath) {
        try {
            initDataSource(dbFilePath);
        } catch (Exception e) {
            log.error("sqlite connect error!", e);
        }
    }

    /**
     * 初始化数据源
     *
     * @param dbFilePath db文件路径
     * @throws SQLException sql异常
     * @author mabin
     * @date 2024/07/26 11:22
     */
    private synchronized void initDataSource(String dbFilePath) {
        if (Objects.isNull(dataSource) || isSwitchDataSource(dbFilePath)) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(JDBC.PREFIX + dbFilePath);
            config.setDriverClassName(Driver);
            config.setAutoCommit(true);
            config.setConnectionTimeout(50000);
            config.setConnectionTestQuery("SELECT 1");
            config.setMinimumIdle(5);
            config.setMaximumPoolSize(10);
            dataSource = new HikariDataSource(config);
            log.warn("sqlite switch sqlite db: " + dbFilePath);
        }
    }

    /**
     * 是否切换数据源
     *
     * @param dbFilePath db文件路径
     * @return {@link java.lang.Boolean}
     * @throws SQLException sql异常
     * @author mabin
     * @date 2024/07/25 18:08
     */
    private Boolean isSwitchDataSource(String dbFilePath) {
        if (Objects.nonNull(dataSource)) {
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                return !StringUtils.equalsAnyIgnoreCase(metaData.getURL(), JDBC.PREFIX + dbFilePath);
            } catch (Exception e) {
                log.error("sqlite switch connect error!", e);
            }
        }
        return false;
    }

    /**
     * 静态获取数据库连接(用于测试连接是否成功, 不创建连接池)
     * 注意: 如果后面的 dbFilePath 路径太深或者名称太长，则建立连接会失败
     *
     * @param dbFilePath db文件路径
     * @return 数据库连接
     */
    public static Connection getConnection(String dbFilePath) {
        Connection connection = null;
        try {
            Class.forName(Driver);
            connection = DriverManager.getConnection(JDBC.PREFIX + dbFilePath);
            return connection;
        } catch (Exception e) {
            log.error("sqlite connect error!", e);
        } finally {
            if (Objects.nonNull(connection)) {
                try {
                    connection.close();
                } catch (Exception e) {
                    log.error("sqlite close connect error!", e);
                }
            }
        }
        return null;
    }

    /**
     * 获取连接
     *
     * @return {@link Connection}
     * @author mabin
     * @date 2024/07/24 17:51
     */
    private void connect() {
        try {
            if (Objects.isNull(connection) || connection.isClosed()) {
                connection = dataSource.getConnection();
            }
        } catch (Exception e) {
            log.error("sqlite connect error!", e);
        }
    }

    /**
     * 执行查询
     *
     * @param sql       sql
     * @param rsHandler 处理程序
     * @return {@link T}
     * @author mabin
     * @date 2024/07/24 17:35
     */
    public <T> T query(String sql, RsHandler<T> rsHandler) {
        StopWatch watch = new StopWatch();
        try {
            watch.start();
            connect();
            return SqlExecutor.query(connection, sql, rsHandler);
        } catch (Exception e) {
            log.error("sqlite query error!", e);
        } finally {
            close();
            watch.stop();
            log.warn("sqlite query consume: " + watch.getTotalTimeMillis() + "ms");
        }
        return null;
    }

    /**
     * 执行查询 返回结果集
     *
     * @param sql             sql
     * @param beanListHandler 豆列表处理程序
     * @return {@link List<T>}
     * @author mabin
     * @date 2024/07/24 17:37
     */
    public <T> List<T> query(String sql, BeanListHandler<T> beanListHandler) {
        StopWatch watch = new StopWatch();
        try {
            watch.start();
            connect();
            return SqlExecutor.query(connection, sql, beanListHandler);
        } catch (Exception e) {
            log.error("sqlite query error!", e);
        } finally {
            close();
            watch.stop();
            log.warn("sqlite query consume: " + watch.getTotalTimeMillis() + "ms");
        }
        return null;
    }

    /**
     * 分页查询
     *
     * @param sql             sql
     * @param page            第页
     * @param size            尺寸
     * @param beanListHandler 豆列表处理程序
     * @return {@link java.util.List<T>}
     * @author mabin
     * @date 2024/07/27 09:58
     */
    public <T> List<T> page(@NotNull String sql, int page, int size, @NotNull BeanListHandler<T> beanListHandler) {
        if (page == 0 || size == 0) {
            return Collections.emptyList();
        }
        return query(sql + " limit " + (page - 1) * size + "," + size, beanListHandler);
    }


    /**
     * 执行更新(包括增、删、改)
     *
     * @param sql
     * @return 更新行数
     */
    public int update(String sql) {
        StopWatch watch = new StopWatch();
        try {
            watch.start();
            connect();
            return SqlExecutor.execute(connection, sql);
        } catch (Exception e) {
            log.error("sqlite update error!", e);
        } finally {
            close();
            watch.stop();
            log.warn("sqlite update consume: " + watch.getTotalTimeMillis() + "ms");
        }
        return 0;
    }

    /**
     * 执行批量更新(包括增、删、改)
     *
     * @param sqlList sql列表
     * @author mabin
     * @date 2024/07/24 17:41
     */
    public void update(@NotNull String... sqlList) {
        StopWatch watch = new StopWatch();
        try {
            watch.start();
            connect();
            SqlExecutor.executeBatch(connection, sqlList);
        } catch (Exception e) {
            log.error("sqlite update error!", e);
        } finally {
            close();
            watch.stop();
            log.warn("sqlite update consume: " + watch.getTotalTimeMillis() + "ms");
        }
    }

    /**
     * 执行批量更新(包括增、删、改)
     *
     * @param sqlList sql列表
     * @author mabin
     * @date 2024/07/24 17:41
     */
    public void update(@NotNull List<String> sqlList) {
        StopWatch watch = new StopWatch();
        try {
            watch.start();
            connect();
            SqlExecutor.executeBatch(connection, sqlList);
        } catch (Exception e) {
            log.error("sqlite update error!", e);
        } finally {
            close();
            watch.stop();
            log.warn("sqlite update consume: " + watch.getTotalTimeMillis() + "ms");
        }
    }

    /**
     * 创建表(不存在则创建)
     *
     * @param ddlSql    ddl sql
     * @param tableName 表名
     * @author mabin
     * @date 2024/07/25 09:52
     */
    public void createTable(@NotNull String ddlSql, @NotNull String tableName) {
        StopWatch watch = new StopWatch();
        try {
            watch.start();
            connect();
            Number number = SqlExecutor.query(connection, String.format(SqliteConstants.SQL.EXIST_TABLE, tableName), new NumberHandler());
            if (Objects.nonNull(number) && number.intValue() > 0) {
                return;
            }
            SqlExecutor.execute(connection, ddlSql);
        } catch (Exception e) {
            log.error("sqlite createTable error!", e);
        } finally {
            close();
            watch.stop();
            log.warn("sqlite createTable consume: " + watch.getTotalTimeMillis() + "ms");
        }
    }


    /**
     * 创建索引(不存在则创建)
     *
     * @param indexName  索引名称
     * @param tableName  表名
     * @param isUnique   是独一无二的
     * @param columnName 列名
     * @author mabin
     * @date 2024/07/25 16:26
     */
    public void createIndex(@NotNull String indexName, @NotNull String tableName, @NotNull Boolean isUnique, @NotNull String... columnName) {
        StopWatch watch = new StopWatch();
        try {
            watch.start();
            connect();
            Number number = SqlExecutor.query(connection, String.format(SqliteConstants.SQL.EXIST_INDEX, indexName, tableName), new NumberHandler());
            if (Objects.nonNull(number) && number.intValue() > 0) {
                return;
            }
            String ddlSql = String.format(isUnique ? SqliteConstants.SQL.CREATE_TABLE_UNIQUE_INDEX : SqliteConstants.SQL.CREATE_TABLE_INDEX,
                    indexName, tableName, String.join(",", columnName));
            SqlExecutor.execute(connection, ddlSql);
        } catch (Exception e) {
            log.error("sqlite createIndex error!", e);
        } finally {
            close();
            watch.stop();
            log.warn("sqlite createIndex consume: " + watch.getTotalTimeMillis() + "ms");
        }
    }

    /**
     * 数据库资源关闭和释放
     *
     * @author mabin
     * @date 2024/07/24 17:42
     */
    public void close() {
        try {
            if (Objects.nonNull(dataSource) && Objects.nonNull(dataSource.getHikariPoolMXBean())) {
                HikariPoolMXBean hikariPoolMXBean = dataSource.getHikariPoolMXBean();
                log.warn(String.format("HikariCP Before - total: %s, active: %s, idle: %s, waiting: %s", hikariPoolMXBean.getTotalConnections(),
                        hikariPoolMXBean.getActiveConnections(), hikariPoolMXBean.getIdleConnections(), hikariPoolMXBean.getThreadsAwaitingConnection()));
            }
            if (Objects.nonNull(connection) && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
            if (Objects.nonNull(dataSource) && Objects.nonNull(dataSource.getHikariPoolMXBean())) {
                HikariPoolMXBean hikariPoolMXBean = dataSource.getHikariPoolMXBean();
                log.warn(String.format("HikariCP End - total: %s, active: %s, idle: %s, waiting: %s", hikariPoolMXBean.getTotalConnections(),
                        hikariPoolMXBean.getActiveConnections(), hikariPoolMXBean.getIdleConnections(), hikariPoolMXBean.getThreadsAwaitingConnection()));
            }
        } catch (SQLException e) {
            log.error("sqlite destroy error!", e);
        }
    }

}

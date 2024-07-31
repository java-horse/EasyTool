package easy.base;

public class SqliteConstants {
    private SqliteConstants() {
    }

    public static final String DB = "db";

    public interface TABLE {
        String BACKUP = "backup";
    }

    public interface SQL {
        String CREATE_TABLE_BACKUP = """
                CREATE TABLE %s
                (
                    id            integer NOT NULL DEFAULT 0 PRIMARY KEY,
                    source        text    NOT NULL DEFAULT '',
                    target        text    NOT NULL DEFAULT '',
                    channel        text    NOT NULL DEFAULT '',
                    create_time   text    NOT NULL DEFAULT '',
                    modified_time text    NOT NULL DEFAULT '',
                    ide           text    NOT NULL DEFAULT ''
                );
                                """;
        String CREATE_TABLE_INDEX = """
                CREATE INDEX %s ON %s (%s);
                """;
        String CREATE_TABLE_UNIQUE_INDEX = """
                CREATE UNIQUE INDEX %s ON %s (%s);
                """;
        String EXIST_TABLE = """
                SELECT count(*) FROM sqlite_master WHERE type='table' AND name='%s';
                """;
        String EXIST_INDEX = """
                SELECT count(*) FROM sqlite_master WHERE type='index' AND name='%s' AND tbl_name='%s';
                """;
        String QUERY_COUNT = """
                SELECT count(*) FROM %s
                """;
        String QUERY_TABLE = """
                SELECT * FROM %s
                """;
    }

}

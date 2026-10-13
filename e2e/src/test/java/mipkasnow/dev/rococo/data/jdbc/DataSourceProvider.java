package mipkasnow.dev.rococo.data.jdbc;

import com.p6spy.engine.spy.P6DataSource;
import mipkasnow.dev.rococo.config.Config;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum DataSourceProvider {
    INSTANCE;

    private final Map<DataBase, DataSource> store = new ConcurrentHashMap<>();

    public DataSource dataSource(DataBase database) {
        return store.computeIfAbsent(database, this::create);
    }

    private DataSource create(DataBase database) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        Config config = Config.getInstance();
        dataSource.setURL(database.url());
        dataSource.setUser(config.jdbcUsername());
        dataSource.setPassword(config.jdbcPassword());
        return new P6DataSource(dataSource);
    }
}

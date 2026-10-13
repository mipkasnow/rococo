package mipkasnow.dev.rococo.data.repository;

import mipkasnow.dev.rococo.data.jdbc.DataBase;
import mipkasnow.dev.rococo.data.jdbc.DataSourceProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public abstract class BaseRepository {

    protected final TransactionTemplate transactionTemplate;
    protected final JdbcTemplate jdbcTemplate;

    protected BaseRepository(DataBase database) {
        JdbcTransactionManager transactionManager = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.dataSource(database)
        );
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.jdbcTemplate = new JdbcTemplate(requireNonNull(transactionManager.getDataSource()));
    }

    @Nonnull
    protected <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... args) {
        List<T> result = jdbcTemplate.query(sql, mapper, args);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    @Nonnull
    protected UUID generatedId(KeyHolder keyHolder) {
        Map<String, Object> keys = requireNonNull(keyHolder.getKeys());
        Object id = requireNonNull(keys.get("id"));
        if (id instanceof UUID uuid) {
            return uuid;
        }
        return UUID.fromString(id.toString());
    }
}

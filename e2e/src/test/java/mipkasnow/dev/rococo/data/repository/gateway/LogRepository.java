package mipkasnow.dev.rococo.data.repository.gateway;

import mipkasnow.dev.rococo.data.entity.gateway.LogEntity;
import mipkasnow.dev.rococo.data.entity.gateway.rowmapper.LogRowMapper;
import mipkasnow.dev.rococo.data.jdbc.DataBase;
import mipkasnow.dev.rococo.data.repository.BaseRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class LogRepository extends BaseRepository {

    public LogRepository() {
        super(DataBase.GATEWAY);
    }

    @Nonnull
    public List<LogEntity> find(@Nullable String httpMethod,
                                @Nullable String path,
                                @Nullable String username) {
        StringBuilder sql = new StringBuilder(
                """
                        SELECT id, occurred_at, ip, username, http_method, path, http_status, success, error
                        FROM logs
                        WHERE 1 = 1
                        """
        );
        List<Object> args = new ArrayList<>();
        if (httpMethod != null) {
            sql.append(" AND http_method = ?");
            args.add(httpMethod);
        }
        if (path != null) {
            sql.append(" AND path = ?");
            args.add(path);
        }
        if (username != null) {
            sql.append(" AND username = ?");
            args.add(username);
        }
        sql.append(" ORDER BY occurred_at DESC");
        return jdbcTemplate.query(sql.toString(), LogRowMapper.instance, args.toArray());
    }

    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM logs");
    }
}

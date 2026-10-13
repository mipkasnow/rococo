package mipkasnow.dev.rococo.data.entity.gateway.rowmapper;

import mipkasnow.dev.rococo.data.entity.gateway.LogEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;

public class LogRowMapper implements RowMapper<LogEntity> {

    public static final LogRowMapper instance = new LogRowMapper();

    private LogRowMapper() {
    }

    @Override
    public LogEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new LogEntity(
                rs.getObject("id", UUID.class),
                rs.getObject("occurred_at", OffsetDateTime.class).toInstant(),
                rs.getString("ip"),
                rs.getString("username"),
                rs.getString("http_method"),
                rs.getString("path"),
                rs.getInt("http_status"),
                rs.getBoolean("success"),
                rs.getString("error")
        );
    }
}

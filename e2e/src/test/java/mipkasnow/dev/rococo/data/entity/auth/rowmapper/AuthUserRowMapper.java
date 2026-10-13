package mipkasnow.dev.rococo.data.entity.auth.rowmapper;

import mipkasnow.dev.rococo.data.entity.auth.AuthUserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AuthUserRowMapper implements RowMapper<AuthUserEntity> {

    public static final AuthUserRowMapper instance = new AuthUserRowMapper();

    private AuthUserRowMapper() {
    }

    @Override
    public AuthUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AuthUserEntity(
                rs.getObject("id", UUID.class),
                rs.getString("username"),
                rs.getString("password"),
                rs.getBoolean("enabled"),
                rs.getBoolean("account_non_expired"),
                rs.getBoolean("account_non_locked"),
                rs.getBoolean("credentials_non_expired"),
                List.of()
        );
    }
}

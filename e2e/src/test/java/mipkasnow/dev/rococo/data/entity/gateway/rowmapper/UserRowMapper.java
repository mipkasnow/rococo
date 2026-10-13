package mipkasnow.dev.rococo.data.entity.gateway.rowmapper;

import mipkasnow.dev.rococo.data.entity.gateway.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRowMapper implements RowMapper<UserEntity> {

    public static final UserRowMapper instance = new UserRowMapper();

    private UserRowMapper() {
    }

    @Override
    public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserEntity(
                rs.getObject("id", UUID.class),
                rs.getString("username"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getObject("avatar_file_id", UUID.class)
        );
    }
}

package mipkasnow.dev.rococo.data.entity.gateway.rowmapper;

import mipkasnow.dev.rococo.data.entity.gateway.CountryEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CountryRowMapper implements RowMapper<CountryEntity> {

    public static final CountryRowMapper instance = new CountryRowMapper();

    private CountryRowMapper() {
    }

    @Override
    public CountryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CountryEntity(
                rs.getObject("id", UUID.class),
                rs.getString("name")
        );
    }
}

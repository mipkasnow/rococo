package mipkasnow.dev.rococo.data.entity.gateway.rowmapper;

import mipkasnow.dev.rococo.data.entity.gateway.MuseumEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MuseumRowMapper implements RowMapper<MuseumEntity> {

    public static final MuseumRowMapper instance = new MuseumRowMapper();

    private MuseumRowMapper() {
    }

    @Override
    public MuseumEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new MuseumEntity(
                rs.getObject("id", UUID.class),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("city"),
                rs.getObject("country_id", UUID.class),
                rs.getObject("photo_file_id", UUID.class)
        );
    }
}

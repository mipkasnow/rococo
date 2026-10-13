package mipkasnow.dev.rococo.data.entity.gateway.rowmapper;

import mipkasnow.dev.rococo.data.entity.gateway.ArtistEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ArtistRowMapper implements RowMapper<ArtistEntity> {

    public static final ArtistRowMapper instance = new ArtistRowMapper();

    private ArtistRowMapper() {
    }

    @Override
    public ArtistEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ArtistEntity(
                rs.getObject("id", UUID.class),
                rs.getString("name"),
                rs.getString("biography"),
                rs.getObject("photo_file_id", UUID.class)
        );
    }
}

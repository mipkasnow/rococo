package mipkasnow.dev.rococo.data.entity.gateway.rowmapper;

import mipkasnow.dev.rococo.data.entity.gateway.PaintingEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PaintingRowMapper implements RowMapper<PaintingEntity> {

    public static final PaintingRowMapper instance = new PaintingRowMapper();

    private PaintingRowMapper() {
    }

    @Override
    public PaintingEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PaintingEntity(
                rs.getObject("id", UUID.class),
                rs.getString("title"),
                rs.getString("description"),
                rs.getObject("artist_id", UUID.class),
                rs.getObject("museum_id", UUID.class),
                rs.getObject("content_file_id", UUID.class)
        );
    }
}

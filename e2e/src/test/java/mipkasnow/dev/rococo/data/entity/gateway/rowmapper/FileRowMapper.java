package mipkasnow.dev.rococo.data.entity.gateway.rowmapper;

import mipkasnow.dev.rococo.data.entity.gateway.FileEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class FileRowMapper implements RowMapper<FileEntity> {

    public static final FileRowMapper instance = new FileRowMapper();

    private FileRowMapper() {
    }

    @Override
    public FileEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FileEntity(
                rs.getObject("id", UUID.class),
                rs.getString("object_key"),
                rs.getString("bucket"),
                rs.getString("file_name"),
                rs.getString("content_type"),
                rs.getLong("size_bytes"),
                rs.getString("sha256")
        );
    }
}

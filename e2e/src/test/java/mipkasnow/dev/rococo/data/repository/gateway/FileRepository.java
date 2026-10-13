package mipkasnow.dev.rococo.data.repository.gateway;

import mipkasnow.dev.rococo.data.entity.gateway.FileEntity;
import mipkasnow.dev.rococo.data.entity.gateway.rowmapper.FileRowMapper;
import mipkasnow.dev.rococo.data.jdbc.DataBase;
import mipkasnow.dev.rococo.data.repository.BaseRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class FileRepository extends BaseRepository {

    public FileRepository() {
        super(DataBase.GATEWAY);
    }

    @Nonnull
    public Optional<FileEntity> findById(UUID id) {
        return queryOne("SELECT * FROM files WHERE id = ?", FileRowMapper.instance, id);
    }

    public void deleteIfUnreferenced(@Nullable UUID fileId) {
        if (fileId == null) {
            return;
        }
        Integer references = jdbcTemplate.queryForObject(
                """
                        SELECT
                            (SELECT COUNT(*) FROM users WHERE avatar_file_id = ?)
                            + (SELECT COUNT(*) FROM artist WHERE photo_file_id = ?)
                            + (SELECT COUNT(*) FROM museum WHERE photo_file_id = ?)
                            + (SELECT COUNT(*) FROM painting WHERE content_file_id = ?)
                        """,
                Integer.class,
                fileId,
                fileId,
                fileId,
                fileId
        );
        if (references != null && references == 0) {
            jdbcTemplate.update("DELETE FROM files WHERE id = ?", fileId);
        }
    }
}

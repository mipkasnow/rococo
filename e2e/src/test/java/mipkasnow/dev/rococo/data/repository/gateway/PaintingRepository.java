package mipkasnow.dev.rococo.data.repository.gateway;

import mipkasnow.dev.rococo.data.entity.gateway.PaintingEntity;
import mipkasnow.dev.rococo.data.entity.gateway.rowmapper.PaintingRowMapper;
import mipkasnow.dev.rococo.data.jdbc.DataBase;
import mipkasnow.dev.rococo.data.repository.BaseRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class PaintingRepository extends BaseRepository {

    private final FileRepository fileRepository = new FileRepository();

    public PaintingRepository() {
        super(DataBase.GATEWAY);
    }

    @Nonnull
    public Optional<PaintingEntity> findById(UUID id) {
        return queryOne("SELECT * FROM painting WHERE id = ?", PaintingRowMapper.instance, id);
    }

    @Nonnull
    public List<PaintingEntity> findByTitle(String title) {
        return jdbcTemplate.query(
                "SELECT * FROM painting WHERE title = ? ORDER BY title",
                PaintingRowMapper.instance,
                title
        );
    }

    @Nonnull
    public List<UUID> findIdsByArtistId(UUID artistId) {
        return jdbcTemplate.query(
                "SELECT id FROM painting WHERE artist_id = ?",
                (rs, rowNum) -> rs.getObject("id", UUID.class),
                artistId
        );
    }

    public void clearMuseum(UUID museumId) {
        jdbcTemplate.update("UPDATE painting SET museum_id = NULL WHERE museum_id = ?", museumId);
    }

    public void deleteById(UUID id) {
        findById(id).ifPresent(painting -> transactionTemplate.executeWithoutResult(status -> {
            jdbcTemplate.update("DELETE FROM painting WHERE id = ?", painting.id());
            fileRepository.deleteIfUnreferenced(painting.contentFileId());
        }));
    }
}

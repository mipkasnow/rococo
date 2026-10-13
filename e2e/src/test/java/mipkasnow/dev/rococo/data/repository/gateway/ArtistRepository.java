package mipkasnow.dev.rococo.data.repository.gateway;

import mipkasnow.dev.rococo.data.entity.gateway.ArtistEntity;
import mipkasnow.dev.rococo.data.entity.gateway.rowmapper.ArtistRowMapper;
import mipkasnow.dev.rococo.data.jdbc.DataBase;
import mipkasnow.dev.rococo.data.repository.BaseRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ArtistRepository extends BaseRepository {

    private final PaintingRepository paintingRepository = new PaintingRepository();
    private final FileRepository fileRepository = new FileRepository();

    public ArtistRepository() {
        super(DataBase.GATEWAY);
    }

    @Nonnull
    public Optional<ArtistEntity> findById(UUID id) {
        return queryOne("SELECT * FROM artist WHERE id = ?", ArtistRowMapper.instance, id);
    }

    @Nonnull
    public Optional<ArtistEntity> findByName(String name) {
        return queryOne("SELECT * FROM artist WHERE name = ?", ArtistRowMapper.instance, name);
    }

    public void deleteById(UUID id) {
        transactionTemplate.executeWithoutResult(status -> {
            paintingRepository.findIdsByArtistId(id).forEach(paintingRepository::deleteById);
            findById(id).ifPresent(artist -> {
                jdbcTemplate.update("DELETE FROM artist WHERE id = ?", artist.id());
                fileRepository.deleteIfUnreferenced(artist.photoFileId());
            });
        });
    }
}

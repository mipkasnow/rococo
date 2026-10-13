package mipkasnow.dev.rococo.data.repository.gateway;

import mipkasnow.dev.rococo.data.entity.gateway.MuseumEntity;
import mipkasnow.dev.rococo.data.entity.gateway.rowmapper.MuseumRowMapper;
import mipkasnow.dev.rococo.data.jdbc.DataBase;
import mipkasnow.dev.rococo.data.repository.BaseRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class MuseumRepository extends BaseRepository {

    private final PaintingRepository paintingRepository = new PaintingRepository();
    private final FileRepository fileRepository = new FileRepository();

    public MuseumRepository() {
        super(DataBase.GATEWAY);
    }

    @Nonnull
    public Optional<MuseumEntity> findById(UUID id) {
        return queryOne("SELECT * FROM museum WHERE id = ?", MuseumRowMapper.instance, id);
    }

    @Nonnull
    public Optional<MuseumEntity> findByTitle(String title) {
        return queryOne("SELECT * FROM museum WHERE title = ?", MuseumRowMapper.instance, title);
    }

    public void deleteById(UUID id) {
        findById(id).ifPresent(museum -> transactionTemplate.executeWithoutResult(status -> {
            paintingRepository.clearMuseum(museum.id());
            jdbcTemplate.update("DELETE FROM museum WHERE id = ?", museum.id());
            fileRepository.deleteIfUnreferenced(museum.photoFileId());
        }));
    }
}

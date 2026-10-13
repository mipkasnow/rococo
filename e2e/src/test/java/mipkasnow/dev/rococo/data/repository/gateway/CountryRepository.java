package mipkasnow.dev.rococo.data.repository.gateway;

import mipkasnow.dev.rococo.data.entity.gateway.CountryEntity;
import mipkasnow.dev.rococo.data.entity.gateway.rowmapper.CountryRowMapper;
import mipkasnow.dev.rococo.data.jdbc.DataBase;
import mipkasnow.dev.rococo.data.repository.BaseRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class CountryRepository extends BaseRepository {

    public CountryRepository() {
        super(DataBase.GATEWAY);
    }

    @Nonnull
    public List<CountryEntity> findAll() {
        return jdbcTemplate.query("SELECT * FROM country ORDER BY name", CountryRowMapper.instance);
    }

    @Nonnull
    public Optional<CountryEntity> findById(UUID id) {
        return queryOne("SELECT * FROM country WHERE id = ?", CountryRowMapper.instance, id);
    }

    @Nonnull
    public Optional<CountryEntity> findByName(String name) {
        return queryOne("SELECT * FROM country WHERE name = ?", CountryRowMapper.instance, name);
    }
}

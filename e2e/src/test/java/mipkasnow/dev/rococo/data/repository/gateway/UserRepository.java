package mipkasnow.dev.rococo.data.repository.gateway;

import mipkasnow.dev.rococo.data.entity.gateway.UserEntity;
import mipkasnow.dev.rococo.data.entity.gateway.rowmapper.UserRowMapper;
import mipkasnow.dev.rococo.data.jdbc.DataBase;
import mipkasnow.dev.rococo.data.repository.BaseRepository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UserRepository extends BaseRepository {

    private final FileRepository fileRepository = new FileRepository();

    public UserRepository() {
        super(DataBase.GATEWAY);
    }

    @Nonnull
    public UserEntity create(String username) {
        return requireNonNull(transactionTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO users (username) VALUES (?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                statement.setString(1, username);
                return statement;
            }, keyHolder);
            return findById(generatedId(keyHolder)).orElseThrow();
        }));
    }

    @Nonnull
    public Optional<UserEntity> findById(UUID id) {
        return queryOne("SELECT * FROM users WHERE id = ?", UserRowMapper.instance, id);
    }

    @Nonnull
    public Optional<UserEntity> findByUsername(String username) {
        return queryOne("SELECT * FROM users WHERE username = ?", UserRowMapper.instance, username);
    }

    public void deleteByUsername(String username) {
        findByUsername(username).ifPresent(user -> transactionTemplate.executeWithoutResult(status -> {
            jdbcTemplate.update("DELETE FROM users WHERE id = ?", user.id());
            fileRepository.deleteIfUnreferenced(user.avatarFileId());
        }));
    }
}

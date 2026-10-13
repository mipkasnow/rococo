package mipkasnow.dev.rococo.data.repository.auth;

import mipkasnow.dev.rococo.data.entity.auth.AuthUserEntity;
import mipkasnow.dev.rococo.data.entity.auth.rowmapper.AuthUserRowMapper;
import mipkasnow.dev.rococo.data.jdbc.DataBase;
import mipkasnow.dev.rococo.data.repository.BaseRepository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UserRepository extends BaseRepository {

    private static final List<String> DEFAULT_AUTHORITIES = List.of("read", "write");

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserRepository() {
        super(DataBase.AUTH);
    }

    @Nonnull
    public AuthUserEntity create(String username, String rawPassword) {
        return requireNonNull(transactionTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        """
                                INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
                                VALUES (?, ?, true, true, true, true)
                                """,
                        Statement.RETURN_GENERATED_KEYS
                );
                statement.setString(1, username);
                statement.setString(2, passwordEncoder.encode(rawPassword));
                return statement;
            }, keyHolder);

            UUID userId = generatedId(keyHolder);
            jdbcTemplate.batchUpdate(
                    "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                    DEFAULT_AUTHORITIES,
                    DEFAULT_AUTHORITIES.size(),
                    (statement, authority) -> {
                        statement.setObject(1, userId);
                        statement.setString(2, authority);
                    }
            );
            return findById(userId).orElseThrow();
        }));
    }

    @Nonnull
    public Optional<AuthUserEntity> findById(UUID id) {
        return queryOne(
                """
                        SELECT id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired
                        FROM users
                        WHERE id = ?
                        """,
                AuthUserRowMapper.instance,
                id
        ).map(this::withAuthorities);
    }

    @Nonnull
    public Optional<AuthUserEntity> findByUsername(String username) {
        return queryOne(
                """
                        SELECT id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired
                        FROM users
                        WHERE username = ?
                        """,
                AuthUserRowMapper.instance,
                username
        ).map(this::withAuthorities);
    }

    public void deleteByUsername(String username) {
        findByUsername(username).ifPresent(user -> transactionTemplate.executeWithoutResult(status -> {
            jdbcTemplate.update("DELETE FROM authority WHERE user_id = ?", user.id());
            jdbcTemplate.update("DELETE FROM users WHERE id = ?", user.id());
        }));
    }

    @Nonnull
    private AuthUserEntity withAuthorities(AuthUserEntity user) {
        List<String> authorities = jdbcTemplate.query(
                "SELECT authority FROM authority WHERE user_id = ? ORDER BY authority",
                (rs, rowNum) -> rs.getString("authority"),
                user.id()
        );
        return user.withAuthorities(authorities);
    }
}

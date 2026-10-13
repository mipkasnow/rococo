package mipkasnow.dev.rococo.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface Config {

    @Nonnull
    static Config getInstance() {
        if ("docker".equals(System.getProperty("test.env"))) {
            return DockerConfig.INSTANCE;
        }
        return LocalConfig.INSTANCE;
    }

    @Nonnull
    String frontUrl();

    @Nonnull
    String authUrl();

    @Nonnull
    String gatewayUrl();

    @Nonnull
    String authJdbcUrl();

    @Nonnull
    String gatewayJdbcUrl();

    @Nonnull
    String jdbcUsername();

    @Nonnull
    String jdbcPassword();

    @Nonnull
    String s3Endpoint();

    @Nonnull
    String s3Region();

    @Nonnull
    String s3AccessKey();

    @Nonnull
    String s3SecretKey();
}

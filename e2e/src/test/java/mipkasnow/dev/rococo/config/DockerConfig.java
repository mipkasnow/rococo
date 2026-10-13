package mipkasnow.dev.rococo.config;

import javax.annotation.Nonnull;

enum DockerConfig implements Config {
    INSTANCE;

    @Override
    @Nonnull
    public String frontUrl() {
        return "";
    }

    @Override
    @Nonnull
    public String authUrl() {
        return "";
    }

    @Override
    @Nonnull
    public String gatewayUrl() {
        return "";
    }

    @Override
    @Nonnull
    public String authJdbcUrl() {
        return "";
    }

    @Override
    @Nonnull
    public String gatewayJdbcUrl() {
        return "";
    }

    @Override
    @Nonnull
    public String jdbcUsername() {
        return "";
    }

    @Override
    @Nonnull
    public String jdbcPassword() {
        return "";
    }

    @Override
    @Nonnull
    public String s3Endpoint() {
        return "";
    }

    @Override
    @Nonnull
    public String s3Region() {
        return "";
    }

    @Override
    @Nonnull
    public String s3AccessKey() {
        return "";
    }

    @Override
    @Nonnull
    public String s3SecretKey() {
        return "";
    }
}

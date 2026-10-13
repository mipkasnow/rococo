package mipkasnow.dev.rococo.config;

import javax.annotation.Nonnull;

enum LocalConfig implements Config {
    INSTANCE;

    @Override
    @Nonnull
    public String frontUrl() {
        return "http://localhost:3000/";
    }

    @Override
    @Nonnull
    public String authUrl() {
        return "http://localhost:9000/";
    }

    @Override
    @Nonnull
    public String gatewayUrl() {
        return "http://localhost:8080/";
    }

    @Override
    @Nonnull
    public String authJdbcUrl() {
        return "jdbc:postgresql://localhost:5433/rococo_auth";
    }

    @Override
    @Nonnull
    public String gatewayJdbcUrl() {
        return "jdbc:postgresql://localhost:5433/rococo_gateway";
    }

    @Override
    @Nonnull
    public String jdbcUsername() {
        return "postgres";
    }

    @Override
    @Nonnull
    public String jdbcPassword() {
        return "secret";
    }

    @Override
    @Nonnull
    public String s3Endpoint() {
        return "http://localhost:9100";
    }

    @Override
    @Nonnull
    public String s3Region() {
        return "us-east-1";
    }

    @Override
    @Nonnull
    public String s3AccessKey() {
        return "user";
    }

    @Override
    @Nonnull
    public String s3SecretKey() {
        return "password";
    }
}

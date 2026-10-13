package mipkasnow.dev.rococo.data.jdbc;

import mipkasnow.dev.rococo.config.Config;

public enum DataBase {
    AUTH {
        @Override
        public String url() {
            return Config.getInstance().authJdbcUrl();
        }
    },
    GATEWAY {
        @Override
        public String url() {
            return Config.getInstance().gatewayJdbcUrl();
        }
    };

    public abstract String url();
}

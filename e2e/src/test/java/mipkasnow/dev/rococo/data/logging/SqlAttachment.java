package mipkasnow.dev.rococo.data.logging;

import io.qameta.allure.attachment.AttachmentData;
import lombok.Getter;

public class SqlAttachment implements AttachmentData {

    private final String name;
    @Getter
    private final String sql;

    public SqlAttachment(String name, String sql) {
        this.name = name;
        this.sql = sql;
    }

    @Override
    public String getName() {
        return name;
    }

}

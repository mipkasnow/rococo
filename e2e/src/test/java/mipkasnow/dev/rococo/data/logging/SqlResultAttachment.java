package mipkasnow.dev.rococo.data.logging;

import io.qameta.allure.attachment.AttachmentData;
import lombok.Getter;

import java.util.List;

public class SqlResultAttachment implements AttachmentData {

    private final String name;
    @Getter
    private final int rowCount;
    @Getter
    private final List<String> headers;
    @Getter
    private final List<List<String>> rows;

    public SqlResultAttachment(String name, int rowCount, List<String> headers, List<List<String>> rows) {
        this.name = name;
        this.rowCount = rowCount;
        this.headers = headers;
        this.rows = rows;
    }

    @Override
    public String getName() {
        return name;
    }

    @Getter
    public static final class ColumnValue {
        private final String column;
        private final String value;

        public ColumnValue(String column, String value) {
            this.column = column;
            this.value = value;
        }
    }
}

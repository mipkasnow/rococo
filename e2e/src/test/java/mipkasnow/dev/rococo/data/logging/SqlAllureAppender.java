package mipkasnow.dev.rococo.data.logging;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.languages.Dialect;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.StdoutLogger;
import io.qameta.allure.attachment.AttachmentData;
import io.qameta.allure.attachment.AttachmentProcessor;
import io.qameta.allure.attachment.DefaultAttachmentProcessor;
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;
import mipkasnow.dev.rococo.data.logging.SqlResultAttachment.ColumnValue;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SqlAllureAppender extends StdoutLogger {

    private final AttachmentProcessor<AttachmentData> attachmentProcessor = new DefaultAttachmentProcessor();

    @Override
    public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
        if (StringUtils.isEmpty(sql) || category == Category.RESULTSET) {
            return;
        }
        String database = databaseName(url);
        SqlAttachment attachment = new SqlAttachment(
                sql.split("\\s+")[0].toUpperCase() + " query to: " + database,
                SqlFormatter.of(Dialect.PostgreSql).format(sql)
        );
        attachmentProcessor.addAttachment(attachment, new FreemarkerAttachmentRenderer("sql-query.ftl"));
    }

    void attachResult(String query, String url, List<List<ColumnValue>> capturedRows) {
        List<String> headers = new ArrayList<>();
        for (List<ColumnValue> capturedRow : capturedRows) {
            for (ColumnValue cell : capturedRow) {
                if (!headers.contains(cell.getColumn())) {
                    headers.add(cell.getColumn());
                }
            }
        }
        List<List<String>> rows = new ArrayList<>();
        for (List<ColumnValue> capturedRow : capturedRows) {
            List<String> values = new ArrayList<>();
            for (String header : headers) {
                String value = "";
                for (ColumnValue cell : capturedRow) {
                    if (header.equals(cell.getColumn())) {
                        value = cell.getValue();
                        break;
                    }
                }
                values.add(value);
            }
            rows.add(values);
        }
        int rowCount = rows.size();
        String verb = StringUtils.isBlank(query) ? "RESULT" : query.split("\\s+")[0].toUpperCase();
        String database = databaseName(url);
        String name = rowCount + (rowCount == 1 ? " row of " : " rows of ") + verb + " query";
        if (StringUtils.isNotEmpty(database)) {
            name = name + " to: " + database;
        }
        SqlResultAttachment attachment = new SqlResultAttachment(name, rowCount, headers, rows);
        attachmentProcessor.addAttachment(attachment, new FreemarkerAttachmentRenderer("sql-result.ftl"));
    }

    private static String databaseName(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        return StringUtils.substringAfterLast(StringUtils.substringBefore(url, "?"), "/");
    }
}

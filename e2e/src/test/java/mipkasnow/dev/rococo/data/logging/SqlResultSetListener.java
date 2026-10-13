package mipkasnow.dev.rococo.data.logging;

import com.p6spy.engine.common.P6LogQuery;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.Value;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.spy.appender.P6Logger;
import mipkasnow.dev.rococo.data.logging.SqlResultAttachment.ColumnValue;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class SqlResultSetListener extends JdbcEventListener {

    private static final int MAX_VALUE_LENGTH = 300;

    private final ThreadLocal<Map<ResultSetInformation, Pending>> pending = ThreadLocal.withInitial(IdentityHashMap::new);

    @Override
    public void onBeforeResultSetNext(ResultSetInformation resultSetInformation) {
        capture(resultSetInformation);
    }

    @Override
    public void onAfterResultSetClose(ResultSetInformation resultSetInformation, SQLException e) {
        capture(resultSetInformation);
        publish(resultSetInformation);
    }

    private void capture(ResultSetInformation resultSetInformation) {
        int rowNumber = resultSetInformation.getCurrRow();
        if (rowNumber < 0 || resultSetInformation.getResultMap().isEmpty()) {
            return;
        }
        Map<ResultSetInformation, Pending> results = pending.get();
        Pending current = results.get(resultSetInformation);
        if (current == null) {
            current = new Pending();
            results.put(resultSetInformation, current);
        }
        if (current.lastCapturedRow == rowNumber) {
            return;
        }
        List<ColumnValue> row = new ArrayList<>();
        for (Map.Entry<String, Value> entry : resultSetInformation.getResultMap().entrySet()) {
            Value value = entry.getValue();
            row.add(new ColumnValue(entry.getKey(), display(value == null ? null : value.getValue())));
        }
        current.rows.add(row);
        current.lastCapturedRow = rowNumber;
    }

    private void publish(ResultSetInformation resultSetInformation) {
        Pending current = pending.get().remove(resultSetInformation);
        if (pending.get().isEmpty()) {
            pending.remove();
        }
        List<List<ColumnValue>> rows = current == null ? List.of() : current.rows;
        String query = resultSetInformation.getSql();
        if (rows.isEmpty() && StringUtils.isBlank(query)) {
            return;
        }
        P6Logger logger = P6LogQuery.getLogger();
        if (logger instanceof SqlAllureAppender appender) {
            String url = resultSetInformation.getConnectionInformation() == null
                    ? ""
                    : resultSetInformation.getConnectionInformation().getUrl();
            appender.attachResult(query, url, rows);
        }
    }

    private static String display(Object value) {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof byte[]) {
            return "[binary]";
        }
        String text = String.valueOf(value);
        if (text.length() <= MAX_VALUE_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_VALUE_LENGTH) + "... (" + text.length() + " chars)";
    }

    private static final class Pending {
        private final List<List<ColumnValue>> rows = new ArrayList<>();
        private int lastCapturedRow = -1;
    }
}

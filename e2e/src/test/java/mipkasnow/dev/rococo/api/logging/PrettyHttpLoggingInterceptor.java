package mipkasnow.dev.rococo.api.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.GzipSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public final class PrettyHttpLoggingInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(PrettyHttpLoggingInterceptor.class);
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Pattern DATA_IMAGE = Pattern.compile(
            "data:image/[\\w.+-]+;base64,[A-Za-z0-9+/=\\r\\n]*"
    );
    private static final boolean LOG_BASE64_PREFIX_ONLY = true;
    private static final int BASE64_PREFIX_LENGTH = 32;
    private static final int PEEK_LIMIT = 1024 * 1024;
    private static final int PRINT_LIMIT = 8 * 1024;
    private static final String RESET = "\u001B[0m";
    private static final String REQUEST_COLOR = "\u001B[1;34m";
    private static final String RESPONSE_COLOR = "\u001B[1;32m";
    private static final String METHOD_COLOR = "\u001B[1;35m";
    private static final String URI_COLOR = "\u001B[1;4;34m";
    private static final String STATUS_COLOR = "\u001B[1;32m";
    private static final String TIME_COLOR = "\u001B[32m";
    private static final String ERROR_COLOR = "\u001B[1;31m";
    private static final String LINE_COLOR = "\u001B[34m";
    private static final long SLOW_RESPONSE_MS = 1000;

    @Override
    @Nonnull
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startedAt = System.nanoTime();
        Response response = chain.proceed(request);
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
        log.info(format(request, response, elapsedMs));
        return response;
    }

    @Nonnull
    private static String format(Request request, Response response, long elapsedMs) {
        StringBuilder builder = new StringBuilder();
        builder.append('\n');
        builder.append(box("REQUEST", REQUEST_COLOR));
        builder.append("Method: ").append(paint(METHOD_COLOR, request.method())).append('\n');
        builder.append("URI: ").append(paint(URI_COLOR, request.url().toString())).append('\n');
        appendHeaders(builder, request.headers());
        appendBody(builder, requestBody(request), REQUEST_COLOR);
        builder.append(paint(LINE_COLOR, "─".repeat(48))).append('\n');
        boolean failed = response.code() >= 400;
        String statusColor = failed ? ERROR_COLOR : STATUS_COLOR;
        String timeColor = elapsedMs > SLOW_RESPONSE_MS ? ERROR_COLOR : TIME_COLOR;
        builder.append(box("RESPONSE", RESPONSE_COLOR));
        builder.append("Status: ").append(paint(statusColor, status(response))).append('\n');
        builder.append("Time: ").append(paint(timeColor, elapsedMs + " ms")).append('\n');
        appendHeaders(builder, response.headers());
        appendBody(builder, responseBody(response), failed ? ERROR_COLOR : RESPONSE_COLOR);
        return builder.toString().stripTrailing();
    }

    @Nonnull
    private static String box(String title, String color) {
        String border = "─".repeat(title.length() + 2);
        return paint(color, "┌" + border + "┐") + "\n"
                + paint(color, "│ " + title + " │") + "\n"
                + paint(color, "└" + border + "┘") + "\n";
    }

    @Nonnull
    private static String paint(String color, String text) {
        return color + text + RESET;
    }

    @Nonnull
    private static String status(Response response) {
        String protocol = response.protocol().toString().toUpperCase();
        String message = response.message();
        if (message.isBlank()) {
            return response.code() + " " + protocol + " " + response.code();
        }
        return response.code() + " " + protocol + " " + response.code() + " " + message;
    }

    private static void appendHeaders(StringBuilder builder, Headers headers) {
        if (headers.size() == 0) {
            return;
        }
        builder.append("Headers:\n");
        for (int i = 0; i < headers.size(); i++) {
            builder.append("    ")
                    .append(headers.name(i))
                    .append(": ")
                    .append(headers.value(i))
                    .append('\n');
        }
    }

    private static void appendBody(StringBuilder builder, @Nullable String body, String color) {
        if (body == null || body.isBlank()) {
            return;
        }
        builder.append(box("BODY", color));
        String normalized = body.endsWith("\n") ? body.substring(0, body.length() - 1) : body;
        for (String line : normalized.split("\n", -1)) {
            builder.append(paint(color, line)).append('\n');
        }
    }

    @Nullable
    private static String requestBody(Request request) {
        RequestBody body = request.body();
        if (body == null) {
            return null;
        }
        MediaType type = body.contentType();
        if (isBinary(type)) {
            return binary(type, length(body));
        }
        if (body.isOneShot() || body.isDuplex()) {
            return "[body omitted]";
        }
        try (Buffer buffer = new Buffer()) {
            body.writeTo(buffer);
            String text = buffer.readString(charset(type));
            if (text.isBlank()) {
                return null;
            }
            return present(type, text);
        } catch (IOException exception) {
            return "[unreadable body]";
        }
    }

    @Nullable
    private static String responseBody(Response response) {
        ResponseBody body = response.body();
        MediaType type = body.contentType();
        if (isBinary(type)) {
            return binary(type, body.contentLength());
        }
        try {
            ResponseBody peeked = response.peekBody(PEEK_LIMIT);
            String text = isGzip(response) ? gunzip(peeked.bytes(), charset(type)) : peeked.string();
            if (text.isBlank()) {
                return null;
            }
            return present(type, text);
        } catch (IOException exception) {
            return "[unreadable body]";
        }
    }

    private static long length(RequestBody body) {
        try {
            return body.contentLength();
        } catch (IOException exception) {
            return -1;
        }
    }

    @Nonnull
    private static String gunzip(byte[] bytes, Charset charset) throws IOException {
        try (Buffer compressed = new Buffer(); Buffer decoded = new Buffer()) {
            compressed.write(bytes);
            try (GzipSource source = new GzipSource(compressed)) {
                decoded.writeAll(source);
            }
            return decoded.readString(charset);
        }
    }

    private static boolean isGzip(Response response) {
        String encoding = response.header("Content-Encoding");
        return encoding != null && encoding.toLowerCase().contains("gzip");
    }

    private static boolean isBinary(@Nullable MediaType type) {
        if (type == null) {
            return false;
        }
        String subtype = type.subtype().toLowerCase();
        return !"text".equals(type.type())
                && !subtype.contains("json")
                && !subtype.contains("xml")
                && !subtype.contains("html")
                && !subtype.contains("x-www-form-urlencoded")
                && !subtype.contains("javascript");
    }

    @Nonnull
    private static Charset charset(@Nullable MediaType type) {
        if (type == null) {
            return StandardCharsets.UTF_8;
        }
        Charset charset = type.charset(StandardCharsets.UTF_8);
        return charset == null ? StandardCharsets.UTF_8 : charset;
    }

    @Nonnull
    private static String binary(@Nullable MediaType type, long length) {
        String kind = type == null ? "binary" : type.toString();
        if (length >= 0) {
            return "[" + kind + ", " + length + " bytes]";
        }
        return "[" + kind + "]";
    }

    @Nonnull
    private static String present(@Nullable MediaType type, String text) {
        boolean keepFullImage = !LOG_BASE64_PREFIX_ONLY && DATA_IMAGE.matcher(text).find();
        String prepared = trimBase64(text);
        if (type != null && type.subtype().toLowerCase().contains("json")) {
            prepared = prettyJson(prepared);
        }
        if (!keepFullImage && prepared.length() > PRINT_LIMIT) {
            return prepared.substring(0, PRINT_LIMIT) + "\n[truncated]";
        }
        return prepared;
    }

    @Nonnull
    private static String trimBase64(String text) {
        if (!LOG_BASE64_PREFIX_ONLY) {
            return text;
        }
        Matcher matcher = DATA_IMAGE.matcher(text);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String value = matcher.group();
            int comma = value.indexOf(',');
            String header = value.substring(0, comma + 1);
            String payload = value.substring(comma + 1).replace("\r", "").replace("\n", "");
            String visible = payload.length() <= BASE64_PREFIX_LENGTH
                    ? header + payload
                    : header + payload.substring(0, BASE64_PREFIX_LENGTH) + "...";
            matcher.appendReplacement(result, Matcher.quoteReplacement(visible));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    @Nonnull
    private static String prettyJson(String text) {
        try {
            return JSON.writerWithDefaultPrettyPrinter().writeValueAsString(JSON.readTree(text));
        } catch (IOException exception) {
            return text;
        }
    }
}

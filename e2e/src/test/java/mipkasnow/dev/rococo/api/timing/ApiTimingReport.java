package mipkasnow.dev.rococo.api.timing;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@ParametersAreNonnullByDefault
@UtilityClass
public final class ApiTimingReport {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String TOKEN = "__API_TIMING_DATA__";
    private static final DateTimeFormatter LOCAL_SECOND_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void write(Instant startedAt, Instant finishedAt, List<ApiCallSample> calls) {
        try {
            ZoneId zone = ZoneId.systemDefault();
            DateTimeFormatter formatter = LOCAL_SECOND_FORMAT.withZone(zone);
            String startedLocal = formatter.format(startedAt);
            String finishedLocal = formatter.format(finishedAt);
            String html = render(startedAt, finishedAt, zone, startedLocal, finishedLocal, calls);

            Path report = reportFile();
            Path directory = report.getParent();
            if (directory == null) {
                throw new IllegalStateException(report.toString());
            }
            Files.createDirectories(directory);
            Files.writeString(report, html);

            Path allureDir = allureDir();
            Files.createDirectories(allureDir);
            String uuid = UUID.randomUUID().toString();
            String attachment = "api-performance-" + uuid + ".html";
            Files.writeString(allureDir.resolve(attachment), html);
            Files.writeString(
                    allureDir.resolve(uuid + "-result.json"),
                    JSON.writeValueAsString(allureResult(uuid, attachment, startedAt, finishedAt, startedLocal, finishedLocal))
            );
            log.info("API performance report: {}", report.toAbsolutePath());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private static String render(
            Instant startedAt,
            Instant finishedAt,
            ZoneId zone,
            String startedLocal,
            String finishedLocal,
            List<ApiCallSample> calls
    ) throws IOException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("startedAt", startedAt.toEpochMilli());
        payload.put("finishedAt", finishedAt.toEpochMilli());
        payload.put("zone", zone.getId());
        payload.put("startedLocal", startedLocal);
        payload.put("finishedLocal", finishedLocal);
        payload.put("calls", calls);
        String json = JSON.writeValueAsString(payload)
                .replace("\u2028", "\\u2028")
                .replace("\u2029", "\\u2029")
                .replace("<", "\\u003c");
        String template = template();
        int index = template.indexOf(TOKEN);
        if (index < 0 || template.indexOf(TOKEN, index + TOKEN.length()) >= 0) {
            throw new IllegalStateException(TOKEN);
        }
        return template.substring(0, index) + json + template.substring(index + TOKEN.length());
    }

    private static String template() throws IOException {
        try (InputStream stream = ApiTimingReport.class.getClassLoader().getResourceAsStream("tpl/api-performance.html")) {
            return new String(Objects.requireNonNull(stream, "tpl/api-performance.html").readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Map<String, Object> allureResult(
            String uuid,
            String attachment,
            Instant startedAt,
            Instant finishedAt,
            String startedLocal,
            String finishedLocal
    ) {
        String date = startedLocal.substring(0, 10);
        String startTime = startedLocal.substring(11);
        String endTime = finishedLocal.substring(11);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("uuid", uuid);
        result.put("name", "Производительность API %s %s–%s".formatted(date, startTime, endTime));
        result.put("status", "passed");
        result.put("stage", "finished");
        result.put("start", startedAt.toEpochMilli());
        result.put("stop", finishedAt.toEpochMilli());
        result.put("fullName", "Статистика производительности");
        result.put("testCaseId", "rococo-api-performance");
        result.put("historyId", "rococo-api-performance");
        result.put("attachments", List.of(Map.of(
                "name", "Отчёт",
                "source", attachment,
                "type", "text/html"
        )));
        result.put("labels", List.of(
                Map.of("name", "epic", "value", "Статистика производительности"),
                Map.of("name", "suite", "value", "Статистика производительности"),
                Map.of("name", "testClass", "value", "Производительность API"),
                Map.of("name", "package", "value", "Статистика производительности")
        ));
        return result;
    }

    private static Path allureDir() {
        String configured = System.getProperty("allure.results.directory");
        if (configured == null || configured.isBlank()) {
            return Path.of("build", "allure-results");
        }
        return Path.of(configured);
    }

    private static Path reportFile() {
        Path parent = allureDir().getParent();
        Path build = parent == null ? Path.of("build") : parent;
        return build.resolve("reports").resolve("api-performance").resolve("index.html");
    }
}

package mipkasnow.dev.rococo.utils;

import lombok.experimental.UtilityClass;
import mipkasnow.dev.rococo.config.Config;
import mipkasnow.dev.rococo.data.jdbc.DataBase;
import mipkasnow.dev.rococo.data.jdbc.DataSourceProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public final class ClearDataUtils {

    private static final String FLYWAY_HISTORY = "flyway_schema_history";
    private static final String COUNTRY = "country";

    public static void clear() {
        clearDatabase(DataBase.AUTH, FLYWAY_HISTORY);
        clearDatabase(DataBase.GATEWAY, FLYWAY_HISTORY, COUNTRY);
        clearMinio();
    }

    private static void clearDatabase(DataBase database, String... excludedTables) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceProvider.INSTANCE.dataSource(database));
        Set<String> excluded = Set.of(excludedTables);
        List<String> tables = jdbcTemplate.queryForList("""
                        SELECT table_name
                        FROM information_schema.tables
                        WHERE table_schema = 'public'
                          AND table_type = 'BASE TABLE'
                        ORDER BY table_name
                        """, String.class).stream()
                .filter(name -> !excluded.contains(name))
                .toList();
        if (tables.isEmpty()) {
            return;
        }
        String sql = tables.stream()
                .map(ClearDataUtils::quote)
                .collect(Collectors.joining(", ", "TRUNCATE ", " RESTART IDENTITY CASCADE"));
        jdbcTemplate.execute(sql);
    }

    private static String quote(String table) {
        if (!table.matches("[a-z_][a-z0-9_]*")) {
            throw new IllegalStateException("Unexpected table name: " + table);
        }
        return "\"" + table + "\"";
    }

    private static void clearMinio() {
        try (S3Client client = s3Client()) {
            client.listBuckets().buckets().forEach(bucket -> clearBucket(client, bucket.name()));
        }
    }

    private static void clearBucket(S3Client client, String bucket) {
        String token = null;
        do {
            ListObjectsV2Request.Builder request = ListObjectsV2Request.builder().bucket(bucket);
            if (token != null) {
                request.continuationToken(token);
            }
            ListObjectsV2Response page = client.listObjectsV2(request.build());
            if (!page.contents().isEmpty()) {
                List<ObjectIdentifier> objects = page.contents().stream()
                        .map(object -> ObjectIdentifier.builder().key(object.key()).build())
                        .toList();
                client.deleteObjects(DeleteObjectsRequest.builder()
                        .bucket(bucket)
                        .delete(Delete.builder().objects(objects).quiet(true).build())
                        .build());
            }
            token = Boolean.TRUE.equals(page.isTruncated()) ? page.nextContinuationToken() : null;
        } while (token != null);
    }

    private static S3Client s3Client() {
        Config config = Config.getInstance();
        return S3Client.builder()
                .endpointOverride(URI.create(config.s3Endpoint()))
                .region(Region.of(config.s3Region()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(config.s3AccessKey(), config.s3SecretKey())
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .chunkedEncodingEnabled(false)
                        .build())
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .build();
    }
}

package mipkasnow.dev.rococo.api.timing;

public record ApiCallSample(
        long at,
        String method,
        String url,
        String endpoint,
        int status,
        long ms,
        String test
) {
}

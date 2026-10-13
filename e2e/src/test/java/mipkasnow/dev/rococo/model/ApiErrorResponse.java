package mipkasnow.dev.rococo.model;

import java.util.List;

public record ApiErrorResponse(
        List<String> errors,
        String errorCode,
        String timeStamp,
        String url
) {
}

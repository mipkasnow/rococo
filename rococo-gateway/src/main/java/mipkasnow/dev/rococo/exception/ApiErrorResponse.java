package mipkasnow.dev.rococo.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        List<String> errors,
        ErrorCode errorCode,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timeStamp,
        String url
) {

    public static ApiErrorResponse of(HttpServletRequest request, ErrorCode errorCode) {
        return of(request, errorCode, List.of(errorCode.getMessage()));
    }

    public static ApiErrorResponse of(HttpServletRequest request, ErrorCode errorCode, List<String> errors) {
        return new ApiErrorResponse(errors, errorCode, LocalDateTime.now(), requestUrl(request));
    }

    private static String requestUrl(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.isBlank()) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + query;
    }
}

package mipkasnow.dev.rococo.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mipkasnow.dev.rococo.exception.ApiException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiRequestLogBuilder {

    private static final int MAX_STACK_FRAMES = 3;
    private static final String NO_CAUSE = "no_cause";
    private static final String NO_INFO = "no_info";
    private static final String ANONYMOUS = "anonymous";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private final RequestBodySanitizer sanitizer;

    public void log(HttpServletRequest request, HttpServletResponse response) {
        try {
            String line = line(request, response);
            if (hasError(request, response)) {
                log.error(line);
            } else {
                log.info(line);
            }
        } catch (Exception exception) {
            log.error("Failed to write API request log: {}", exception.toString());
        }
    }

    String line(HttpServletRequest request, HttpServletResponse response) {
        Throwable exception = RequestLogAttributes.exception(request);
        ErrorFields error = errorFields(exception);
        return "[%s|%s|%s|%s|%s|%s|%s|%s|%s|%s]".formatted(
                field(clientIp(request)),
                field(username(request)),
                colored(BLUE, field(request.getMethod())),
                field(path(request)),
                colored(statusColor(response.getStatus()), Integer.toString(response.getStatus())),
                requestBody(request),
                responseBody(response),
                field(error.exception()),
                field(error.cause()),
                field(error.info())
        );
    }

    ApiLogEvent event(HttpServletRequest request, HttpServletResponse response) {
        Throwable exception = RequestLogAttributes.exception(request);
        ErrorFields error = errorFields(exception);
        int status = response.getStatus();
        return new ApiLogEvent(
                UUID.randomUUID(),
                Instant.now(),
                limit(field(clientIp(request)), 64),
                limit(field(username(request)), 255),
                limit(field(request.getMethod()), 16),
                limit(field(path(request)), 2048),
                status,
                exception == null && status < 400,
                exception == null ? null : field(error.exception()) + "|" + field(error.cause()) + "|" + field(error.info())
        );
    }

    private boolean hasError(HttpServletRequest request, HttpServletResponse response) {
        return RequestLogAttributes.exception(request) != null || response.getStatus() >= 400;
    }

    private ErrorFields errorFields(Throwable exception) {
        if (exception == null) {
            return new ErrorFields("", "", "");
        }
        if (exception instanceof ApiException apiException) {
            String cause = apiException.getCause() == null
                    ? NO_CAUSE
                    : apiException.getCause().getClass().getSimpleName() + ":" + apiException.getCause().getMessage();
            return new ErrorFields("ApiException." + apiException.getErrorCode().name(), cause, NO_INFO);
        }
        return new ErrorFields(
                exception.getClass().getSimpleName() + ":" + exception.getMessage(),
                NO_CAUSE,
                stack(exception)
        );
    }

    private String stack(Throwable exception) {
        StackTraceElement[] trace = exception.getStackTrace();
        if (trace == null || trace.length == 0) {
            return NO_INFO;
        }
        int frames = Math.min(trace.length, MAX_STACK_FRAMES);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < frames; i++) {
            if (i > 0) {
                builder.append('/');
            }
            builder.append(trace[i].getClassName()).append(':').append(trace[i].getLineNumber());
        }
        return builder.toString();
    }

    private String requestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            return sanitizer.sanitize(wrapper.getContentAsByteArray(), request.getContentType());
        }
        return "";
    }

    private String responseBody(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper wrapper) {
            return sanitizer.sanitize(wrapper.getContentAsByteArray(), response.getContentType());
        }
        return "";
    }

    private String path(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.isEmpty()) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + query;
    }

    private String username(HttpServletRequest request) {
        Authentication authentication = authentication(request);
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ANONYMOUS;
        }
        String name = authentication.getName();
        if (name == null || name.isBlank()) {
            return ANONYMOUS;
        }
        return name;
    }

    private Authentication authentication(HttpServletRequest request) {
        HttpServletRequest current = request;
        while (current != null) {
            Object value = current.getAttribute(RequestAttributeSecurityContextRepository.DEFAULT_REQUEST_ATTR_NAME);
            if (value instanceof SecurityContext context && context.getAuthentication() != null) {
                return context.getAuthentication();
            }
            if (!(current instanceof HttpServletRequestWrapper wrapper)
                    || !(wrapper.getRequest() instanceof HttpServletRequest next)
                    || next == current) {
                return null;
            }
            current = next;
        }
        return null;
    }

    private String colored(String color, String value) {
        return color + value + RESET;
    }

    private String statusColor(int status) {
        return status >= 400 ? RED : GREEN;
    }

    private String clientIp(HttpServletRequest request) {
        String realIp = request.getHeader("X-Real-IP");
        if (realIp == null || realIp.isBlank()) {
            return request.getRemoteAddr();
        }
        return realIp;
    }

    private String field(String value) {
        return RequestBodySanitizer.flatten(value == null ? "" : value);
    }

    private String limit(String value, int max) {
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }

    private record ErrorFields(String exception, String cause, String info) {
    }
}

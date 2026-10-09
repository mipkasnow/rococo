package mipkasnow.dev.rococo.exception;

import jakarta.servlet.http.HttpServletRequest;
import mipkasnow.dev.rococo.log.RequestLogAttributes;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException exception, HttpServletRequest request) {
        RequestLogAttributes.setException(request, exception);
        return response(request, exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception,
                                                             HttpServletRequest request) {
        RequestLogAttributes.setException(request, exception);
        List<String> errors = exception.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .filter(message -> message != null && !message.isBlank())
                .distinct()
                .toList();
        if (errors.isEmpty()) {
            errors = List.of(ErrorCode.BadRequest.getMessage());
        }
        return response(request, ErrorCode.BadRequest, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException exception,
                                                                    HttpServletRequest request) {
        RequestLogAttributes.setException(request, exception);
        return response(request, ErrorCode.BadRequest);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException exception,
                                                                HttpServletRequest request) {
        String detail = exception.getMostSpecificCause().getMessage();
        ErrorCode errorCode = ErrorCode.Unexpected;
        if (detail != null && detail.contains("artist_name_key")) {
            errorCode = ErrorCode.ArtistNameTaken;
        } else if (detail != null && detail.contains("museum_title_key")) {
            errorCode = ErrorCode.MuseumTitleTaken;
        }
        RequestLogAttributes.setException(request, new ApiException(errorCode, exception));
        return response(request, errorCode);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) throws Exception {
        if (exception instanceof AccessDeniedException || exception instanceof AuthenticationException) {
            throw exception;
        }
        Throwable root = NestedExceptionUtils.getMostSpecificCause(exception);
        if (root instanceof ApiException apiException) {
            RequestLogAttributes.setException(request, apiException);
            return response(request, apiException);
        }
        RequestLogAttributes.setException(request, exception);
        return response(request, ErrorCode.Unexpected);
    }

    private ResponseEntity<ApiErrorResponse> response(HttpServletRequest request, ApiException exception) {
        return response(request, exception.getErrorCode(), List.of(exception.getMessage()));
    }

    private ResponseEntity<ApiErrorResponse> response(HttpServletRequest request, ErrorCode errorCode) {
        return response(request, errorCode, List.of(errorCode.getMessage()));
    }

    private ResponseEntity<ApiErrorResponse> response(HttpServletRequest request, ErrorCode errorCode, List<String> errors) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiErrorResponse.of(request, errorCode, errors));
    }
}

package mipkasnow.dev.rococo.log;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestLogAttributes {

    static final String EXCEPTION = "rococo.log.exception";

    private RequestLogAttributes() {
    }

    public static void setException(HttpServletRequest request, Throwable exception) {
        request.setAttribute(EXCEPTION, exception);
    }

    static Throwable exception(HttpServletRequest request) {
        return (Throwable) request.getAttribute(EXCEPTION);
    }
}

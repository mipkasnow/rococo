package mipkasnow.dev.rococo.api.timing;

import mipkasnow.dev.rococo.jupiter.extension.TestMethodContextExtension;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.extension.ExtensionContext;
import retrofit2.Invocation;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.HTTP;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@ParametersAreNonnullByDefault
public final class ApiTimingInterceptor implements Interceptor {

    @Override
    @Nonnull
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startedAt = System.nanoTime();
        long at = System.currentTimeMillis();
        Response response = null;
        try {
            response = chain.proceed(request);
            return response;
        } finally {
            record(request, response == null ? 0 : response.code(), at, startedAt);
        }
    }

    private static void record(Request request, int status, long at, long startedAt) {
        long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
        ApiTimingCollector.record(new ApiCallSample(
                at,
                request.method(),
                request.url().toString(),
                endpoint(request),
                status,
                ms,
                currentTest()
        ));
    }

    @Nonnull
    private static String endpoint(Request request) {
        return request.method() + " " + path(request);
    }

    @Nonnull
    private static String path(Request request) {
        Invocation invocation = request.tag(Invocation.class);
        String template = invocation == null ? null : declaredPath(invocation.method());
        if (template == null || template.isBlank()) {
            return request.url().encodedPath();
        }
        int query = template.indexOf('?');
        if (query >= 0) {
            template = template.substring(0, query);
        }
        if (template.startsWith("http://") || template.startsWith("https://")) {
            return template;
        }
        if (!template.startsWith("/")) {
            return "/" + template;
        }
        return template;
    }

    @Nullable
    private static String declaredPath(Method method) {
        GET get = method.getAnnotation(GET.class);
        if (get != null) {
            return get.value();
        }
        POST post = method.getAnnotation(POST.class);
        if (post != null) {
            return post.value();
        }
        PUT put = method.getAnnotation(PUT.class);
        if (put != null) {
            return put.value();
        }
        PATCH patch = method.getAnnotation(PATCH.class);
        if (patch != null) {
            return patch.value();
        }
        DELETE delete = method.getAnnotation(DELETE.class);
        if (delete != null) {
            return delete.value();
        }
        HEAD head = method.getAnnotation(HEAD.class);
        if (head != null) {
            return head.value();
        }
        OPTIONS options = method.getAnnotation(OPTIONS.class);
        if (options != null) {
            return options.value();
        }
        HTTP http = method.getAnnotation(HTTP.class);
        if (http != null) {
            return http.path();
        }
        return null;
    }

    @Nonnull
    private static String currentTest() {
        ExtensionContext context = TestMethodContextExtension.context();
        if (context == null) {
            return "";
        }
        String className = context.getTestClass().map(Class::getSimpleName).orElse("");
        String displayName = context.getDisplayName();
        if (className.isEmpty()) {
            return displayName;
        }
        if (displayName.isBlank()) {
            return className;
        }
        return className + "." + displayName;
    }
}

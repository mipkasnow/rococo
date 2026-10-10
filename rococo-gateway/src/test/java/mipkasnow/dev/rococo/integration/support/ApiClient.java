package mipkasnow.dev.rococo.integration.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;
import java.util.function.Consumer;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

public final class ApiClient {

    private static final long TOKEN_TTL_SECONDS = 1800;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final String username;

    public ApiClient(MockMvc mockMvc, ObjectMapper objectMapper) {
        this(mockMvc, objectMapper, null);
    }

    private ApiClient(MockMvc mockMvc, ObjectMapper objectMapper, String username) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.username = username;
    }

    public ApiClient as(String username) {
        return new ApiClient(mockMvc, objectMapper, username);
    }

    public ApiClient anonymous() {
        return new ApiClient(mockMvc, objectMapper, null);
    }

    public JavaType pageOf(Class<?> type) {
        return objectMapper.getTypeFactory().constructParametricType(PageResponse.class, type);
    }

    public <T> T get(String uri, Class<T> type) {
        return send(HttpMethod.GET, uri, null, type);
    }

    public <T> T get(String uri, JavaType type) {
        return send(HttpMethod.GET, uri, null, type);
    }

    public <T> T get(String uri, JavaType type, Consumer<MockHttpServletRequestBuilder> customizer) {
        return send(HttpMethod.GET, uri, null, type, customizer);
    }

    public <T> T send(HttpMethod method, String uri, Object body, Class<T> type) {
        return send(method, uri, body, objectMapper.constructType(type));
    }

    public <T> T send(HttpMethod method, String uri, Object body, JavaType type) {
        return send(method, uri, body, type, null);
    }

    public <T> T send(HttpMethod method, String uri, Object body, JavaType type, Consumer<MockHttpServletRequestBuilder> customizer) {
        ApiExchange response = exchange(method, uri, body, customizer);
        int status = response.status();
        if (status < 200 || status >= 300) {
            throw new AssertionError("Expected 2xx but was " + status + ": " + response.body());
        }
        return response.as(type);
    }

    public ApiExchange exchange(HttpMethod method, String uri, Object body) {
        return exchange(method, uri, body, null);
    }

    public ApiExchange exchange(HttpMethod method, String uri, Object body, Consumer<MockHttpServletRequestBuilder> customizer) {
        MockHttpServletRequestBuilder builder = request(method, uri);
        if (username != null) {
            Instant issuedAt = Instant.now();
            builder.with(jwt().jwt(claims -> claims
                    .subject(username)
                    .issuedAt(issuedAt)
                    .expiresAt(issuedAt.plusSeconds(TOKEN_TTL_SECONDS))));
        }
        if (body != null) {
            builder.contentType(MediaType.APPLICATION_JSON).content(write(body));
        }
        if (customizer != null) {
            customizer.accept(builder);
        }
        return new ApiExchange(objectMapper, perform(builder));
    }

    private String write(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private MvcResult perform(MockHttpServletRequestBuilder builder) {
        try {
            return mockMvc.perform(builder).andReturn();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}

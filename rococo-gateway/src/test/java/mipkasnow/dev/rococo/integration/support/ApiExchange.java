package mipkasnow.dev.rococo.integration.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public final class ApiExchange {

    private final ObjectMapper objectMapper;
    private final MvcResult result;

    ApiExchange(ObjectMapper objectMapper, MvcResult result) {
        this.objectMapper = objectMapper;
        this.result = result;
    }

    public int status() {
        return result.getResponse().getStatus();
    }

    public String body() {
        try {
            return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public <T> T as(Class<T> type) {
        return as(objectMapper.constructType(type));
    }

    public <T> T as(JavaType type) {
        String payload = body();
        if (payload.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(exception);
        }
    }
}

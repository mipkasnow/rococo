package mipkasnow.dev.rococo.log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RequestBodySanitizer {

    static final int MAX_BODY_LENGTH = 2000;
    private static final String IMAGE_PREFIX = "data:image/";
    private static final Set<String> IMAGE_FIELDS = Set.of("photo", "content", "avatar");

    private final ObjectMapper objectMapper;

    public String sanitize(byte[] body, String contentType) {
        if (body == null || body.length == 0) {
            return "";
        }
        if (isBinary(contentType)) {
            return "[binary data]";
        }
        String text = new String(body, StandardCharsets.UTF_8);
        if (contentType != null && contentType.toLowerCase().contains("json")) {
            text = maskJson(text);
        }
        return cut(flatten(text));
    }

    private String maskJson(String text) {
        try {
            JsonNode node = objectMapper.readTree(text);
            mask(node);
            return objectMapper.writeValueAsString(node);
        } catch (Exception exception) {
            return text;
        }
    }

    private void mask(JsonNode node) {
        if (node instanceof ObjectNode object) {
            List<String> names = new ArrayList<>();
            object.fieldNames().forEachRemaining(names::add);
            for (String name : names) {
                JsonNode child = object.get(name);
                if (IMAGE_FIELDS.contains(name) && child.isTextual()) {
                    object.put(name, imagePlaceholder(child.asText()));
                } else {
                    mask(child);
                }
            }
        } else if (node instanceof ArrayNode array) {
            array.forEach(this::mask);
        }
    }

    private String imagePlaceholder(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        int comma = value.indexOf(',');
        if (value.regionMatches(true, 0, IMAGE_PREFIX, 0, IMAGE_PREFIX.length()) && comma >= 0) {
            int payloadLength = value.length() - comma - 1;
            return "[image " + (payloadLength * 3 / 4) + "]";
        }
        return "[image " + value.length() + "]";
    }

    static String flatten(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.replace("|", "+").replaceAll("[\\r\\n]+", " ");
    }

    private static String cut(String value) {
        if (value.length() <= MAX_BODY_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_BODY_LENGTH) + "...";
    }

    private static boolean isBinary(String contentType) {
        if (contentType == null) {
            return false;
        }
        String type = contentType.toLowerCase();
        return type.contains("image/")
                || type.contains("audio/")
                || type.contains("video/")
                || type.contains("octet-stream")
                || type.contains("pdf");
    }
}

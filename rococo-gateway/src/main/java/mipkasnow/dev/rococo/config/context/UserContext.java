package mipkasnow.dev.rococo.config.context;

import mipkasnow.dev.rococo.exception.ApiException;
import mipkasnow.dev.rococo.exception.ErrorCode;

public class UserContext {

    private final ThreadLocal<String> username = new ThreadLocal<>();

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getUsername() {
        String value = username.get();
        if (value == null || value.isBlank()) {
            throw new ApiException(ErrorCode.Unauthorized);
        }
        return value;
    }

    public void clear() {
        username.remove();
    }
}

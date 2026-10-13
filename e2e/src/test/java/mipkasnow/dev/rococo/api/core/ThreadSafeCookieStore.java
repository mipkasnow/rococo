package mipkasnow.dev.rococo.api.core;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

@ParametersAreNonnullByDefault
public enum ThreadSafeCookieStore implements CookieStore {
    INSTANCE;

    private final ThreadLocal<CookieStore> cs = ThreadLocal.withInitial(
            ThreadSafeCookieStore::inMemoryCookieStore
    );

    @Override
    public void add(URI uri, HttpCookie cookie) {
        cs.get().add(uri, cookie);
    }

    @Override
    @Nonnull
    public List<HttpCookie> get(URI uri) {
        return cs.get().get(uri);
    }

    @Override
    @Nonnull
    public List<HttpCookie> getCookies() {
        return cs.get().getCookies();
    }

    @Override
    @Nonnull
    public List<URI> getURIs() {
        return cs.get().getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return cs.get().remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return cs.get().removeAll();
    }

    public String cookieValue(String cookieName) {
        return getCookies().stream()
                .filter(c -> c.getName().equals(cookieName))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow();
    }

    private static CookieStore inMemoryCookieStore() {
        return new CookieManager().getCookieStore();
    }
}

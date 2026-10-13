package mipkasnow.dev.rococo.jupiter.extension;

import mipkasnow.dev.rococo.api.client.ArtistApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.Artist;
import mipkasnow.dev.rococo.model.artist.ArtistCreateRequest;
import mipkasnow.dev.rococo.model.artist.ArtistResponse;
import mipkasnow.dev.rococo.utils.DataUrl;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomString;

@ParametersAreNonnullByDefault
public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);

    private static final String PHOTO = "images/artist/Levitan.jpg";

    private final ArtistApiClient artistApiClient = new ArtistApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Artist.class)
                .ifPresent(artist -> {
                    ArtistResponse created = artistApiClient.create(new ArtistCreateRequest(
                            value(artist.name(), 12),
                            value(artist.biography(), 20),
                            DataUrl.fromClasspath(PHOTO)
                    ));
                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Nonnull
    public static ArtistResponse artist() {
        ExtensionContext context = TestMethodContextExtension.context();
        ArtistResponse artist = context.getStore(NAMESPACE).get(context.getUniqueId(), ArtistResponse.class);
        if (artist == null) {
            throw new IllegalStateException("@Artist is required");
        }
        return artist;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(ArtistResponse.class);
    }

    @Override
    @Nonnull
    public ArtistResponse resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return artist();
    }

    @Nonnull
    private static String value(String requested, int length) {
        return requested.isBlank() ? randomString(length) : requested;
    }
}

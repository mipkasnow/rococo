package mipkasnow.dev.rococo.jupiter.extension;

import mipkasnow.dev.rococo.api.client.PaintingApiClient;
import mipkasnow.dev.rococo.jupiter.annotation.Painting;
import mipkasnow.dev.rococo.model.artist.ArtistIdRequest;
import mipkasnow.dev.rococo.model.artist.ArtistResponse;
import mipkasnow.dev.rococo.model.museum.MuseumIdRequest;
import mipkasnow.dev.rococo.model.museum.MuseumResponse;
import mipkasnow.dev.rococo.model.painting.PaintingCreateRequest;
import mipkasnow.dev.rococo.model.painting.PaintingResponse;
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
public class PaintingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);

    private static final String CONTENT = "images/painting/Kalitka, Vasnetsov.jpg";

    private final PaintingApiClient paintingApiClient = new PaintingApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Painting.class)
                .ifPresent(painting -> {
                    ArtistResponse artist = ArtistExtension.artist();
                    MuseumResponse museum = MuseumExtension.museum();
                    PaintingResponse created = paintingApiClient.create(new PaintingCreateRequest(
                            value(painting.title(), 12),
                            value(painting.description(), 20),
                            DataUrl.fromClasspath(CONTENT),
                            new ArtistIdRequest(artist.id()),
                            new MuseumIdRequest(museum.id())
                    ));
                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Nonnull
    public static PaintingResponse painting() {
        ExtensionContext context = TestMethodContextExtension.context();
        PaintingResponse painting = context.getStore(NAMESPACE).get(context.getUniqueId(), PaintingResponse.class);
        if (painting == null) {
            throw new IllegalStateException("@Painting is required");
        }
        return painting;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(PaintingResponse.class);
    }

    @Override
    @Nonnull
    public PaintingResponse resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return painting();
    }

    @Nonnull
    private static String value(String requested, int length) {
        return requested.isBlank() ? randomString(length) : requested;
    }
}

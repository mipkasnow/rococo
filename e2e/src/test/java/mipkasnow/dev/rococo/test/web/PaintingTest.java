package mipkasnow.dev.rococo.test.web;

import com.codeborne.selenide.Selenide;
import mipkasnow.dev.rococo.jupiter.annotation.ApiLogin;
import mipkasnow.dev.rococo.jupiter.annotation.Artist;
import mipkasnow.dev.rococo.jupiter.annotation.Museum;
import mipkasnow.dev.rococo.jupiter.annotation.Painting;
import mipkasnow.dev.rococo.jupiter.annotation.meta.WebTest;
import mipkasnow.dev.rococo.model.artist.ArtistResponse;
import mipkasnow.dev.rococo.model.museum.MuseumResponse;
import mipkasnow.dev.rococo.model.painting.PaintingResponse;
import mipkasnow.dev.rococo.page.ArtistDetailPage;
import mipkasnow.dev.rococo.page.PaintingDetailPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomString;

@WebTest
public class PaintingTest {

    private static final String PHOTO = "images/painting/Fregat pod parusom, Aivazovsky.jpg";

    @Test
    @ApiLogin
    @Artist
    @Museum
    @DisplayName("К художнику можно добавить картину")
    void paintingShouldBeAddedToArtist(ArtistResponse artist, MuseumResponse museum) {
        String title = randomString(8);
        String description = randomString(11);

        Selenide.open(ArtistDetailPage.url(artist.id()), ArtistDetailPage.class)
                .checkName(artist.name())
                .openNewPainting()
                .fill(title, description)
                .uploadContent(PHOTO)
                .selectMuseum(museum.title())
                .add()
                .checkPainting(title)
                .openPainting(title)
                .checkTitle(title)
                .checkDescription(description)
                .checkArtist(artist.name());
    }

    @Test
    @ApiLogin
    @Artist
    @Museum
    @Painting
    @DisplayName("Картину художника можно отредактировать")
    void artistPaintingShouldBeUpdated(ArtistResponse artist, PaintingResponse painting) {
        String title = randomString(8);
        String description = randomString(11);

        Selenide.open(PaintingDetailPage.url(painting.id()), PaintingDetailPage.class)
                .checkTitle(painting.title())
                .checkArtist(artist.name())
                .openEdit()
                .checkValues(painting.title(), painting.description())
                .fill(title, description)
                .save()
                .checkTitle(title)
                .checkDescription(description)
                .checkArtist(artist.name());
    }
}

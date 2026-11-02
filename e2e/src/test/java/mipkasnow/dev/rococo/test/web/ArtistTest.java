package mipkasnow.dev.rococo.test.web;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Feature;
import mipkasnow.dev.rococo.jupiter.annotation.ApiLogin;
import mipkasnow.dev.rococo.jupiter.annotation.meta.WebTest;
import mipkasnow.dev.rococo.page.ArtistPage;
import mipkasnow.dev.rococo.page.message.FieldMsg;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static mipkasnow.dev.rococo.utils.RandomDataUtils.randomString;

@WebTest
@Feature("Художники")
public class ArtistTest {

    private static final String PHOTO = "images/artist/Aivazovsky.jpg";
    private static final String PHOTO_LIST = "screenshots/artist/aivazovsky-list.png";
    private static final String PHOTO_CARD = "screenshots/artist/aivazovsky-card.png";

    @Test
    @ApiLogin
    @DisplayName("Художник сохраняет имя, биографию и фото")
    void artistShouldSaveNameBiographyAndPhoto() {
        String name = randomString(3);
        String biography = randomString(11);

        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .openNewArtist()
                .fill(name, biography)
                .uploadPhoto(PHOTO)
                .add()
                .checkArtist(name)
                .checkPhoto(name, PHOTO_LIST)
                .openArtist(name)
                .checkName(name)
                .checkBiography(biography)
                .checkPhoto(PHOTO_CARD);
    }

    @Test
    @ApiLogin
    @DisplayName("Пустая форма художника не отправляется")
    void emptyArtistFormShouldStayOpen() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .openNewArtist()
                .submit()
                .checkFieldNotHaveError(FieldMsg.NAME_TOO_SHORT)
                .checkFieldNotHaveError(FieldMsg.BIOGRAPHY_TOO_SHORT);
    }

    @Test
    @ApiLogin
    @DisplayName("Без изображения художник не добавляется")
    void artistWithoutPhotoShouldStayOpen() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .openNewArtist()
                .fill(randomString(3), randomString(11))
                .submit()
                .checkFieldNotHaveError(FieldMsg.NAME_TOO_SHORT)
                .checkFieldNotHaveError(FieldMsg.BIOGRAPHY_TOO_SHORT);
    }

    @Test
    @ApiLogin
    @DisplayName("Имя из 2 символов не сохраняется")
    void tooShortNameShouldBeRejected() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .openNewArtist()
                .fill(randomString(2), randomString(11))
                .uploadPhoto(PHOTO)
                .submit()
                .checkFieldNotHaveError(FieldMsg.BIOGRAPHY_TOO_SHORT)
                .checkFieldError(FieldMsg.NAME_TOO_SHORT);
    }

    @Test
    @ApiLogin
    @DisplayName("Имя длиннее 255 символов не сохраняется")
    void tooLongNameShouldBeRejected() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .openNewArtist()
                .fill(randomString(256), randomString(11))
                .uploadPhoto(PHOTO)
                .submit()
                .checkFieldNotHaveError(FieldMsg.BIOGRAPHY_TOO_LONG)
                .checkFieldError(FieldMsg.NAME_TOO_LONG);
    }

    @Test
    @ApiLogin
    @DisplayName("Биография из 10 символов не сохраняется")
    void tooShortBiographyShouldBeRejected() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .openNewArtist()
                .fill(randomString(3), randomString(10))
                .uploadPhoto(PHOTO)
                .submit()
                .checkFieldNotHaveError(FieldMsg.NAME_TOO_SHORT)
                .checkFieldError(FieldMsg.BIOGRAPHY_TOO_SHORT);
    }

    @Test
    @ApiLogin
    @DisplayName("Биография длиннее 2000 символов не сохраняется")
    void tooLongBiographyShouldBeRejected() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .openNewArtist()
                .fill(randomString(3), randomString(2001))
                .uploadPhoto(PHOTO)
                .submit()
                .checkFieldNotHaveError(FieldMsg.NAME_TOO_LONG)
                .checkFieldError(FieldMsg.BIOGRAPHY_TOO_LONG);
    }

    @Test
    @ApiLogin
    @DisplayName("Слишком короткие имя и биография показывают обе ошибки")
    void tooShortNameAndBiographyShouldBeRejected() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .openNewArtist()
                .fill(randomString(2), randomString(10))
                .uploadPhoto(PHOTO)
                .submit()
                .checkFieldError(FieldMsg.NAME_TOO_SHORT)
                .checkFieldError(FieldMsg.BIOGRAPHY_TOO_SHORT);
    }

    @Test
    @ApiLogin
    @DisplayName("Слишком длинные имя и биография показывают обе ошибки")
    void tooLongNameAndBiographyShouldBeRejected() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .openNewArtist()
                .fill(randomString(256), randomString(2001))
                .uploadPhoto(PHOTO)
                .submit()
                .checkFieldError(FieldMsg.NAME_TOO_LONG)
                .checkFieldError(FieldMsg.BIOGRAPHY_TOO_LONG);
    }

    @Test
    @ApiLogin
    @DisplayName("Художник с уже занятым именем не добавляется")
    void duplicateNameShouldBeRejected() {
        String name = randomString(8);
        String biography = randomString(11);

        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .openNewArtist()
                .fill(name, biography)
                .uploadPhoto(PHOTO)
                .add()
                .openNewArtist()
                .fill(name, biography)
                .uploadPhoto(PHOTO)
                .submitTakenName();
    }
}

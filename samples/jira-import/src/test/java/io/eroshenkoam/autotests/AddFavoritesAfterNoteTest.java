package io.eroshenkoam.autotests;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

public class AddFavoritesAfterNoteTest {

    @Test
    @TmsLink("AE-5")
    @Tags({@Tag("favorites"), @Tag("regress")})
    @DisplayName("Добавление в избранное после создания заметки")
    public void shouldAddToFavoriteAfterNodeTest() {
    }

    @Test
    @TmsLink("AE-4")
    @Tags({@Tag("critical"), @Tag("favorites"), @Tag("regress")})
    @DisplayName("Удаление из избранного после удаления заметки")
    public void shouldDeleteToFavoriteAfterNodeTest() {
    }

}

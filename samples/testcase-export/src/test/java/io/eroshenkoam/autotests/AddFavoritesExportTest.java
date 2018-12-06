package io.eroshenkoam.autotests;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AddFavoritesExportTest {

    private BasicSteps steps;

    @Test
    @TmsLink("AE-5")
    @Features({@Feature("Favorites"), @Feature("Notes")})
    @Stories({@Story("Add to favorites after adding note")})
    @DisplayName("Добавление в избранное после создания заметки")
    public void shouldAddToFavoriteAfterNodeTest() {
        steps.openMainPage();
        steps.openAutoCardPage("Volvo XC90");
        steps.addNotesToAutoCard("в хорошем состоянии");
        steps.openFavoritesPage();
        steps.checkFavoritesListContains("Volvo XC90");
    }

    @Test
    @TmsLink("AE-4")
    @Features({@Feature("Favorites"), @Feature("Notes")})
    @Stories({@Story("Delete from favorites after deleting note")})
    @DisplayName("Удаление из избранного после удаления заметки")
    public void shouldDeleteToFavoriteAfterNodeTest() {
        steps.openMainPage();
        steps.openAutoCardPage("Volvo XC90");
        steps.addNotesToAutoCard("в хорошем состоянии");
        steps.openFavoritesPage();
        steps.checkFavoritesListContains("Volvo XC90");
    }

}

package io.eroshenkoam.allure;

import io.qameta.allure.Feature;
import io.qameta.allure.Features;
import io.qameta.allure.Stories;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class AddFavoritesAfterNoteTest {

    private BasicSteps steps;

    @Test
    @TmsLink("AE-5")
    @Features({@Feature("First Feature"), @Feature("Second Feature")})
    @Stories({@Story("First Story"), @Story("Second Story")})
    public void shouldAddToFavoriteAfterNodeTest() {
        steps.openMainPage();
        steps.openAutoCardPage("Volvo XC90");
        steps.addNotesToAutoCard("в хорошем состоянии");
        steps.openFavoritesPage();
        steps.checkFavoritesListContains("Volvo XC90");
    }

}

package io.eroshenkoam.allure;

import io.qameta.allure.Step;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class BasicSteps {

    @Step("Открываем главную страницу")
    public void openMainPage() {

    }

    @Step("Открываем страницу машины марки {mark}")
    public void openAutoCardPage(String mark) {

    }

    @Step("Добавляем заметку {text} к машине")
    public void addNotesToAutoCard(String text) {

    }

    @Step("Открываем страницу избранного")
    public void openFavoritesPage() {

    }

    @Step("Проверяем что список избранного сожержит марку {mark}")
    public void checkFavoritesListContains(String mark) {

    }

}

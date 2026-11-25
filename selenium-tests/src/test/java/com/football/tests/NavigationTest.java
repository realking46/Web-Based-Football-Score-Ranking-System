package com.football.tests;

import com.football.base.BaseTest;
import com.football.pages.HomePage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.assertEquals;

public class NavigationTest extends BaseTest {

    @DataProvider
    public Object[][] rounds() {
        return new Object[][]{
                {"r1-Overview", "round1.html"},
                {"r2-Overview", "round2.html"},
                {"r3-Overview", "round3.html"},
                {"semi-final-Overview", "Semi-Final.html"},
                {"final-Overview", "final.html"},
        };
    }

    @Test(dataProvider = "rounds", description = "Each round's Overview button opens that round's page")
    public void overviewButtonsOpenRoundPages(String buttonId, String page) {
        new HomePage(driver).openOverview(buttonId);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.urlContains(page));
    }

    @Test(groups = "smoke", description = "View Current Standings opens the player standings page")
    public void viewStandingsOpensStandingsPage() {
        new HomePage(driver).openStandings();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.urlContains("standings.html"));
        assertEquals(driver.getTitle(), "Player Standings");
    }
}

package com.football.tests;

import com.football.base.BaseTest;
import com.football.pages.HomePage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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

    @Test(dataProvider = "rounds",
          description = "Each round's Overview button opens that round's page; "
                      + "on a fresh tournament the page reports that no match data is saved yet")
    public void overviewButtonsOpenRoundPages(String buttonId, String page) {
        HomePage home = new HomePage(driver);
        home.openOverview(buttonId);

        // Round pages alert on load when localStorage has no data for that round.
        assertEquals(home.acceptAlert(), "No saved data found.");
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.urlContains(page));
        assertTrue(driver.getCurrentUrl().endsWith(page));
    }

    @Test(groups = "smoke", description = "View Current Standings opens the player standings page")
    public void viewStandingsOpensStandingsPage() {
        new HomePage(driver).openStandings();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.urlContains("standings.html"));
        assertEquals(driver.getTitle(), "Player Standings");
    }
}

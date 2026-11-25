package com.football.tests;

import com.football.base.BaseTest;
import com.football.pages.HomePage;
import com.football.pages.HomePage.Side;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class HomePageTest extends BaseTest {

    @Test(groups = "smoke", description = "Home page loads with the standings table and its six columns")
    public void standingsTableHasExpectedColumns() {
        HomePage home = new HomePage(driver);
        assertEquals(driver.getTitle(), "Football Tournament");
        assertEquals(home.tableHeaders(), List.of("Team", "Wins", "Draws", "Losses", "Goals", "Points"));
    }

    @Test(groups = "smoke", description = "A fresh tournament starts with every stat at zero")
    public void freshTournamentStartsAtZero() {
        HomePage home = new HomePage(driver);
        assertEquals(home.tableRow(1).subList(1, 6), List.of("0", "0", "0", "0", "0"));
        assertEquals(home.tableRow(2).subList(1, 6), List.of("0", "0", "0", "0", "0"));
    }

    @Test(description = "Both team pickers offer the same list of nations")
    public void bothSidesOfferSameTeams() {
        HomePage home = new HomePage(driver);
        List<String> left = home.teamOptions(Side.LEFT);
        assertTrue(left.size() >= 2, "expected at least two teams");
        assertEquals(home.teamOptions(Side.RIGHT), left);
    }
}

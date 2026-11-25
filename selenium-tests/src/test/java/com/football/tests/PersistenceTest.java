package com.football.tests;

import com.football.base.BaseTest;
import com.football.pages.HomePage;
import com.football.pages.HomePage.Side;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/** Save (Ctrl+S), reload and reset (Ctrl+R) behaviour, backed by localStorage. */
public class PersistenceTest extends BaseTest {

    @Test(groups = "smoke", description = "Ctrl+S saves the tournament and confirms with an alert")
    public void ctrlSSavesTournament() {
        HomePage home = new HomePage(driver);
        home.selectTeam(Side.LEFT, "Brazil");
        assertEquals(home.pressCtrl("s"), "Data saved successfully!");
    }

    @Test(description = "Saved teams and squads are restored after a page reload")
    public void savedStateSurvivesReload() {
        HomePage home = new HomePage(driver);
        home.selectTeam(Side.LEFT, "Brazil");
        home.selectTeam(Side.RIGHT, "England");
        home.openPlayerDropdown(Side.LEFT);
        home.pickPlayer(Side.LEFT, 1);
        home.pressCtrl("s");

        driver.navigate().refresh();
        home = new HomePage(driver);

        assertEquals(home.tableRow(1).get(0), "Brazil");
        assertEquals(home.tableRow(2).get(0), "England");
        assertEquals(home.selectedPlayerNames(Side.LEFT), List.of("Lionel Messi"));
    }

    @Test(description = "Ctrl+R resets teams, squads and stats")
    public void ctrlRResetsTournament() {
        HomePage home = new HomePage(driver);
        home.selectTeam(Side.LEFT, "Brazil");
        home.openPlayerDropdown(Side.LEFT);
        home.pickPlayer(Side.LEFT, 0);

        assertEquals(home.pressCtrl("r"), "Data reset successfully!");
        assertTrue(home.selectedPlayerNames(Side.LEFT).isEmpty());
        assertEquals(home.tableRow(1).subList(1, 6), List.of("0", "0", "0", "0", "0"));
    }

    // ---------------- known bugs (excluded from the default run, see README) ----------------

    @Test(groups = "known-bugs",
          description = "BUG: after reload, already-picked players can be picked again. "
                      + "script.js compares image URLs against a hard-coded local path "
                      + "(file:///C:/Users/LENOVO/Desktop/tournament/), so it only works on one machine.")
    public void pickedPlayersStayDisabledAfterReload() {
        HomePage home = new HomePage(driver);
        home.openPlayerDropdown(Side.LEFT);
        home.pickPlayer(Side.LEFT, 0);
        home.pressCtrl("s");

        driver.navigate().refresh();
        home = new HomePage(driver);

        assertTrue(home.isPlayerOptionDisabled(Side.LEFT, 0));
    }

    @Test(groups = "known-bugs",
          description = "BUG: after Ctrl+R the team logo becomes <img src=\"null\"> (a broken image), "
                      + "because the placeholder option has no data-image attribute.")
    public void resetLeavesNoBrokenLogo() {
        HomePage home = new HomePage(driver);
        home.selectTeam(Side.LEFT, "Brazil");
        home.pressCtrl("r");

        assertFalse(home.teamLogoSrc(Side.LEFT).endsWith("null"));
    }
}

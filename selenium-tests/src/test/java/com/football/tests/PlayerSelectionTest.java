package com.football.tests;

import com.football.base.BaseTest;
import com.football.pages.HomePage;
import com.football.pages.HomePage.Side;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class PlayerSelectionTest extends BaseTest {

    @Test(groups = "smoke", description = "Picking a player adds them to the squad and disables the option")
    public void pickingPlayerAddsToSquad() {
        HomePage home = new HomePage(driver);
        home.openPlayerDropdown(Side.LEFT);
        home.pickPlayer(Side.LEFT, 0);

        assertEquals(home.selectedPlayerNames(Side.LEFT), List.of("Cristiano Ronaldo"));
        assertTrue(home.isPlayerOptionDisabled(Side.LEFT, 0), "picked player should not be selectable twice");
    }

    @Test(description = "Clicking a player in the squad removes them and re-enables the option")
    public void removingPlayerReEnablesOption() {
        HomePage home = new HomePage(driver);
        home.openPlayerDropdown(Side.LEFT);
        home.pickPlayer(Side.LEFT, 1);
        home.removeSelectedPlayer(Side.LEFT, 0);

        assertTrue(home.selectedPlayerNames(Side.LEFT).isEmpty());
        assertFalse(home.isPlayerOptionDisabled(Side.LEFT, 1));
    }

    @Test(description = "Left and right squads are independent")
    public void squadsAreIndependent() {
        HomePage home = new HomePage(driver);
        home.openPlayerDropdown(Side.LEFT);
        home.pickPlayer(Side.LEFT, 1);          // Lionel Messi (left)
        home.openPlayerDropdown(Side.RIGHT);
        home.pickPlayer(Side.RIGHT, 0);         // C. Ronaldo (right)

        assertEquals(home.selectedPlayerNames(Side.LEFT), List.of("Lionel Messi"));
        assertEquals(home.selectedPlayerNames(Side.RIGHT), List.of("C. Ronaldo"));
    }

    @Test(description = "Boundary: a squad accepts 11 players and rejects the 12th with an alert")
    public void squadIsCappedAtEleven() {
        HomePage home = new HomePage(driver);
        home.openPlayerDropdown(Side.RIGHT);    // right side offers more than 11 players
        for (int i = 0; i < 11; i++) {
            home.pickPlayer(Side.RIGHT, i);
        }
        assertEquals(home.selectedPlayerNames(Side.RIGHT).size(), 11);

        home.pickPlayer(Side.RIGHT, 11);
        assertEquals(home.acceptAlert(), "You can't select more than 11 players!");
        assertEquals(home.selectedPlayerNames(Side.RIGHT).size(), 11);
    }
}

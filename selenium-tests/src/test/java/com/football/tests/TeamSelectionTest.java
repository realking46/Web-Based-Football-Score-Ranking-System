package com.football.tests;

import com.football.base.BaseTest;
import com.football.pages.HomePage;
import com.football.pages.HomePage.Side;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TeamSelectionTest extends BaseTest {

    @DataProvider
    public Object[][] teams() {
        return new Object[][]{{"Brazil", "brazil"}, {"Argentina", "argentina"}, {"France", "france"}};
    }

    @Test(groups = "smoke", dataProvider = "teams",
          description = "Picking the left team updates the table row, every round block and the logo")
    public void leftTeamSelectionPropagates(String team, String logoFile) {
        HomePage home = new HomePage(driver);
        home.selectTeam(Side.LEFT, team);

        assertEquals(home.tableRow(1).get(0), team);
        home.roundTeamNames(Side.LEFT).forEach(name -> assertEquals(name, team));
        assertTrue(home.teamLogoSrc(Side.LEFT).endsWith("images/teams/" + logoFile + ".png"));
    }

    @Test(description = "Picking the right team updates the second table row and every round block")
    public void rightTeamSelectionPropagates() {
        HomePage home = new HomePage(driver);
        home.selectTeam(Side.RIGHT, "Germany");

        assertEquals(home.tableRow(2).get(0), "Germany");
        home.roundTeamNames(Side.RIGHT).forEach(name -> assertEquals(name, "Germany"));
    }

    @Test(description = "Changing a team replaces the previous one everywhere")
    public void changingTeamReplacesPrevious() {
        HomePage home = new HomePage(driver);
        home.selectTeam(Side.LEFT, "Brazil");
        home.selectTeam(Side.LEFT, "Portugal");

        assertEquals(home.tableRow(1).get(0), "Portugal");
        home.roundTeamNames(Side.LEFT).forEach(name -> assertEquals(name, "Portugal"));
    }
}

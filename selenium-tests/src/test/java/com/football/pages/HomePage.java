package com.football.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/** Page object for index.html: team pickers, player pickers, standings table and round links. */
public class HomePage {

    public enum Side {
        LEFT("leftteam", "left-team-image", "dropdownHeader-left", "imageDropdown-left", ".player-displayleft"),
        RIGHT("rightteam", "right-team-image", "dropdownHeader-right", "imageDropdown-right", ".player-displayright");

        final By teamSelect, teamImage, dropdownHeader, dropdown, selectedPlayers;

        Side(String select, String image, String header, String dropdown, String selected) {
            this.teamSelect = By.id(select);
            this.teamImage = By.id(image);
            this.dropdownHeader = By.id(header);
            this.dropdown = By.id(dropdown);
            this.selectedPlayers = By.cssSelector(selected);
        }
    }

    private static final By TABLE_ROWS = By.cssSelector(".tournament-table tr");
    private static final By VIEW_STANDINGS = By.id("view-standings");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // ---------- teams ----------

    public void selectTeam(Side side, String team) {
        new Select(driver.findElement(side.teamSelect)).selectByValue(team);
    }

    /** Option values (e.g. "RealMadrid"), excluding the empty "Select Team" placeholder. */
    public List<String> teamOptions(Side side) {
        return new Select(driver.findElement(side.teamSelect)).getOptions().stream()
                .map(o -> o.getAttribute("value")).filter(v -> v != null && !v.isEmpty()).toList();
    }

    public String teamLogoSrc(Side side) {
        return driver.findElement(side.teamImage).findElement(By.tagName("img")).getAttribute("src");
    }

    /** Team names shown inside every round block (Final ... Round 1) for one side. */
    public List<String> roundTeamNames(Side side) {
        String css = side == Side.LEFT ? ".left-team-name" : ".right-team-name";
        return driver.findElements(By.cssSelector(css)).stream().map(WebElement::getText).toList();
    }

    // ---------- standings table ----------

    public List<String> tableHeaders() {
        return driver.findElement(TABLE_ROWS).findElements(By.tagName("th")).stream()
                .map(WebElement::getText).toList();
    }

    /** Row 1 = left team, row 2 = right team. */
    public List<String> tableRow(int row) {
        return driver.findElements(TABLE_ROWS).get(row).findElements(By.tagName("td")).stream()
                .map(WebElement::getText).toList();
    }

    // ---------- players ----------

    public void openPlayerDropdown(Side side) {
        driver.findElement(side.dropdownHeader).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(side.dropdown));
    }

    public List<WebElement> playerOptions(Side side) {
        return driver.findElement(side.dropdown).findElements(By.cssSelector(".dropdownimg"));
    }

    public void pickPlayer(Side side, int index) {
        playerOptions(side).get(index).click();
    }

    public List<String> selectedPlayerNames(Side side) {
        return driver.findElement(side.selectedPlayers).findElements(By.tagName("img")).stream()
                .map(img -> img.getAttribute("alt")).toList();
    }

    public void removeSelectedPlayer(Side side, int index) {
        driver.findElement(side.selectedPlayers).findElements(By.tagName("img")).get(index).click();
    }

    public boolean isPlayerOptionDisabled(Side side, int index) {
        return "none".equals(playerOptions(side).get(index).getCssValue("pointer-events"));
    }

    // ---------- keyboard shortcuts & alerts ----------

    public String pressCtrl(String key) {
        new Actions(driver).keyDown(Keys.CONTROL).sendKeys(key).keyUp(Keys.CONTROL).perform();
        return acceptAlert();
    }

    public String acceptAlert() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        return text;
    }

    // ---------- navigation ----------

    public void openOverview(String buttonId) {
        driver.findElement(By.id(buttonId)).click();
    }

    public void openStandings() {
        driver.findElement(VIEW_STANDINGS).click();
    }
}

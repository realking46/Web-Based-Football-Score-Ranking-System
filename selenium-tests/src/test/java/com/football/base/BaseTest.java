package com.football.base;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Starts a local static file server for the site (unless -DbaseUrl is given)
 * and opens a fresh browser for every test.
 *
 * System properties:
 *   -Dbrowser=chrome|firefox   (default chrome)
 *   -Dheadless=true|false      (default true)
 *   -DbaseUrl=http://...       (default: serve the repo root locally)
 */
public abstract class BaseTest {

    private static HttpServer server;
    protected static String baseUrl;
    protected WebDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void startServer() {
        baseUrl = System.getProperty("baseUrl");
        if (baseUrl == null || baseUrl.isBlank()) {
            Path siteRoot = Path.of("..").toAbsolutePath().normalize();
            server = SimpleFileServer.createFileServer(
                    new InetSocketAddress("127.0.0.1", 0), siteRoot, SimpleFileServer.OutputLevel.NONE);
            server.start();
            baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void openBrowser() {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        String browser = System.getProperty("browser", "chrome");

        if (browser.equalsIgnoreCase("firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            if (headless) options.addArguments("-headless");
            driver = new FirefoxDriver(options);
        } else {
            ChromeOptions options = new ChromeOptions();
            if (headless) options.addArguments("--headless=new");
            options.addArguments("--window-size=1600,1000", "--no-sandbox", "--disable-dev-shm-usage");
            driver = new ChromeDriver(options);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

        // Start every test from a clean tournament: the app keeps its state in localStorage.
        driver.get(baseUrl + "/index.html");
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        driver.navigate().refresh();
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}

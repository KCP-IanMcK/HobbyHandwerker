package org.example.backend.end2end;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class End2EndTest {

  private WebDriver driver;
  WebDriverWait wait;

  @BeforeEach
  void setUp() {
    WebDriverManager.chromedriver().setup();

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless=new");
    options.addArguments("--disable-gpu");
    options.addArguments("--window-size=1920,1080");
    options.addArguments("--no-sandbox");

    //driver = new ChromeDriver(options);
    driver = new ChromeDriver();
    wait = new WebDriverWait(driver, Duration.of(15, ChronoUnit.SECONDS));
  }

  @AfterEach
  void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Test
  @Order(1)
  void testHomepageTitle() {
    driver.get("http://localhost:4200");

    String title = driver.getTitle();
    assertEquals("HobbyHandwerker", title);
  }

  @Test
  @Order(2)
  void testCreateProfile() {
    driver.get("http://localhost:4200");

    WebElement createProfileButton = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("createProfileButton"))
    );

    createProfileButton.click();

    WebElement createProfileContainer = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("createProfileContainer"))
    );

    assertTrue(createProfileContainer.isDisplayed());

    WebElement closeCreateProfileButton = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("closeCreateProfileButton"))
    );

    closeCreateProfileButton.click();

    assertTrue(driver.findElements(By.id("closeCreateProfileButton")).isEmpty());
  }

  @Test
  @Order(3)
  void testLoginButton() {
    driver.get("http://localhost:4200");

    WebElement loginButton = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("loginButton"))
    );

    loginButton.click();

    WebElement loginContainer = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("loginContainer"))
    );

    assertTrue(loginContainer.isDisplayed());

    WebElement loginCancel = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("loginCancel"))
    );

    loginCancel.click();

    WebElement homeContainer = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("homeContainer"))
    );

    assertTrue(homeContainer.isDisplayed());
  }

  @Test
  @Order(4)
  void testLogin() {
    driver.get("http://localhost:4200");

    WebElement loginButton = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("loginButton"))
    );

    loginButton.click();

    WebElement loginContainer = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("loginContainer"))
    );

    wait.until(ExpectedConditions.visibilityOf(loginContainer));

    WebElement loginUsername = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("loginUsername"))
    );

    WebElement loginPassword = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("loginPassword"))
    );

    loginUsername.sendKeys("dummy2");
    loginPassword.sendKeys("Pass2");

    WebElement loginSubmit = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("loginSubmit"))
    );

    loginSubmit.click();

    WebElement homeContainer = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("homeContainer"))
    );

    assertTrue(homeContainer.isDisplayed());
  }

  @Test
  @Order(5)
  void testShowProfileButton() {
    testLogin();
    driver.navigate().refresh();

    WebElement showProfileButton = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("showProfileButton"))
    );

    showProfileButton.click();

    WebElement profileContainer = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("profileContainer"))
    );

    assertTrue(profileContainer.isDisplayed());
  }

  @Test
  @Order(6)
  void testEditProfile() {
    testLogin();
    driver.navigate().refresh();

    WebElement showProfileButton = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("showProfileButton"))
    );

    showProfileButton.click();

    WebElement editProfileButton = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("editProfileButton"))
    );

    editProfileButton.click();

    WebElement editContainer = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("editContainer"))
    );

    assertTrue(editContainer.isDisplayed());

    WebElement usernameInput = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("usernameInput"))
    );

    usernameInput.sendKeys("Max");

    WebElement closeEditProfileButton = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("closeEditProfileButton"))
    );

    closeEditProfileButton.click();

    WebElement username = wait.until(
      ExpectedConditions.elementToBeClickable(By.id("username"))
    );

    assertEquals("Name: dummy2" , username.getText());
  }
}

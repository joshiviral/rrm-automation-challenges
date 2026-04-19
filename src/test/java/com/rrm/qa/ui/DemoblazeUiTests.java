package com.rrm.qa.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class DemoblazeUiTests {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1400, 900));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    void ui_addProductToCart_andVerifyInCart() {
        // Intent: Verify user can add a product to cart and it appears in cart
        // Preconditions: Site available, cart empty (new session)

        driver.get("https://www.demoblaze.com/");

        // Step: Open product details
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Samsung galaxy s6"))).click();

        // Step: Click Add to cart
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Add to cart"))).click();

        // Expected: alert "Product added"
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        alert.accept();
        assertTrue(alertText.toLowerCase().contains("product"), "Expected product-added alert, got: " + alertText);

        // Step: Navigate to Cart
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cart"))).click();

        // Expected: cart table eventually contains product name
        By cartBody = By.id("tbodyid");

        // Wait for the cart body to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartBody));
        // Wait until the cart body text contains the product (handles async load)
        boolean itemFound = wait.until(driver -> driver.findElement(cartBody).getText().contains("Samsung galaxy s6"));

        assertTrue(itemFound, "Cart does not contain expected item.");
    }

    @Test
    void ui_placeOrder_andVerifyConfirmation() {
        // Intent: Verify user can place an order and receive confirmation
        // Preconditions: At least one item in cart

        driver.get("https://www.demoblaze.com/");

        // Add one product to cart
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Samsung galaxy s6"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Add to cart"))).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        // Go to cart
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Cart"))).click();

        // Place Order
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Place Order')]")))
                .click();

        // Fill purchase form
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name"))).sendKeys("Viral Joshi");
        driver.findElement(By.id("country")).sendKeys("Canada");
        driver.findElement(By.id("city")).sendKeys("Toronto");
        driver.findElement(By.id("card")).sendKeys("4111111111111111");
        driver.findElement(By.id("month")).sendKeys("04");
        driver.findElement(By.id("year")).sendKeys("2026");

        // Purchase
        driver.findElement(By.xpath("//button[contains(text(),'Purchase')]")).click();

        // Expected: confirmation modal with Id and Amount
        WebElement confirmation = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sweet-alert")));
        String text = confirmation.getText();

        assertTrue(text.contains("Id"), "Confirmation should contain Id. Actual: " + text);
        assertTrue(text.contains("Amount"), "Confirmation should contain Amount. Actual: " + text);

        // OK
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'OK')]"))).click();

        // Confirmation should disappear
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".sweet-alert")));
    }
}

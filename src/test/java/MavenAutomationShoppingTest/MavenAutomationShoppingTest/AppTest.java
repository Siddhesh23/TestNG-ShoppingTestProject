package MavenAutomationShoppingTest.MavenAutomationShoppingTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.File;

public class AppTest {
	private WebDriver d;
	private WebDriverWait w;
	Properties prop = new Properties();
	Actions a;

	@BeforeMethod // to open the browser and navigate to the required page before each test method
	public void setup() throws IOException {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\Resources\\chromedriver.exe");
		d = new ChromeDriver();
		d.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		d.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		d.manage().window().maximize();
		d.manage().deleteAllCookies();
		FileInputStream ip = new FileInputStream(System.getProperty("user.dir") + "\\Resources\\config.properties");
		prop.load(ip);
		d.get(prop.getProperty("URL"));
		d.manage().window().maximize();
	}

	@AfterMethod // to close the browser after each test method
	public void close() {
		d.close();
	}

	public void screencapture() throws IOException // method to capture screenshots, save them in 'Screenshots' folder
													// and also insert them in reports
	{
		File srcFile = ((TakesScreenshot) d).getScreenshotAs(OutputType.FILE);
		File screenshotName = new File(System.getProperty("user.dir") + "\\Screenshots\\" + d.getTitle() + ".png");
		FileUtils.copyFile(srcFile, screenshotName);
		Reporter.log("<br><img src='" + screenshotName + "' height='500' width='500'/><br>");
	}

	@Test(priority = 0, enabled = true, description = "testcase to create new account")
	public void signup() throws IOException {
		w = new WebDriverWait(d, 20);
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("signin"))));
		d.findElement(By.xpath(prop.getProperty("signin"))).click();
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("email"))));
		d.findElement(By.xpath(prop.getProperty("email"))).sendKeys(prop.getProperty("emailid"));
		d.findElement(By.xpath(prop.getProperty("createaccount"))).click();
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("firstnametextbox"))));
		d.findElement(By.xpath(prop.getProperty("firstnametextbox"))).sendKeys(prop.getProperty("firstname"));
		d.findElement(By.xpath(prop.getProperty("lastnametextbox"))).sendKeys(prop.getProperty("lastname"));
		d.findElement(By.xpath(prop.getProperty("passwordtextbox"))).sendKeys(prop.getProperty("password"));
		d.findElement(By.xpath(prop.getProperty("address1textbox"))).sendKeys(prop.getProperty("address1"));
		d.findElement(By.xpath(prop.getProperty("citytextbox"))).sendKeys(prop.getProperty("city"));
		d.findElement(By.xpath(prop.getProperty("statetextbox"))).sendKeys(prop.getProperty("state"));
		d.findElement(By.xpath(prop.getProperty("mobiletextbox"))).sendKeys(prop.getProperty("mobile"));
		d.findElement(By.xpath(prop.getProperty("postcodetextbox"))).sendKeys(prop.getProperty("postcode"));
		screencapture();
		d.findElement(By.xpath(prop.getProperty("submitaccount"))).click();
	}

	@Test(priority = 1, enabled = true, description = "testcase to sign in, purchase and verify total amount")
	public void purchase() throws IOException {
		w = new WebDriverWait(d, 20);
		a = new Actions(d);

		// signin with created account
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("signin"))));
		d.findElement(By.xpath(prop.getProperty("signin"))).click();
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("signmail"))));
		d.findElement(By.xpath(prop.getProperty("signmail"))).sendKeys(prop.getProperty("emailid"));
		d.findElement(By.xpath(prop.getProperty("signpasswrd"))).sendKeys(prop.getProperty("password"));
		d.findElement(By.xpath(prop.getProperty("submitlogin"))).click();

		// select product in 'Women' tab
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("womensection"))));
		d.findElement(By.xpath(prop.getProperty("womensection"))).click();
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("productimg"))));
		a.moveToElement(d.findElement(By.xpath(prop.getProperty("productimg")))).build().perform();

		// Quick View Page: Add product to cart with quantity=2
		d.findElement(By.xpath(prop.getProperty("quickview"))).click();
		d.switchTo().frame(d.findElement(By.tagName("iframe")));
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("addtocart"))));
		d.findElement(By.xpath(prop.getProperty("add"))).click();
		float price = Float.parseFloat(d.findElement(By.xpath(prop.getProperty("price"))).getText().replace("$", ""));
		screencapture();
		d.findElement(By.xpath(prop.getProperty("addtocart"))).click();

		// Quick View Page: Amount Verification and proceed to checkout
		float totalonpage1right = Float
				.parseFloat(d.findElement(By.xpath(prop.getProperty("totalonpage1right"))).getText().replace("$", ""));
		float totalonpage1left = Float
				.parseFloat(d.findElement(By.xpath(prop.getProperty("totalonpage1left"))).getText().replace("$", ""));
		float shipping = Float
				.parseFloat(d.findElement(By.xpath(prop.getProperty("shippingcost"))).getText().replace("$", ""));
		float grandtotalonpage1 = Float
				.parseFloat(d.findElement(By.xpath(prop.getProperty("grandtotalonpage1"))).getText().replace("$", ""));
		Reporter.log("QuickView Page: UnitPrice:$" + price + " Total:$" + totalonpage1right + " Shipping:$" + shipping
				+ " Grand Total:$" + grandtotalonpage1);
		screencapture();
		Assert.assertEquals(totalonpage1right, 2 * price,
				"Incorrect Total Value on Quick View Page.Total value should be twice of unit product price");
		Assert.assertEquals(totalonpage1right, totalonpage1left,
				"Total Values displayed are not consistent on Quick View Page");
		Assert.assertEquals(grandtotalonpage1, totalonpage1right + shipping,
				"Incorrect Grand Total on Quick View Page");
		d.findElement(By.xpath(prop.getProperty("proceedtocheckout1"))).click();

		// Summary Page: Amount Verification and move ahead
		float grandtotalonpage2 = Float
				.parseFloat(d.findElement(By.xpath(prop.getProperty("grandtotalonpage2"))).getText().replace("$", ""));
		float tax = Float.parseFloat(d.findElement(By.xpath(prop.getProperty("tax"))).getText().replace("$", ""));
		float totalwithtax = Float
				.parseFloat(d.findElement(By.xpath(prop.getProperty("totalwithtax"))).getText().replace("$", ""));
		Reporter.log("Summary Page: GrandTotal:$" + grandtotalonpage2 + " Tax:$" + tax + " Amount with tax:$"
				+ totalwithtax);
		screencapture();
		Assert.assertEquals(totalwithtax, tax + grandtotalonpage2, "Incorrect Grand Total on Summary Page");
		d.findElement(By.xpath(prop.getProperty("proceedtocheckout2"))).click();
		Reporter.log("QuickViewPage: UnitPrice:$" + price + " Total:$" + totalonpage1right + " Shipping:$" + shipping
				+ " Grand Total:$" + grandtotalonpage1);

		// Address Page: move ahead
		screencapture();
		d.findElement(By.xpath(prop.getProperty("proceedtocheckout3"))).click();

		// Shipping Page: agree to terms and conditions and move ahead
		d.findElement(By.xpath(prop.getProperty("checkbox"))).click();
		screencapture();
		d.findElement(By.xpath(prop.getProperty("proceedtocheckout4"))).click();

		// Payment Page: Amount Verification
		float totalwithtaxonpage5 = Float.parseFloat(
				d.findElement(By.xpath(prop.getProperty("totalwithtaxonpage5"))).getText().replace("$", ""));
		Reporter.log("Payment Page: Amount with tax:$" + totalwithtaxonpage5);
		Assert.assertEquals(totalwithtax, tax + totalwithtaxonpage5, "Incorrect Grand Total on Payment Page");
		d.findElement(By.xpath(prop.getProperty("paymentmode"))).click();
		d.findElement(By.xpath(prop.getProperty("confirmpayment"))).click();
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("sucessmessage"))));
		String expectedSuccessMessage = prop.getProperty("expectedsuccessmessage");
		String actualMessage = d.findElement(By.xpath(prop.getProperty("sucessmessage"))).getText().trim();
		Assert.assertTrue(actualMessage.equals(expectedSuccessMessage),
				"Payment success message not displayed on Payment Page");
		Reporter.log("Payment Sucessful");
		screencapture();

		// Order History: Amount Verification
		d.findElement(By.xpath(prop.getProperty("customeraccount"))).click();
		d.findElement(By.xpath(prop.getProperty("orderhistory"))).click();
		w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(prop.getProperty("historyprice"))));
		float historyPrice = Float
				.parseFloat(d.findElement(By.xpath(prop.getProperty("historyprice"))).getText().replace("$", ""));
		Reporter.log("Order History: Amount with tax:$" + historyPrice);
		screencapture();
		Assert.assertEquals(totalwithtax, historyPrice, "Incorrect Grand Total in Order History table");
	}
}

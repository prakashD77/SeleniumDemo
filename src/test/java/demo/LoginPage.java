package demo;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.bettercloud.vault.VaultException;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class LoginPage {
    ExtentReports extentReports;
    ExtentTest extentTest;
    ChromeDriver driver;

    @BeforeMethod
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\sdevagalla\\SherwinFramework\\DemoTest\\src\\WebDriver\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        extentReports = Extentreports.getReportObject();
        extentTest = extentReports.createTest("Login Test");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        extentReports.flush();
    }

    @Test
    public void login() throws InterruptedException, IOException, VaultException {
        try {
            FileInputStream fis = new FileInputStream("C:\\Users\\sdevagalla\\Desktop\\Demo.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            XSSFRow row = sheet.getRow(0);
            int colCount = row.getLastCellNum();
            Object data[][] = new Object[rowCount - 1][colCount];
            for (int i = 0; i < rowCount - 1; i++) {
                row = sheet.getRow(i + 1);
                for (int j = 0; j < colCount; j++) {
                    data[i][j] = row.getCell(j);
                    System.out.println(data[i][j]);
                }
            }

            // Navigate to the login page
            driver.get("https://helpdesk.miraclesoft.com/login");
            driver.manage().window().maximize();
            driver.findElement(By.xpath("//input[@id='exampleInputEmail1']")).sendKeys(data[0][0].toString());
            driver.findElement(By.xpath("//input[@id='exampleInputPassword1']")).sendKeys(data[0][1].toString());
            Thread.sleep(2000);
            // Click the login button
            driver.findElement(By.xpath("//span[normalize-space()='Login']")).click();
            Thread.sleep(2000);
            driver.findElement(By.xpath("(//button[normalize-space()='Add'])[1]")).click();
            WebElement testDropDown = driver.findElement(By.xpath("//select[@name='selectName']"));
            Select dropdown = new Select(testDropDown);
            Thread.sleep(2000);
            dropdown.selectByIndex(3);
            Thread.sleep(1000);
            driver.findElement(By.xpath("//select[@name='selectSubName']")).click();
            Thread.sleep(1000);
            driver.findElement(By.xpath("(//option[normalize-space()='Software Request'])[1]")).click();
            driver.findElement(By.xpath("(//input[@id='username3'])[1]")).sendKeys(data[0][2].toString());
            driver.findElement(By.cssSelector("#username4")).sendKeys("Please Install the required softwares in my system as soon as possible");
            Thread.sleep(2000);
            driver.findElement(By.xpath("//div[@class='ml-3']//span[1]")).click();
            WebElement element = driver.findElement(By.xpath("//input[@id='file-1']"));
            element.sendKeys("C:/Users/sdevagalla/Pictures/computer.png");
            Thread.sleep(3000);
            driver.findElement(By.xpath("//button[normalize-space()='Close']")).click();

            Thread.sleep(2000);
            extentTest.pass("Tests passed");
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Test failed due to exception: " + e.getMessage());
            captureScreenshot("failure_screenshot");
            e.printStackTrace();
        }
    }

    private void captureScreenshot(String screenshotName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);

            String destinationPath = "./Screenshots/" + screenshotName + ".png";
            FileUtils.copyFile(source, new File(destinationPath));

            // Convert image to Base64
            String base64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(destinationPath)));

            extentTest.fail("Failure Screenshot",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64Image).build());
        } catch (Exception e) {
            System.out.println("Exception while taking screenshot: " + e.getMessage());
        }
    }
}
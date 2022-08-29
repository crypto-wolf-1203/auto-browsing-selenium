package com.pplvn;

import com.pplvn.model.*;
import com.pplvn.util.FileUtil;
import com.pplvn.util.SlugUtils;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.NullOutputStream;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainController {
    private final static Logger log = LogManager.getLogger(MainController.class);
    private OkHttpClient client = new OkHttpClient();
    private ConfigTool configTool;
    private String titleParent;
    private ArrayList<String> listAsinError;


    public MainController() throws IOException {
        readConfig();
    }

    private void readConfig() throws IOException {
        InputStream input = new FileInputStream("config/config.properties");
        Properties prop = new Properties();
        // load a properties file
        prop.load(input);
        configTool = new ConfigTool();

        configTool.setCountry(loadProperties(prop, "amazon.country"));
        configTool.setHandlingTime(loadProperties(prop, "amazon.handling.time"));
        configTool.setPrice(loadProperties(prop, "amazon.price"));
        configTool.setQuantity(loadProperties(prop, "amazon.quantity"));
        configTool.setUrl(loadProperties(prop, "amazon.url"));
        configTool.setData(loadProperties(prop, "options.chrome.user.data"));
        configTool.setProfile(loadProperties(prop, "options.chrome.profile.directory"));
        configTool.setTimeOutClick(Integer.parseInt(loadProperties(prop, "options.chrome.time.out.click")));
        configTool.setTimeOutOpen(Integer.parseInt(loadProperties(prop, "options.chrome.time.out.open")));
        configTool.setTimeOutClose(Integer.parseInt(loadProperties(prop, "options.chrome.time.out.close")));
    }


    private String loadProperties(Properties prop, String name) {
        String property = prop.getProperty(name);
        log.debug(String.format("Load %s = %s", name, property));
        return property;
    }

    public void start() throws IOException {
        try {
            WebDriver driver = getWebDriver(configTool);
            log.debug("Start Click");
            listAsinError = new ArrayList<>();
            clickGetAsin(configTool, driver);
            driver.close();
        } catch (org.openqa.selenium.NoSuchElementException ex) {
            log.error("Start Error", ex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private WebDriver getWebDriver(ConfigTool configTool) throws IOException {
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
        ChromeDriverService chromeDriverService = new ChromeDriverService.Builder().build();
        chromeDriverService.sendOutputTo(NullOutputStream.NULL_OUTPUT_STREAM);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--user-data-dir=" + configTool.getData());
        options.addArguments("--profile-directory=" + configTool.getProfile());
        log.debug("Done Get Data");
        WebDriver driver = new ChromeDriver(chromeDriverService, options);
        log.debug("Success New Chrome");
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        return driver;
    }


    private void clickGetAsin(ConfigTool configTool, WebDriver driver) throws InterruptedException, IOException {
        //Gõ Asin vào rồi search
        File file = new File("config/asin.txt");
        List<String> listAsin = FileUtil.readAsin(file.getPath());
        for (String asin : listAsin) {
            if (StringUtils.equals(configTool.getUrl(), "co.uk")) {
                log.debug("Hijack EU");
                startHiJackEu(configTool, driver, asin);
            } else {
                log.debug("Hijack US");
                startHiJack(configTool, driver, asin);
            }
        }
        log.debug("Done List Asin ");
        if (listAsinError.size() > 0) {
            log.debug("Auto Hj List Asin Error ");
            for (String asinError : listAsinError) {
                if (StringUtils.equals(configTool.getUrl(), "co.uk")) {
                    log.debug("Hijack EU");
                    startHiJackEu(configTool, driver, asinError);
                } else {
                    log.debug("Hijack US");
                    startHiJack(configTool, driver, asinError);
                }
                listAsinError.remove(asinError);
            }
        }
        exportAinsError();
        log.debug("Complete HJ");
    }

    private void exportAinsError() throws IOException {
        FileWriter fileWriter = new FileWriter("config/Asin Error.txt", true);
        BufferedWriter printWriter = new BufferedWriter(fileWriter);
        for (String asinLink : listAsinError) {
            printWriter.write(asinLink);
            printWriter.newLine();
        }
        printWriter.close();
        log.debug("Done Export Asin Error ");
    }


    private void startHiJackEu(ConfigTool configTool, WebDriver driver, String asin) throws InterruptedException {
        try {
            // Open browser
            driver.get("https://sellercentral.amazon." + configTool.getUrl() + "/product-search/search?q=" + asin + "&ref_=xx_addlisting_dnav_xx");
            TimeUnit.SECONDS.sleep(10);
            log.debug("Start Hijack " + asin);
            // click 'Show Variants'
            // get no exception
//            WebDriverWait wait = new WebDriverWait(driver, 10);
//            WebElement click = findElementNoExceptionWithWebDriver(driver, By.ByClassName.className("flex-end"));
//            log.debug(click);
//            wait.until(ExpectedConditions.visibilityOf(click));
//            log.debug("Click");
//            click.click();

            WebElement click = findElementNoExceptionWithWebDriver(driver, By.ByXPath.xpath("/html[1]/body[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/kat-box[1]" +
                    "/div[1]/section[3]/div[1]/section[2]/div[1]/kat-icon[1]"));


            log.debug(click);
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            click.click();
            log.debug("Done Click");

//            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
//            findElementNoExceptionWithWebElement(click,By.ByTagName.tagName("kat-icon")).click();
//            log.debug("Done Click");
//            click.click();
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            // Click Show more cho đến khi không còn show more nữa
            boolean showMore = true;
            do {
                WebElement detectShowMore = findElementNoExceptionWithWebDriver(driver, By.ByLinkText.linkText("Show 10 more"));
                // Click show more
                if (detectShowMore != null) {
                    detectShowMore.click();
                    // waite
                    TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
                    log.debug("Done Show More");
                } else {
                    showMore = false;
                    log.debug("Cant Show More");
                }
            } while (showMore);

            List<String> linkAsin = new ArrayList<>();
            // Tìm
            // bắt đầu tìm kat-dropdown
            List<WebElement> variationRows = driver.findElements(By.ByClassName.className("variation-row"));
            for (WebElement variationRow : variationRows) {

                // chỗ này click vẫn có rủi ro
                WebElement katDropdown = findElementNoExceptionWithWebElement(variationRow, By.ByTagName.tagName("kat-dropdown"));
                katDropdown.click();
                // select 'new'
                findElementNoExceptionWithWebElement(katDropdown, By.ByClassName.className("option-inner-container")).click();
                // get Button 'Sell this variation' của child
                List<WebElement> sell_this_variation = driver.findElements(By.partialLinkText("Sell this variation"));
                // lay Href
                for (WebElement sell : sell_this_variation) {
                    String href = sell.getAttribute("href");
                    //add Href vao LinkAsin con
                    linkAsin.add(href);
                }
                // waite
                TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            }
            // Lọc Duplicate Link Asin
            List<String> listStream = linkAsin.stream().distinct().collect(Collectors.toList());
            log.debug("Total Asin Child " + listStream.size());
            if (listStream.size() == 0) {
                log.debug("Show Variants Null");
                log.debug("Start Hijack " + asin);
                String url = "https://sellercentral.amazon." + configTool.getUrl() + "/abis/listing/syh?asin=" + asin + "&ref_=xx_addlisting_dnav_xx#offer";
                String randomString = randomString();
                Random rand = new Random();
                int n = rand.nextInt(100);
                String SKU = asin + "-" + SlugUtils.makeSlug(randomString) + "-" + String.format("%03d", n);
                configTool.setSKU(SKU);
                log.debug("EU");
                clickAsinEu(configTool, driver, url);

            } else {
                for (int i = 0; i < listStream.size(); i++) {
                    for (String link : listStream) {
                        int k = 1 + i;
                        log.debug("Get Link :" + link);
                        String randomString = randomString();
                        String SKU = asin + "-" + SlugUtils.makeSlug(randomString) + "-" + String.format("%03d", i++);
                        configTool.setSKU(SKU);
                        log.debug("EU");
                        clickAsinEu(configTool, driver, link);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Skip asin " + asin, e);
            listAsinError.add(asin);

        }
    }


    private void startHiJack(ConfigTool configTool, WebDriver driver, String asin) throws InterruptedException {
        try {
            // Open browser
            driver.get("https://sellercentral.amazon." + configTool.getUrl() + "/product-search/search?q=" + asin + "&ref_=xx_addlisting_dnav_xx");

            log.debug("Start Hijack " + asin);
            TimeUnit.SECONDS.sleep(5);
            // click 'Show Variants'
            // get no exception
            findElementNoExceptionWithWebDriver(driver, By.ByXPath.xpath("//body/div[@id='a-page']/div[@id='sc-content-container']/div[@id='product-search-container']" +
                    "/div[1]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/kat-box[1]/div[1]/section[3]/div[1]/section[2]/div[1]")).click();

            // Click Show more cho đến khi không còn show more nữa
            boolean showMore = true;
            do {
                // Click show more
                if (driver.findElements(By.partialLinkText("Show 10 more")).size() > 0) {
                    WebElement showMoreElement = findElementNoExceptionWithWebDriver(driver, By.partialLinkText("Show 10 more"));
                    showMoreElement.click();
                    // waite
                    TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
                } else {
                    showMore = false;
                }
            } while (showMore);

            List<String> linkAsin = new ArrayList<>();
            // Tìm
            // bắt đầu tìm kat-dropdown
            List<WebElement> variationRows = driver.findElements(By.ByClassName.className("variation-row"));
            for (WebElement variationRow : variationRows) {

                // chỗ này click vẫn có rủi ro
                WebElement katDropdown = findElementNoExceptionWithWebElement(variationRow, By.ByTagName.tagName("kat-dropdown"));
                katDropdown.click();
                // select 'new'
                findElementNoExceptionWithWebElement(katDropdown, By.ByClassName.className("option-inner-container")).click();
                // get Button 'Sell this variation' của child
                List<WebElement> sell_this_variation = driver.findElements(By.partialLinkText("Sell this variation"));
                // lay Href
                for (WebElement sell : sell_this_variation) {
                    String href = sell.getAttribute("href");
                    //add Href vao LinkAsin con
                    linkAsin.add(href);
                }

                // waite
                TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            }
            // Lọc Duplicate Link Asin
            List<String> listStream = linkAsin.stream().distinct().collect(Collectors.toList());
            log.debug("Total Asin Child " + listStream.size());
            if (listStream.size() == 0) {
                log.debug("Show Variants Null");
                log.debug("Start Hijack " + asin);
                String url = "https://sellercentral.amazon." + configTool.getUrl() + "/abis/listing/syh?asin=" + asin + "&ref_=xx_addlisting_dnav_xx#offer";
                String randomString = randomString();
                Random rand = new Random();
                int n = rand.nextInt(100);
                String SKU = asin + "-" + SlugUtils.makeSlug(randomString) + "-" + String.format("%03d", n);
                configTool.setSKU(SKU);

                log.debug("US");
                clickListAsin(configTool, driver, url);
            } else {
                for (int i = 0; i < listStream.size(); i++) {
                    for (String link : listStream) {
                        int k = 1 + i;
                        log.debug("Get Link :" + link);
                        String randomString = randomString();
                        String SKU = asin + "-" + SlugUtils.makeSlug(randomString) + "-" + String.format("%03d", i++);
                        configTool.setSKU(SKU);
                        log.debug("US");
                        clickListAsin(configTool, driver, link);

                    }
                }
            }


        } catch (Exception e) {
            log.error("Skip asin " + asin, e);
            listAsinError.add(asin);
        }
    }

    private WebElement findElementNoExceptionWithWebElement(WebElement parent, By by) {
        if (parent.findElements(by).size() > 0) {
            return parent.findElement(by);
        } else {
            return null;
        }
    }

    private WebElement findElementNoExceptionWithWebDriver(WebDriver parent, By by) {
        if (parent.findElements(by).size() > 0) {
            return parent.findElement(by);
        } else {
            return null;
        }
    }

    private String randomString() {
        int length = 3;
        boolean useLetters = true;
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

        return generatedString;
    }

    public static void setAttribute(WebDriver driver, WebElement elem, String attr, String value) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute(arguments[1],arguments[2])",
                elem, attr, value);
    }


// Start click link Asin con

    private void clickListAsin(ConfigTool configTool, WebDriver driver, String link) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            driver.get(link);
            WebElement check = findElementNoExceptionWithWebDriver(driver, By.ById.id("advanced-view-switch"));
            if (check == null) {
                driver.get(link);
            }
            check.click();
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            findElementNoExceptionWithWebDriver(driver, By.ByTagName.tagName("kat-input")).sendKeys(configTool.getSKU());
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            findElementNoExceptionWithWebDriver(driver, By.ByXPath.xpath("//kat-input[@id='standard_price']")).sendKeys(configTool.getPrice());
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            findElementNoExceptionWithWebDriver(driver, By.ByXPath.xpath("//*[@id=\"quantity\"]")).sendKeys(configTool.getQuantity());
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            WebElement findDropDown = findElementNoExceptionWithWebDriver(driver, By.id("condition_type"));
            log.debug("done condition");
            setAttribute(driver, findDropDown, "value", "new, new");
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            findElementNoExceptionWithWebDriver(driver, By.ByXPath.xpath("//kat-input[@id='fulfillment_latency']")).sendKeys(configTool.getHandlingTime());
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            findElementNoExceptionWithWebDriver(driver, By.ByXPath.xpath("//*[@id=\"EditSaveAction\"]")).click();
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClose());
        } catch (Exception e) {
            log.error("Skipp Error ", e);
        }
    }

    private void clickAsinEu(ConfigTool configTool, WebDriver driver, String link) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            driver.get(link);
            WebElement check = findElementNoExceptionWithWebDriver(driver, By.ByXPath.xpath("//*[@id=\"advanced-view-switch\"]"));
            if (check == null) {
                driver.get(link);
            }
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            findElementNoExceptionWithWebDriver(driver, By.ByCssSelector.cssSelector("#item_sku")).sendKeys(configTool.getSKU());
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            findElementNoExceptionWithWebDriver(driver, By.ByCssSelector.cssSelector("#standard_price")).sendKeys(configTool.getPrice());
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            findElementNoExceptionWithWebDriver(driver, By.ByCssSelector.cssSelector("#quantity")).sendKeys(configTool.getQuantity());
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            findElementNoExceptionWithWebDriver(driver, By.ByCssSelector.cssSelector("#country_of_origin")).sendKeys(configTool.getCountry());
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            WebElement findDropDown = findElementNoExceptionWithWebDriver(driver, By.id("condition_type"));
            log.debug("done condition");
            setAttribute(driver, findDropDown, "value", "new, new");
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClick());
            findElementNoExceptionWithWebDriver(driver, By.ByCssSelector.cssSelector("#EditSaveAction")).click();
            TimeUnit.SECONDS.sleep(configTool.getTimeOutClose());
        } catch (Exception e) {
            log.error("Skipp Error ", e);
        }
    }


}







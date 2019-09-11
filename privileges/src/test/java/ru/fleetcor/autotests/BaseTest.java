package ru.fleetcor.autotests;

import com.codeborne.selenide.Configuration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.testng.ITest;
import org.testng.annotations.*;
import org.testng.annotations.Optional;
import org.testng.asserts.SoftAssert;
import ru.fleetcor.pages.Privileges.Privileges;

import ru.fleetcor.properties.Prop;
import ru.fleetcor.util.Util;
import ru.yandex.qatools.allure.annotations.Attachment;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

//import org.openqa.selenium.ie.InternetExplorerDriver;
// https://ppr-v3.transitcard.ru:8080/
//https://ppr-v3.transitcard.ru:8080/
//https://ppr-v3.transitcard.ru:8080/



public abstract class BaseTest implements ITest {
    public static WebDriver driver;
    public static final Logger log = Logger.getLogger(BaseTest.class);

    public static String browser;
    public static String url;
    public static String card;
    private static String urlGpc;
    public static String logPath;
    public static String downloadFilePath;
    public static Util util;
    public static Privileges privileges;
    public SoftAssert softAssert;

    public static Properties properties;

    private ThreadLocal<String> testName = new ThreadLocal<>();

    public static void FilePDF() {

        File dir = new File(downloadFilePath);    // колхозный кусок кода, который определяет тип скачиваемого файла
        File[] ls = dir.listFiles();               // стандартный метод не работает т.к. у файла всегда разное имя
        String nn = ls[0].getName();                //
        if (nn.endsWith(".pdf")) {}                 // если имя файла заканчивается на .pdf то не делаем ничего
        else {
            Assert.assertEquals("Ослик","Суслик");   // если нет, то выполняем заведомо провальное сравнение и тест падает
        }

    }

    public static int coloranalyzed(String Fpath, int x1, int y1, int x2, int y2, double proc) throws IOException {

        BufferedImage small = ImageIO.read(new File(Fpath));
        int result =0;
        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
                int clr = small.getRGB(x, y);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;
                int mid = (red + green + blue) / 3;
                result = result + mid;
            }
        }
        int def = (x2 - x1)*(y2 - y1);
        result = result / def;

        int res = 2;
        if (result<(255*proc)) { res= 0;   // OK

        } else {
            if (result<(255*(proc+0.06))) {
                res= 1;   // Warning
            } else {
                res= 2;   // FAIL
            }
        }

        return res;

    }


    @BeforeSuite
    @Parameters({"file"})
    public void setupSuite(String file) {

        System.out.println("BeforSuite");
        getLoginProperties(file);
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        Date date = new Date();
        logPath = "log/AutoTest.log";
        System.setProperty("logFile.name", logPath);
        String log4jConfPath = "src/test/resources/ru/fleetcor/properties/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
        log.info("###################################################################################");
        File folder = new File("errors");
        if (!folder.exists()) {
            folder.mkdir();
        }
        downloadFilePath = System.getProperty("user.dir") + "\\" + properties.getProperty("pathToDownload");
        File folderToDownloads = new File(downloadFilePath);
        if (!folderToDownloads.exists())
            folderToDownloads.mkdir();
        url = properties.getProperty("url");
        card = properties.getProperty("card");
    }

    private static WebDriver getDriver() {
        log.info("Получаем driver для браузера " + browser);
        try {
            switch (browser.trim()) {
                case "chrome": {
                    Map<String, Object> preferences = new Hashtable<String, Object>();
                    preferences.put("download.default_directory", downloadFilePath);
                    preferences.put("safebrowsing.enabled", true);
                    preferences.put("select_file_dialogs.allowed", true);
                   preferences.put("download.prompt_for_download", false);
                    preferences.put("plugins.plugins_disabled", new String[] {
                            "Chrome PDF Viewer"
                    });
                    preferences.put("plugins.always_open_pdf_externally", true);
                    ChromeOptions options = new ChromeOptions();
                    options.setExperimentalOption("prefs", preferences);
                    options.addArguments("--disable-extensions");
                    options.addArguments("start-maximized");
                    options.addArguments("--safebrowsing-disable-download-protection");
                    options.addArguments("--no-sandbox");

                    //options.addArguments("--headless");

                    ChromeOptions cap = new ChromeOptions();
                    cap.setCapability(ChromeOptions.CAPABILITY, options);
                    Configuration.headless = false;  ///---отключает/включает беготню тестов на экране
                    Configuration.screenshots = true; // ---родной скриншотер селенида, при применении аллюра нужно отрубать
                    Configuration.startMaximized = true;
                    //   Configuration.fileDownload = PROXY;
                    //  Configuration.proxyEnabled = true;
                    if (System.getProperty("os.name").split(" ")[0].trim().equals("Windows"))
                        System.setProperty("webdriver.chrome.driver", "driver\\chromedriver.exe");
                    else
                        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
                    driver = new ChromeDriver(options);
                    break;
                }
            }
        } catch (IllegalStateException e) {
            log.error("IllegalStateException: " + e.getMessage());
        }

        //driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    @BeforeMethod
    @Parameters({"browser", "url"})
    /*/* Попытка создать настройки для юзера, котоорые будут подтягиваться из пропертей
            {"isGPC"})
    public  void setupMethod(String browser, Method method)*/
    public void setupMethod(String browser, Method method, @Optional("url") String url)throws MalformedURLException {

        testName.set(method.getName() + " " + browser);
        softAssert = new SoftAssert();
    }



    @AfterMethod
    public static void shutDown() {
    //    log.info("Закрываем driver браузера " + browser);
     //   driver.quit();
    }

    @Override
    public String getTestName() {
        return testName.get();
    }

    @AfterSuite
    public static void endSuite() throws IOException, SQLException, ClassNotFoundException {
        log.info("Количество ошибок после выполнения тестов: " + isError);
        if (isError > 0) {
            File errors = new File("errors");
            if (errors.exists()) {
                deleteDirectory(errors);
            }
        }
        File downloads = new File(downloadFilePath);
        deleteDirectory(downloads);

    }


    /**
     * Метод для двойного нажатия мышкой на элемент
     * @param element элемент, на который нужно нажать два раза
     */
    public static void doubleClick(WebElement element){
        Actions actions = new Actions(driver);
        actions.doubleClick(element).perform();
    }


    /**
     * Метод для принудительного обозначения теста как непройденного
     */
    private static int isError = 0;
    public void assertTest(String methodName, Throwable e) throws IOException {
        isError += 1;
        String pathToFile;
        pathToFile = makeScreenshot(methodName);
        getBytes(pathToFile);
        org.testng.Assert.fail(e.getMessage());
    }

    @Attachment("Скриншот")
    private static byte[] getBytes(String path) throws IOException {
        log.error("path = " + path);
        return Files.readAllBytes(Paths.get(path, ""));
    }

    /**
     * Метод для удаления директории
     * @param folder директория
     */
    private static void deleteDirectory(File folder) {
        if (folder.exists()) {
            File files[] = folder.listFiles();
            for (File file: files) {
                file.delete();
            }
            folder.delete();
        }
    }

    /**
     * Метод для создания скриншота
     * @param methodName название метода чтобы таким же именем назвать скриншот с ошибкой
     * @return строка с путем до файла
     */
    private static String makeScreenshot(String methodName) {
        try {
            log.info("Делаем скриншот с ошибкой");
//            driver.manage().window().maximize();
            Screenshot screenshot = new AShot().takeScreenshot(driver);
            BufferedImage image = screenshot.getImage();
            String pathname = "errors"+"\\" + methodName  + ".png";
            File outputImage = new File(pathname);
            ImageIO.write(image, "png", outputImage);
            return pathname;
        } catch (IOException e) {
            log.error("IOException: " + e.getMessage());
            return null;
        }
    }

    /**
     * Метод для получения названия метода
     * @return строка с названием метода
     */
    public String getMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    /**
     * Метод, в который оборачиваются тесты, чтобы ловить исключения
     * @param testBody тело теста
     */
    public void runTest(TestBody testBody) {
        try {
            testBody.run();
        } catch (Exception | AssertionError e) {
            String methodName = getMethodName();
            log.error("Exception in " + methodName + ": " + e.getMessage().split("\\(")[0]);
            try {
                assertTest(methodName, e);
            } catch (IOException e1) {
                log.error(e1.getMessage());
            }
        }
    }

    public static void getLoginProperties(String file){
        InputStream is = Prop.class.getResourceAsStream(file);
        properties = new Properties();
        try {
            properties.load(is);
            is.close();
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Метод для получения имени файла по ссылке
     * @param element элемент, в котором находится файл
     * @return
     */
    public static String getFileName(WebElement element) {
        String[] split;
        String url = driver.getCurrentUrl();
        String href = element.getAttribute("href");
        split = href.split("/");
        String res = split[split.length - 1];
        return res;
    }



}

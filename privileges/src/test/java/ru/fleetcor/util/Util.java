package ru.fleetcor.util;

//import oracle.jdbc.OracleTypes;
import com.codeborne.selenide.ElementsCollection;
import org.apache.commons.lang3.time.DateUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.fleetcor.autotests.BaseTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static ru.fleetcor.autotests.BaseTest.log;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;


public class Util {

    private WebDriver driver;

    private final String uuid_loginFleet = "65df45ef-f1df-4352-9506-06cbeb471e41";

    private final static String GET_USER_INFO =
            "SELECT ac.id_account, cl.id_cont, cl.id_web_cont, am.password " +
                    "FROM   account_mail am " +
                    "JOIN account ac ON am.id_account = ac.id_account " +
                    "JOIN cont_account ca ON ca.id_account = ac.id_account " +
                    "JOIN   cont_link cl ON ca.id_cont = cl.id_web_cont " +
                    "JOIN cont c ON c.id_cont = cl.id_cont " +
                    "WHERE  ac.accountname = UPPER(?) AND ac.acitve = 'Y'";

    public Util(WebDriver driver){
        this.driver = driver;
    }

    /*метод для получения списка окон. Вызов:Util.handleMultipleWindows(driver, "title окна")*/
    public static void handleMultipleWindows(WebDriver driver) {
        Set<String> windows = driver.getWindowHandles();
        for (String window : windows) {
            driver.switchTo().window(window);
        }
    }



    public static Integer GetPopupNews(String id_cont, String locale)
            throws SQLException, ClassNotFoundException {
        int count = 0;
//        ResultSet resultSet = null;
//        Connection conn = ConnectionFactory.getConnection();
//        CallableStatement cstmt = conn.prepareCall(("BEGIN NEWS_PKG.get_popup_news(?,?,?); END;"));
//        cstmt.setString(1, id_cont);
//        cstmt.setString(2, locale);
//        cstmt.registerOutParameter(3, OracleTypes.CURSOR);
//        cstmt.execute();
//        resultSet = (ResultSet) cstmt.getObject(3);
//        while (resultSet.next()) {
//            count++;
//        }
//        resultSet.close();
//        cstmt.close();
//        conn.close();
        return count;
    }

    /**
     * Метод для получения даты, относительно текущей (значения могут быть отрицательными - тогда будет вычитание
     *  из текущей даты)
     * @param days количество дней
     * @param months количество месяцев
     * @param years количество лет
     * @return строка типа "дд.мм.гггг"
     */
    public static String getDateFromCurrent(int days, int months, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        calendar.add(Calendar.MONTH, months);
        calendar.add(Calendar.YEAR, years);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String strDate = format.format(date);
        return strDate;
    }

    /**
     * Метод для сравнения двух строк в зависимости от флага
     * @param str1 первая строка
     * @param str2 вторая строка
     * @param flag true - строки должны быть одинаковыми, false - строки должны быть разными
     * @throws Exception
     */
    public static void checkStrings(String str1, String str2, boolean flag) throws Exception {
        try {
            if (flag)
                assertEquals(str1, str2);
            else
                assertNotEquals(str1, str2);
        } catch (AssertionError e) {
            throw new Exception(e);
        }
    }

    /**
     * Метод для преобразования файла в массив байт
     * @param path путь к файлу
     * @return массив байт
     * @throws IOException
     */
    public static byte[] convertToBytes(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            byte[] content = Files.readAllBytes(file.toPath());
            return content;
        }
        else {
            return null;
        }
    }
    /**
     * Метод для проверки есть ли элемент в DOM. Есть элемент возвращаем true, нет элемента возвращаем false
     * @param driver драйвер
     * @param element элемент
     */
    public static boolean isElementPresent(WebDriver driver,  WebElement element, int time) {
        try {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            Wait<WebDriver> wait = new WebDriverWait(driver, time);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            return false;
        }
    }

    public static void checkFileExists(String fileName) throws Exception {
        TimeUnit.SECONDS.sleep(1);
        String path = BaseTest.downloadFilePath + "\\" + fileName;
        File file = new File(path);
        if (!file.exists()) {
            throw new Exception("Файл " + path + " не существует");
        }
    }

    public static int getFilterCount(WebElement element) {
        String text = element.getText();
        String countStr = text.split(":")[1].trim();
        int count = Integer.parseInt(countStr);
        return count;
    }

    public static String getFileNameFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    /**
     * Метод для ввода даты
     * @param field селектор поля ввода
     * @param date дата
     */
    public static void inputDate(WebElement field, String date) {
        field.sendKeys(date);
    }

    public static boolean isDateSorted( String dateString1, String dateString2) throws ParseException {

        Date date1 = getDate("dd.MM.yyyy", dateString1);
        Date date2 = getDate("dd.MM.yyyy", dateString2);

        return date1.after(date2);
    }

    public static Date getDate( String pattern , String dateToParse) throws ParseException {

        if (pattern.equals("dd MMMM yyyy")) {
            DateFormat inputFormat = new SimpleDateFormat("dd MMMM yyyy", RusDateFormatSymbols);
            return inputFormat.parse(dateToParse);
        } else if (pattern.equals("dd MMMM yyyy HH:mm")) {
            DateFormat inputFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", RusDateFormatSymbols);
            return inputFormat.parse(dateToParse);
        } else{
            DateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            return simpleDateFormat.parse(dateToParse);
        }
    }

    public static Date getDateTodayYesterday(String date) throws ParseException {
        if (date.equals("сегодня")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(sdf.format(new Date()));
        }else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(sdf.format(DateUtils.addDays(new Date(), -1)));
        }
    }

    public static Date decideDate(String dateString) throws ParseException {
        Date date;
        if (dateString.contains("вчера")){
            date = Util.getDateTodayYesterday("вчера");
        }else if (dateString.contains("сегодня")){
            date = Util.getDateTodayYesterday("сегодня");
        }else{
            date = Util.getDate("dd.MM.yyyy", dateString.split(": ")[1]);
        }
        return date;
    }

    private static DateFormatSymbols RusDateFormatSymbols = new DateFormatSymbols(){

        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }

    };

    public static boolean isPaginationWorks(String[] card1, String[] card2){
        if (card1[0].equals(card2[0]))
            return card1[1].equals(card2[1]);
        else return true;
    }

    public static boolean isElementsCountPresented(WebDriver driver, int count){
        List listOfTransactions = $$(By.cssSelector(".card-details.tr"));
        return listOfTransactions.size() == count;
    }

    public static boolean isDateArraySorted(ArrayList<WebElement> dates, boolean descending) throws ParseException {

        boolean isSorted = true;

        if (descending) {
            for (int i = 1; i < dates.size() - 1; i++) {
                Date date1, date2;
                if (dates.get(i).getText().contains("сегодня")) {
                    date1 = Util.getDateTodayYesterday("сегодня");
                } else if (dates.get(i).getText().contains("вчера")) {
                    date1 = Util.getDateTodayYesterday("вчера");
                } else {
                    date1 = Util.getDate("dd.MM.yyyy", dates.get(i).getText().split(":")[1].substring(1, 11));
                }

                if (dates.get(i++).getText().contains("сегодня")) {
                    date2 = Util.getDateTodayYesterday("сегодня");
                } else if (dates.get(i++).getText().contains("вчера")) {
                    date2 = Util.getDateTodayYesterday("вчера");
                } else {
                    date2 = Util.getDate("dd.MM.yyyy", dates.get(i++).getText().split(":")[1].substring(1, 11));
                }

                if(!(date1.before(date2) || date1.equals(date2))){
                    isSorted = false;
                    break;
                }

            }

        } else{

            for (int i = 1; i < dates.size() - 1; i++) {

                Date date1, date2;
                if (dates.get(i).getText().contains("сегодня")) {
                    date1 = Util.getDateTodayYesterday("сегодня");
                } else if (dates.get(i).getText().contains("вчера")) {
                    date1 = Util.getDateTodayYesterday("вчера");
                } else {
                    date1 = Util.getDate("dd.MM.yyyy", dates.get(i).getText());
                }

                if (dates.get(i++).getText().contains("сегодня")) {
                    date2 = Util.getDateTodayYesterday("сегодня");
                } else if (dates.get(i++).getText().contains("вчера")) {
                    date2 = Util.getDateTodayYesterday("вчера");
                } else {
                    date2 = Util.getDate("dd.MM.yyyy", dates.get(i++).getText());
                }

                if(!(date1.after(date2) || date1.equals(date2))){
                    isSorted = false;
                    break;
                }

            }
        }
        return isSorted;
    }
    public static boolean isNameListSorted(ArrayList<WebElement> names, boolean descending) {
        boolean isSorted = true;

        if (descending) {
            for (int i = 1; i < names.size() - 1; i++) {
                char name1 = names.get(i).getText().toCharArray()[0];
                char name2 = names.get(i + 1).getText().toCharArray()[0];
                System.out.println(name1 + " = " + (int) name1 + " " + name2 + " " + (int) name2);
                if (!(name1 > name2 || name1 == name2)) {
                    System.out.println("Here");
                    isSorted = false;
                    break;
                }
            }
        } else {
            for (int i = 1; i < names.size() - 1; i++) {
                char name1 = names.get(i).getText().toCharArray()[0];
                char name2 = names.get(i + 1).getText().toCharArray()[0];
                System.out.println(name1 + " = " + (int) name1 + " " + name2 + " " + (int) name2);
                if (!(name1 < name2 || name1 == name2)) {
                    System.out.println("Here");
                    isSorted = false;
                    break;
                }
            }
        }

        return isSorted;
    }

    public static boolean isCarsCountPresented( int count){
        ElementsCollection listOfCars = $$(byCssSelector("div.car-details.tr")); //driver.findElements(By.cssSelector("car-details > div > div.col-car.td > span"));
        return listOfCars.size() == count;

    }

    public static void closeRocketChat(WebDriver driver)  {

        if (driver instanceof JavascriptExecutor) {
            try {
                Thread.sleep(1000);
                ((JavascriptExecutor) driver)
                        .executeScript("document.getElementsByClassName('rocketchat-widget')[0].remove();");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static boolean areTwoPicturesIdential(File picture1, File picture2){

        BufferedImage img1 = null;
        BufferedImage img2 = null;

        double percentage = 0.0;

        try
        {
            img1 = ImageIO.read(picture1);
            img2 = ImageIO.read(picture2);
        }
        catch (IOException e)
        {
            log.error(e);
        }

        int width1 = img1.getWidth();
        int width2 = img2.getWidth();

        int height1 = img1.getHeight();
        int height2 = img2.getHeight();

        if ((width1 != width2) || (height1 != height2))
            log.error("Праметры изображений не совпадают");
        else
        {
            long difference = 0;
            for (int y = 0; y < height1; y++)
            {
                for (int x = 0; x < width1; x++)
                {
                    int rgbA = img1.getRGB(x, y);
                    int rgbB = img2.getRGB(x, y);
                    int redA = (rgbA >> 16) & 0xff;
                    int greenA = (rgbA >> 8) & 0xff;
                    int blueA = (rgbA) & 0xff;
                    int redB = (rgbB >> 16) & 0xff;
                    int greenB = (rgbB >> 8) & 0xff;
                    int blueB = (rgbB) & 0xff;

                    difference += Math.abs(redA - redB);
                    difference += Math.abs(greenA - greenB);
                    difference += Math.abs(blueA - blueB);
                }
            }

            double total_pixels = width1 * height1 * 3;

            double avg_different_pixels = difference / total_pixels;

            percentage = (avg_different_pixels / 255) * 100;
        }
        return percentage < 1;
    }
}
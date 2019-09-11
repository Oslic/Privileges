package ru.fleetcor.pages.Privileges;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.testng.ITest;
import org.testng.asserts.SoftAssert;
import ru.fleetcor.autotests.BaseTest;
import ru.fleetcor.util.Util;
import ru.yandex.qatools.allure.annotations.Step;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static ru.fleetcor.autotests.BaseTest.properties;


public class Privileges extends BaseTest {



    @Step("Заходим в клуб привилегий")
    public static void logopassClub() throws InterruptedException {
        open("https://ppr-v3.transitcard.ru");
        $(By.id("login")).sendKeys("yappr@lk.ru");
        $(By.id("password")).sendKeys("1");
       // Thread.sleep(5000);
        if ($(byXpath("//input[@id='accept-UA']")).isSelected() == false) {
            $(byXpath("//label[contains(text(),'Принимаю условия')]")).click();}
            $(byXpath("//div[@class='button btn-yellow lg close-right-pane']")).click();
            //Thread.sleep(5000);
            $(byXpath("//a[@class='adv-nav-elem']//span[contains(text(),'Клуб привилегий')]")).click();

    }

    @Step("Проверяем кнопку получения привилегии")
    public static void gotoVTB() throws InterruptedException {
        $(byXpath("//div[@class='rp-content']//div//button[@class='button btn-yellow']")).click();
        Thread.sleep(5000);
    }

    @Step("Добавляем привилегию в Транзит")
    public static void addtoTransit() throws InterruptedException, ClassNotFoundException {
        System.setProperty("oracle.net.tns_admin", "C:\\app\\Aleksei.Morozov\\product\\11.2.0\\client_1\\network\\admin");
        Class.forName("oracle.jdbc.OracleDriver");
        String request = "INSERT INTO CONT_PRIVILEGE" +
                "(ID,ID_TYPE,NAME,SDESC,FDESC,POSITION,DATEOUT,SHOW_DATE,CUSTOM,URL,SHOW_IN_PO,IMG)" +
                "VALUES('AAAAAAFFFFFFFCCCCCCVVVVVVDDFFCVB'," +
                "3," +
                "'Автотестовая привилегия' " +
                ",'Привилегия для автоматизированного тестирования '" +
                ",'Привилегия для автоматизированного тестирования. Будет создаваться и удаляться при каждом автотесте. При нажатии на кнопку кидает на сайт ВТБ '" +
                ",5" +
                ",NULL" +
                ",0" +
                ",0" +
                ",'https://www.vtb.ru'" +
                ",1" +
                ",NULL)";

        Connection trans = null;
        Connection lk = null;
        Statement stmt = null;
        Statement stmt2 = null;

        try {
            trans = DriverManager.getConnection("jdbc:oracle:thin:@srv-t3-test.transitcard.ru:1521:SUN", "TRANSIT2", "TRANSIT2");
            System.out.println("Connection established TRANSIT2");
            stmt = trans.createStatement();
            stmt.executeQuery(request);
            lk = DriverManager.getConnection("jdbc:oracle:thin:@srv-fly-qa.transitcard.ru:1521:WEBDB", "PERSONAL_OFFICE", "PERSONAL_OFFICE");
            System.out.println("Connection established LK");
            stmt2 = lk.createStatement();
            stmt2.addBatch("begin DBMS_MVIEW.REFRESH('mv_cont_privilege','f'); end;");
            stmt2.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e) {
            }
            if (stmt2 != null) try {
                stmt2.close();
            } catch (Exception e) {
            }
            if (trans != null) try {
                trans.close();
            } catch (Exception e) {
            }
            if (lk != null) try {
                lk.close();
            } catch (Exception e) {
            }
        }
        Thread.sleep(3000);
    }

    @Step("Удаляем привилегию из Транзита")
    public static void removefromTransit() throws InterruptedException {
        Connection trans = null;
        Connection lk = null;
        Statement stmt = null;
        Statement stmt2 = null;

        try {
            trans = DriverManager.getConnection("jdbc:oracle:thin:@srv-t3-test.transitcard.ru:1521:SUN", "TRANSIT2", "TRANSIT2");
            System.out.println("Connection established TRANSIT2");
            stmt = trans.createStatement();
            stmt.addBatch("begin CLIENT_SERVICES_PKG.DEL_PRIVILEGES('AAAAAAFFFFFFFCCCCCCVVVVVVDDFFCVB'); end;");
            stmt.executeBatch();
            lk = DriverManager.getConnection("jdbc:oracle:thin:@srv-fly-qa.transitcard.ru:1521:WEBDB", "PERSONAL_OFFICE", "PERSONAL_OFFICE");
            System.out.println("Connection established LK");
            stmt2 = lk.createStatement();
            stmt2.addBatch("begin DBMS_MVIEW.REFRESH('mv_cont_privilege','f'); end;");
            stmt2.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e) {
            }
            if (stmt2 != null) try {
                stmt2.close();
            } catch (Exception e) {
            }
            if (trans != null) try {
                trans.close();
            } catch (Exception e) {
            }
            if (lk != null) try {
                lk.close();
            } catch (Exception e) {
            }
        }
        Thread.sleep(3000);

    }

    @Step("Проверяем, что привилегия удалилась")
    public void checkDelLK() throws InterruptedException {
        logopassClub();
        softAssert.assertFalse($(byXpath("//div[@class='priv-text description'][contains(text(),'Привелегия для автоматизированного тестирования')]")).exists());
        softAssert.assertAll();
    }


}

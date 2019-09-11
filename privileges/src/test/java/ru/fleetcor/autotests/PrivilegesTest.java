package ru.fleetcor.autotests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.fleetcor.HttpClient.ITTestHttpClient;
import ru.fleetcor.pages.Privileges.Privileges;
import ru.yandex.qatools.allure.annotations.Description;


import java.io.IOException;

import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import java.sql.*;

import java.util.ArrayList;


public  class PrivilegesTest extends BaseTest {

    //public Privileges privileges;

    @AfterMethod
    private void sendTestResult(ITestResult iTestResult) throws InterruptedException {
        Privileges.removefromTransit();
        Privileges.logopassClub();
        Thread.sleep(1000);
        softAssert.assertFalse($(byXpath("//div[@class='priv-text description'][contains(text(),'Привилегия для автоматизированного тестирования')]")).exists());
        softAssert.assertAll();
        String testName = iTestResult.getMethod().getMethodName();
        int status = iTestResult.getStatus();
        ITTestHttpClient.sendTestsResults(testName, status);
        getWebDriver().quit();
    }

    @BeforeMethod
    private void openCompanySettingsPage() throws InterruptedException, IOException, ClassNotFoundException {
        Privileges.addtoTransit();
        Configuration.startMaximized = true;
    }

    @Test
    @Description("Проверка отображения привилегии в ЛК")
    public void checkPrivilege() throws InterruptedException {
        Privileges.logopassClub();
        $(byXpath("//div[@class='priv-text description'][contains(text(),'Привилегия для автоматизированного тестирования')]")).click();
        Thread.sleep(1000);
        softAssert.assertTrue($(byXpath("//div[@class='description'][contains(text(),'Привилегия для автоматизированного тестирования')]")).isDisplayed());
        softAssert.assertAll();
        softAssert.assertTrue($(byXpath("//div[@class='full-description description'][contains(text(),'Привилегия для автоматизированного тестирования')]")).isDisplayed());
        softAssert.assertAll();
        Privileges.gotoVTB();
        ArrayList<String> tabs2 = new ArrayList<String> ( getWebDriver().getWindowHandles());
        getWebDriver().switchTo().window(tabs2.get(1));
        softAssert.assertEquals("https://www.vtb.ru/",getWebDriver().getCurrentUrl());
        System.out.println(getWebDriver().getCurrentUrl());
        getWebDriver().switchTo().window(tabs2.get(0));
        $(byXpath("//nct-right-pane/div[@class='rp-cross']/i")).click();
        $(byLinkText("Выйти")).click();
        softAssert.assertAll();

    }


}

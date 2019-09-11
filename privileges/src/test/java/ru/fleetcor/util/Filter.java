package ru.fleetcor.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan.Zhirnov on 08.11.2018.
 */
public class Filter {

    private static List<WebElement> list;
    private static String selector;
    private static WebElement inputField;
    private static WebDriver driver;
    public Filter(WebDriver driver, String selector, WebElement inputField) {
        this.driver = driver;
        this.selector = selector;
        this.list = new ArrayList<>();
        this.list = getList();
        this.inputField = inputField;
    }

    public void selectItem(int number) throws Exception {
        inputField.click();
        if (number <= list.size()) {
            list.get(number - 1).click();
        }
        else throw new Exception("List index is out of range");
    }

    public void inputAndSelectFirst(String string) throws Exception {
        inputField.click();
        inputField.sendKeys(string);
        list = getList();
        inputField.click();
        selectItem(1);
    }

    private List<WebElement> getList() {
        return driver.findElements(By.cssSelector(selector));
    }
}

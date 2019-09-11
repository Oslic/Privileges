package ru.fleetcor.util.retry;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Created by Ivan.Zhirnov on 08.11.2018.
 */
public class Retry implements IRetryAnalyzer {
    private int count = 0;
    private static int maxCount = 1;
    @Override
    public boolean retry(ITestResult iTestResult) {
        if (!iTestResult.isSuccess()) {
            if (count < maxCount) {
                count++;
                iTestResult.setStatus(ITestResult.FAILURE);
                return true;
            } else {
                iTestResult.setStatus(ITestResult.FAILURE);
            }
        } else {
            iTestResult.setStatus(ITestResult.SUCCESS);
        }
        return false;
    }
}

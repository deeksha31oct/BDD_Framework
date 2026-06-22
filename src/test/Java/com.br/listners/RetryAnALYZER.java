package com.br.listners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnALYZER implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY = 2;   // retry a failed test up to 2 times

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {
            retryCount++;
            System.out.println("Retrying '" + result.getName()
                    + "' | Attempt: " + retryCount + " of " + MAX_RETRY);
            return true;    // true = run the test again
        }
        return false;       // false = stop retrying, mark as failed
    }
}
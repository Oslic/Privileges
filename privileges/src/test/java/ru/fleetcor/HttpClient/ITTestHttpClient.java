package ru.fleetcor.HttpClient;

import okhttp3.*;
import org.json.simple.JSONObject;
import java.io.IOException;

public class ITTestHttpClient {

    private static OkHttpClient client = new OkHttpClient();

    private static final String setTestResultURL = "http://172.20.144.24/api/Public/SetAutoTestResult";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static JSONObject jsonTestresult = new JSONObject();

    private static Request.Builder requestWithHeaders = new Request.Builder()
            .header("secretKeyBase64", "ZzU1UmtWWHVaaGNOTDJoMA==")
            .header("userName", "admin")
            .header("Content-Type","application/json");


    public static void startTest(String testRunId, int testPlanGlobalId){

        RequestBody requestBody = RequestBody.create(JSON, "");

        Request request = requestWithHeaders
                .url("http://172.20.144.24/api/Public/StartTestRun/" + testRunId)
                .post(requestBody)
                .build();

        try {
           client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setJsonRows(testRunId, testPlanGlobalId);
    }

    public static void sendTestsResults(String testName, int testStatus){

        JSONObject json = jsonTestresult;

        json.put("autoTestExternalId", testName);

        String outcome = getOutcomeByStatus(testStatus);

        json.put("outcome", outcome);

        RequestBody requestBody = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .header("secretKeyBase64", "ZzU1UmtWWHVaaGNOTDJoMA==")
                .header("userName", "admin")
                .header("Content-Type","application/json")
                .url(setTestResultURL)
                .post(requestBody)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setJsonRows(String testRunId, int testPlanGlobalId){

        jsonTestresult.put("testRunId", testRunId);
        jsonTestresult.put("testPlanGlobalId", testPlanGlobalId);
        jsonTestresult.put("configurationGlobalId", 18);
        jsonTestresult.put("status", "Ready");
        jsonTestresult.put("message", "");
        jsonTestresult.put("stackTrace", "");

    }


    private static String getOutcomeByStatus(int status){

        String outcome = "";

        switch (status){

            case 1:
                outcome = "Passed";
                break;

            case 2:
                outcome = "NotPassed";
                break;

            case 3:
                outcome = "Skipped";
                break;

            default:
                break;
        }

        return outcome;
    }

}

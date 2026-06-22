package com.br.cucumber;

public class TestContext {

    private final runTimeTestData runTimeData;

    public TestContext() {
        runTimeData = new runTimeTestData();
    }

    public runTimeTestData getRunTimeTestData() {
        return runTimeData;
    }
}
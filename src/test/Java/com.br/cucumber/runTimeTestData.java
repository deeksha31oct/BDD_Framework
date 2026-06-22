package com.br.cucumber;

import java.util.HashMap;
import java.util.Map;

public class runTimeTestData {

    // holds values captured during a scenario at runtime
    private final Map<String, Object> contextData = new HashMap<>();

    // SET / store a value into the context
    public void setContext(String key, Object value) {
        contextData.put(key, value);
        System.out.println("Stored in context -> " + key + " = " + value);
    }

    // FETCH / get a value from the context
    public Object getContext(String key) {
        return contextData.get(key);
    }

    // fetch as String (convenience)
    public String getContextAsString(String key) {
        Object value = contextData.get(key);
        return value == null ? null : value.toString();
    }

    // check if a key exists
    public boolean isKeyPresent(String key) {
        return contextData.containsKey(key);
    }


    // clear all runtime data (e.g. after a scenario)
    public void clearContext() {
        contextData.clear();
    }
}
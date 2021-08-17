package com.shakil.barivara.model;

public class TestSpinnerData {
    private String Id;
    private String Value;

    public TestSpinnerData(String id, String value) {
        Id = id;
        Value = value;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}

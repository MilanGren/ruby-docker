package com.example.springboot;

import lombok.Getter;

@Getter
public class ResultPdfaValidation {

    private final String filename;
    public boolean isvalid;
    private String value = "Failed";
    private String flavourId;
    private String iso;

    public ResultPdfaValidation(String filename) {
        this.filename = filename;
    }

    public void create(boolean isvalid, String flavourId, String iso) {
        this.isvalid = isvalid;
        if (isvalid) {
            value = "Passed";
        }
        this.flavourId = flavourId;
        this.iso = iso;
    }

    public String getValue() {
        return value;
    }

    public String getFlavourId() {
        return flavourId;
    }

    public String getFilename() {
        return filename;
    }

    public String getIso() {
        return iso;
    }

    @Override
    public String toString() {
        return "filename: " + filename + ", value: " + getValue() + ", flavourId: " + getFlavourId() + ", iso: " + getIso();
    }

    public void setAskedFlavourId(String flavourId) {
        this.flavourId = flavourId;
    }

}
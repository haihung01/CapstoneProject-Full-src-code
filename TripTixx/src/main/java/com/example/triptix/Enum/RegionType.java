package com.example.triptix.Enum;

public enum RegionType {
    BAC("Bắc"),
    TRUNG("Trung"),
    NAM("Nam"),
    ORTHER("Hỗn hợp");

    private String value;
    private RegionType(String value){
        this.value = value;
    }
    public String getValue(){
        return value;
    }
}
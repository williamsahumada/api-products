package com.test.api.product.service;

/**
 * Title: EnumUploadFileType.java <br>
 * Copyright: Williams Ahumada <br>
 * Company: Teste <br>
 * <p/>
 *
 */


public enum EnumUploadFileType {

    XLS(1,"xls", "Excel"),
    CVS(3, "csv", "Excel_csv"),
    XLSX(4, "xlsx", "Excel_Xlsx");

    private int value;
    private String name;
    private String description;

    private EnumUploadFileType(int value, String name, String description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getName(){
        return  name;
    }

    public String getDescription(){
        return  description;
    }

    public static EnumUploadFileType valueOf(int value) {
        for (EnumUploadFileType edt : values()) {
            if (edt.getValue() == value) {
                return edt;
            }
        }
        return null;
    }
    public static EnumUploadFileType getByName(String value) {
        for (EnumUploadFileType edt : values()) {
            if (edt.getName().equals(value)) {
                return edt;
            }
        }
        return null;
    }

}


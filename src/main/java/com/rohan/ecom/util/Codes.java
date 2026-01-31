package com.rohan.ecom.util;

public enum Codes {
    SUCCESS(0), FAIL(-1);

    private final int code;

    Codes(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}

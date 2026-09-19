package com.openecom.ecom.exceptions;

public class ProductQuantityExceededException extends RuntimeException {
    public ProductQuantityExceededException(String msg) {
        super(msg);
    }
}

package com.rohan.ecom.exceptions;

public class UserDetailsNotFoundException extends RuntimeException {
    public UserDetailsNotFoundException(String msg) {
        super(msg);
    }
}

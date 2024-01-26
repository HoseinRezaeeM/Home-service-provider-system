package com.example.homeserviceprovider.exception;

public class DuplicatePasswordException extends RuntimeException {

    public DuplicatePasswordException(String message) {
        super(message);
    }
}

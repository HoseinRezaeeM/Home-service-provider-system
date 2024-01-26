package com.example.homeserviceprovider.exception;

public class OrderIsNotExistException extends RuntimeException {

    public OrderIsNotExistException(String message) {
        super(message);
    }
}

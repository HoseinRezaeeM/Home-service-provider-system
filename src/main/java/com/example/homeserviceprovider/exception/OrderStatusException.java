package com.example.homeserviceprovider.exception;

public class OrderStatusException extends RuntimeException {
    public OrderStatusException(String message) {
        super(message);
    }
}

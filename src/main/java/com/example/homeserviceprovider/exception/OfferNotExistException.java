package com.example.homeserviceprovider.exception;

public class OfferNotExistException extends RuntimeException {
    public OfferNotExistException(String message) {
        super(message);
    }
}

package com.exceptions;

public class InvalidMarkException extends RuntimeException {

    public InvalidMarkException(String message) {
        super(message);
    }
}
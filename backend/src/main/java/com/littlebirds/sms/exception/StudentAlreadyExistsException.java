package com.littlebirds.sms.exception;

public class StudentAlreadyExistsException extends RuntimeException {

    public StudentAlreadyExistsException(String message) {
        super(message);
    }
}

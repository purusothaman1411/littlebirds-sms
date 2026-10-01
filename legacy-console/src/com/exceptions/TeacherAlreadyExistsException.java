package com.exceptions;

public class TeacherAlreadyExistsException extends RuntimeException {

    public TeacherAlreadyExistsException(String message) {
        super(message);
    }
}
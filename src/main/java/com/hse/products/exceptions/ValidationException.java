package com.hse.products.exceptions;

public class ValidationException extends ApiException {

    public ValidationException(ExceptionKeyEnum exceptionKey, String message) {
        super(exceptionKey, message);
    }
}

package com.hse.products.exceptions;

public class NotFoundException extends ApiException {

    public NotFoundException(ExceptionKeyEnum exceptionKey, String message) {
        super(exceptionKey, message);
    }
}

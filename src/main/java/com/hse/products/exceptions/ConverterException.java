package com.hse.products.exceptions;

public class ConverterException extends ApiException {

    public ConverterException(ExceptionKeyEnum exceptionKey, String message) {
        super(exceptionKey, message);
    }
}

package com.hse.products.exceptions;

import lombok.Getter;

@Getter
public class ApiException extends Exception {

    private ExceptionKeyEnum exceptionKey;

    public ApiException(ExceptionKeyEnum exceptionKey, String message) {
        super(message);
        this.exceptionKey = exceptionKey;
    }
}

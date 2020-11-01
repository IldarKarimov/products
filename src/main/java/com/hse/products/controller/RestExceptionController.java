package com.hse.products.controller;

import com.hse.products.exceptions.ApiException;
import com.hse.products.exceptions.ConverterException;
import com.hse.products.exceptions.ExceptionKeyEnum;
import com.hse.products.exceptions.NotFoundException;
import com.hse.products.model.Error;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @ExceptionHandler(UndeclaredThrowableException.class)
    public final ResponseEntity<Error> handleUndeclaredExceptions(UndeclaredThrowableException ex) {
        Throwable originalException = ex.getCause();
        if (originalException instanceof ApiException) {
            ResponseEntity apiResponseEntity = getApiResponseEntity((ApiException) originalException);
            return apiResponseEntity;
        }
        return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<String> handleInvalidArgumentExceptions(Exception ex) {
        return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public final ResponseEntity<String> handleConversionExceptions(Exception ex) {
        return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Error> handleAllExceptions(Exception ex) {
        return new ResponseEntity(getInternalServerErrorException(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity getApiResponseEntity(ApiException ex) {
        if (ex instanceof NotFoundException) {
            return getResponseEntityNotFound(ex);
        }
        if(ex instanceof ConverterException) {
            return getResponseEntityInternalServerError(ex);
        }
        return getResponseEntityBadRequest(ex);
    }

    private ResponseEntity getResponseEntityInternalServerError(ApiException ex) {
        return new ResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity getResponseEntityNotFound(ApiException ex) {
        return new ResponseEntity(getExceptionResponse(ex), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity getResponseEntityBadRequest(ApiException ex) {
        return new ResponseEntity(getExceptionResponse(ex), HttpStatus.BAD_REQUEST);
    }

    private List<Error> getInternalServerErrorException() {
        Error error = new Error();
        error.setKey(ExceptionKeyEnum.UnknownException.toString());
        error.setDetail("An error occured. Please try again later");
        return Collections.singletonList(error);
    }

    private List<Error> getExceptionResponse(ApiException ex) {
        Error error = new Error();
        error.setKey(ex.getExceptionKey().toString());
        error.setDetail(ex.getMessage());
        return Collections.singletonList(error);
    }
}

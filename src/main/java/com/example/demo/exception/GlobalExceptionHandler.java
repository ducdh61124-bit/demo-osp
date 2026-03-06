package com.example.demo.exception;

import com.example.demo.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleNotFound(ResourceNotFoundException ex) {
        String msg = messageSource.getMessage(ex.getMessage(), ex.getArgs(), LocaleContextHolder.getLocale());
        return new ApiResponse(404, msg, null, "Not Found");
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleRuntimeException(RuntimeException ex) {
        String msg;
        try {
            msg = messageSource.getMessage(ex.getMessage(), null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            msg = ex.getMessage();
        }
        return new ApiResponse(400, msg, null, "Bad Request");
    }
}
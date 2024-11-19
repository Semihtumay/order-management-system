package com.semihtumay.orderservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonConversionException extends RuntimeException {
    public JsonConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}

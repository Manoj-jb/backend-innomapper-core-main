package com.innowell.core.core.exception;

public class CustomInnowellException extends RuntimeException {

    public CustomInnowellException() {
        super();
    }

    public CustomInnowellException(String message) {
        super(message);
    }

    public CustomInnowellException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomInnowellException(Throwable cause) {
        super(cause);
    }
}

package com.finance.finance.exception;

//Unauthorized Action Exception
public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}

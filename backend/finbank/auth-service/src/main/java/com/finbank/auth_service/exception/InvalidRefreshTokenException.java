package com.finbank.auth_service.exception;

public class InvalidRefreshTokenException extends RuntimeException{
    public InvalidRefreshTokenException(String Message){
        super(Message);
    }
}

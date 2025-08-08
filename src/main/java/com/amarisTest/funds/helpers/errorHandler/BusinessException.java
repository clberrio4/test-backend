package com.amarisTest.funds.helpers.errorHandler;

public class BusinessException extends AppException{
    public BusinessException(String message, int status) {
        super(message, status);
    }
}

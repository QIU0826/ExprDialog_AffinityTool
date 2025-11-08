package com.exprdialog.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    // 错误码
    private final String errorCode;
    
    // 添加getErrorCode()方法，供GlobalExceptionHandler使用
    public String getErrorCode() {
        return errorCode;
    }

    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCode.BUSINESS_ERROR.getCode();
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode.getCode();
    }
}
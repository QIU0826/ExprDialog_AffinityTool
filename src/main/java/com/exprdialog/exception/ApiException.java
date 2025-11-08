package com.exprdialog.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    // API名称
    private final String apiName;
    // 错误码
    private final String errorCode;
    // 响应数据
    private final Object responseData;
    
    // 添加getCode方法返回errorCode
    public String getCode() {
        return errorCode;
    }
    
    // 重写getMessage方法
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public ApiException(String message) {
        super(message);
        this.apiName = null;
        this.errorCode = ErrorCode.API_ERROR.getCode();
        this.responseData = null;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.apiName = null;
        this.errorCode = ErrorCode.API_ERROR.getCode();
        this.responseData = null;
    }

    public ApiException(String apiName, String message) {
        super(message);
        this.apiName = apiName;
        this.errorCode = ErrorCode.API_ERROR.getCode();
        this.responseData = null;
    }

    public ApiException(String apiName, String message, Throwable cause) {
        super(message, cause);
        this.apiName = apiName;
        this.errorCode = ErrorCode.API_ERROR.getCode();
        this.responseData = null;
    }

    public ApiException(String apiName, String message, String errorCode) {
        super(message);
        this.apiName = apiName;
        this.errorCode = errorCode;
        this.responseData = null;
    }

    public ApiException(String apiName, String message, String errorCode, Object responseData) {
        super(message);
        this.apiName = apiName;
        this.errorCode = errorCode;
        this.responseData = responseData;
    }

    public ApiException(String apiName, String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.apiName = apiName;
        this.errorCode = errorCode;
        this.responseData = null;
    }
}
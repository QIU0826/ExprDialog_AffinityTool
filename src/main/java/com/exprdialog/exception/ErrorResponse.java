package com.exprdialog.exception;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class ErrorResponse {
    // 错误码
    private String code;
    // 错误消息
    private String message;
    // 额外信息
    private Map<String, String> details = new HashMap<>();

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(String code, String message, Map<String, String> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }
}
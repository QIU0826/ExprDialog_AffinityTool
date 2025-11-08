package com.exprdialog.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 系统错误
    SYSTEM_ERROR("SYS001", "系统内部错误"),
    
    // 业务错误
    BUSINESS_ERROR("BUS001", "业务处理失败"),
    
    // 参数错误
    INVALID_PARAMS("PARAM001", "参数格式错误"),
    MISSING_PARAMS("PARAM002", "缺少必要参数"),
    
    // 资源错误
    RESOURCE_NOT_FOUND("RES001", "请求的资源不存在"),
    RESOURCE_ALREADY_EXISTS("RES002", "资源已存在"),
    
    // API错误
    API_ERROR("API001", "第三方API调用失败"),
    API_TIMEOUT("API002", "第三方API调用超时"),
    API_PERMISSION_DENIED("API003", "API权限不足"),
    
    // 数据库错误
    DATABASE_ERROR("DB001", "数据库操作失败"),
    
    // 表情识别错误
    EMOTION_DETECT_ERROR("EMO001", "表情识别失败"),
    FACE_DETECT_ERROR("EMO002", "人脸检测失败"),
    
    // 好感度错误
    AFFINITY_UPDATE_ERROR("AFF001", "好感度更新失败"),
    AFFINITY_CALC_ERROR("AFF002", "好感度计算失败"),
    
    // 对话错误
    DIALOG_GENERATE_ERROR("DIA001", "对话生成失败"),
    HISTORY_QUERY_ERROR("DIA002", "对话历史查询失败"),
    
    // 用户错误
    USER_NOT_FOUND("USER001", "用户不存在"),
    USER_AUTH_FAILED("USER002", "用户认证失败");
    
    // 错误码
    private final String code;
    // 错误消息
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    // 手动添加getter方法，确保编译通过
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
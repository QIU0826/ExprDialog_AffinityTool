package com.exprdialog.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    // 资源ID
    private final Object resourceId;
    // 资源类型
    private final String resourceType;

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceId = null;
        this.resourceType = null;
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.resourceId = null;
        this.resourceType = null;
    }

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("Resource %s with id %s not found", resourceType, resourceId));
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }

    public ResourceNotFoundException(String resourceType, Object resourceId, Throwable cause) {
        super(String.format("Resource %s with id %s not found", resourceType, resourceId), cause);
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }
}
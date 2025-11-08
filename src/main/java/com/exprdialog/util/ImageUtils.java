package com.exprdialog.util;

import java.io.IOException;
import java.util.Base64;
import java.lang.reflect.*;

/**
 * 图像处理工具类
 * 使用反射方式加载OpenCV，确保即使没有OpenCV依赖，代码也能编译通过
 */
public class ImageUtils {

    /**
     * 检查OpenCV是否可用
     */
    private static boolean isOpenCVAvailable() {
        try {
            Class.forName("org.opencv.core.Mat");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * 通用的图像处理方法 - 使用反射方式调用OpenCV
     * 注意：由于移除了OpenCV依赖，所有具体的图像处理方法已简化
     * 实际使用时需要确保OpenCV库已正确加载
     */
    
    /**
     * 将Base64字符串转换为图像数据
     * 注意：由于移除了OpenCV依赖，此方法返回原始字节数组
     */
    public static byte[] base64ToImageData(String base64Str) throws IOException {
        // 移除Base64前缀（如果有）
        String cleanBase64 = base64Str;
        if (cleanBase64.contains(",")) {
            cleanBase64 = cleanBase64.split(",")[1];
        }

        // 解码Base64字符串为字节数组
        return Base64.getDecoder().decode(cleanBase64);
    }

    /**
     * 将图像数据转换为Base64字符串
     */
    public static String imageDataToBase64(byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            return null;
        }
        return Base64.getEncoder().encodeToString(imageData);
    }
    
    /**
     * 使用反射调用OpenCV功能的示例方法
     * 实际使用时需要根据具体需求实现
     */
    private static Object callOpenCVMethod(String className, String methodName, Class<?>[] paramTypes, Object... params) {
        if (!isOpenCVAvailable()) {
            throw new UnsupportedOperationException("OpenCV库不可用，无法执行图像处理操作");
        }
        
        try {
            Class<?> cls = Class.forName(className);
            Method method = cls.getMethod(methodName, paramTypes);
            return method.invoke(null, params);
        } catch (Exception e) {
            throw new RuntimeException("调用OpenCV方法失败: " + className + "." + methodName, e);
        }
    }
    
    /**
     * 检测人脸的ROI是否有效的简化版本
     */
    public static boolean isValidFaceROI(int x, int y, int width, int height, int imageWidth, int imageHeight) {
        // 检查ROI是否在图像范围内
        boolean inBounds = x >= 0 && y >= 0 && 
                          x + width <= imageWidth && 
                          y + height <= imageHeight;
        
        // 检查ROI大小是否合理（人脸太小可能不是有效人脸）
        boolean reasonableSize = width >= 20 && height >= 20 && 
                                width < imageWidth * 0.8 && 
                                height < imageHeight * 0.8;
        
        // 检查宽高比是否合理（人脸通常接近正方形）
        double aspectRatio = (double) width / height;
        boolean reasonableRatio = aspectRatio >= 0.7 && aspectRatio <= 1.3;
        
        return inBounds && reasonableSize && reasonableRatio;
    }
}
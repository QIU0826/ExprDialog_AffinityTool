package com.exprdialog.util;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.regex.Pattern;

public class CommonUtils {

    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    // 手机号正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 生成UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 判断是否为有效的邮箱地址
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 判断是否为有效的手机号
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 将Base64字符串转换为字节数组
     */
    public static byte[] base64ToBytes(String base64Str) {
        if (base64Str == null || base64Str.isEmpty()) {
            return null;
        }
        // 去除Base64前缀（如果有）
        String cleanBase64 = base64Str;
        if (cleanBase64.contains("base64,") || cleanBase64.contains("BASE64,")) {
            cleanBase64 = cleanBase64.split(",")[1];
        }
        return Base64.decodeBase64(cleanBase64);
    }

    /**
     * 将字节数组转换为Base64字符串
     */
    public static String bytesToBase64(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return Base64.encodeBase64String(bytes);
    }

    /**
     * 将字符串转换为Base64
     */
    public static String stringToBase64(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return Base64.encodeBase64String(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将Base64字符串转换为字符串
     */
    public static String base64ToString(String base64Str) {
        if (base64Str == null || base64Str.isEmpty()) {
            return null;
        }
        byte[] bytes = base64ToBytes(base64Str);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 获取当前时间戳（秒）
     */
    public static long getCurrentTimestamp() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    public static long getCurrentTimestampMillis() {
        return LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    /**
     * 截断字符串
     */
    public static String truncateString(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }

    /**
     * 判断对象是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).isEmpty();
        }
        return false;
    }

    /**
     * 生成随机数
     */
    public static int generateRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

    /**
     * 限制数值在指定范围内
     */
    public static int clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * 限制数值在指定范围内
     */
    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }
}
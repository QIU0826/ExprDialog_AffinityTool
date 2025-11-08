package com.exprdialog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Configuration
public class OpenCVConfig {
    private static final Logger log = LoggerFactory.getLogger(OpenCVConfig.class);

    @Value("${opencv.haar-cascade-path}")
    private String haarCascadePath;
    
    // OpenCV加载状态
    private boolean isOpenCVLoaded = false;

    /**
     * 初始化OpenCV库（使用反射避免编译时依赖）
     */
    @PostConstruct
    public void initOpenCV() {
        try {
            // 尝试使用反射加载OpenCV核心类
            try {
                Class<?> coreClass = Class.forName("org.opencv.core.Core");
                
                try {
                    // 尝试直接指定OpenCV库的路径（可选）
                    String dllPath = "D:\\opencv\\build\\java\\x64\\opencv_java480.dll";
                    File dllFile = new File(dllPath);
                    if (dllFile.exists()) {
                        System.load(dllPath);
                        log.info("已加载OpenCV DLL: {}", dllPath);
                    } else {
                        log.warn("OpenCV DLL文件不存在: {}, 将尝试使用系统库路径", dllPath);
                    }
                } catch (Exception e) {
                    log.warn("直接加载OpenCV DLL失败: {}, 继续尝试", e.getMessage());
                }
                
                try {
                    Field nativeLibraryNameField = coreClass.getDeclaredField("NATIVE_LIBRARY_NAME");
                    nativeLibraryNameField.setAccessible(true);
                    String nativeLibraryName = (String) nativeLibraryNameField.get(null);
                    
                    // 加载OpenCV本地库
                    System.loadLibrary(nativeLibraryName);
                    isOpenCVLoaded = true;
                    log.info("OpenCV库加载成功: {}", nativeLibraryName);
                    
                    // 验证Haar级联分类器文件是否存在
                    File haarCascadeFile = new File(haarCascadePath);
                    if (!haarCascadeFile.exists()) {
                        log.warn("Haar级联分类器文件不存在: {}", haarCascadePath);
                    } else {
                        log.info("Haar级联分类器文件加载成功: {}", haarCascadePath);
                    }
                } catch (Exception e) {
                    log.warn("OpenCV本地库加载失败: {}", e.getMessage());
                    isOpenCVLoaded = false;
                }
            } catch (ClassNotFoundException e) {
                log.warn("OpenCV类未找到，人脸检测功能将不可用");
                isOpenCVLoaded = false;
            }
        } catch (Exception e) {
            log.warn("OpenCV初始化过程中出现异常: {}", e.getMessage());
            isOpenCVLoaded = false;
        }
        
        // 即使OpenCV加载失败，也允许应用继续运行
        if (!isOpenCVLoaded) {
            log.info("系统将以降级模式运行，人脸检测和表情识别功能将不可用");
        }
    }
    
    /**
     * 提供OpenCV加载状态
     */
    @Bean
    public boolean isOpenCVLoaded() {
        return isOpenCVLoaded;
    }

    /**
     * 提供Haar级联分类器路径
     */
    @Bean
    public String haarCascadePath() {
        return haarCascadePath;
    }
}
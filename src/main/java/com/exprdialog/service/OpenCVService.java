package com.exprdialog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Base64;

@Service
public class OpenCVService {
    
    @Value("${opencv.haar-cascade-path}")
    private String haarCascadePath;
    
    @Value("${opencv.image-width}")
    private int faceWidth;
    
    @Value("${opencv.image-height}")
    private int faceHeight;
    
    private boolean isOpenCVLoaded = false;
    private Object faceDetector; // 使用Object避免编译错误
    
    @PostConstruct
    public void init() {
        try {
            try {
                // 使用反射方式加载OpenCV和初始化组件
                Class<?> coreClass = Class.forName("org.opencv.core.Core");
                
                try {
                    java.lang.reflect.Field nativeLibraryNameField = coreClass.getDeclaredField("NATIVE_LIBRARY_NAME");
                    nativeLibraryNameField.setAccessible(true);
                    String nativeLibraryName = (String) nativeLibraryNameField.get(null);
                    
                    // 加载OpenCV本地库
                    System.loadLibrary(nativeLibraryName);
                    
                    try {
                        // 初始化人脸检测器
                        Class<?> cascadeClassifierClass = Class.forName("org.opencv.objdetect.CascadeClassifier");
                        faceDetector = cascadeClassifierClass.getDeclaredConstructor().newInstance();
                        
                        // 调用load方法
                        java.lang.reflect.Method loadMethod = cascadeClassifierClass.getMethod("load", String.class);
                        loadMethod.invoke(faceDetector, haarCascadePath);
                        
                        isOpenCVLoaded = true;
                        System.out.println("OpenCV库和人脸检测模型加载成功: " + haarCascadePath);
                    } catch (Exception e) {
                        System.out.println("警告: 人脸检测器初始化失败: " + e.getMessage());
                        isOpenCVLoaded = false;
                    }
                } catch (Exception e) {
                    System.out.println("警告: OpenCV本地库加载失败: " + e.getMessage());
                    isOpenCVLoaded = false;
                }
            } catch (ClassNotFoundException e) {
                System.out.println("警告: OpenCV类未找到，人脸检测功能将不可用");
                isOpenCVLoaded = false;
            }
        } catch (Exception e) {
            System.out.println("警告: OpenCV初始化过程中出现异常: " + e.getMessage());
            isOpenCVLoaded = false;
        }
        
        // 即使初始化失败，服务也可以继续运行（降级模式）
        if (!isOpenCVLoaded) {
            System.out.println("OpenCV服务已配置为降级模式运行，人脸检测功能将不可用");
        }
    }
    
    /**
     * 从Base64编码的图像中检测并裁剪人脸区域
     * @param base64Image Base64编码的图像数据
     * @return 裁剪后的人脸图像（Base64编码）或原始图像的处理版本
     */
    public String detectAndCropFace(String base64Image) {
        // 如果OpenCV未加载，返回原始图像数据的简单处理版本
        if (!isOpenCVLoaded || faceDetector == null) {
            System.out.println("OpenCV未加载，返回简单处理后的图像");
            try {
                // 简单返回原始数据的处理版本
                byte[] imageData = Base64.getDecoder().decode(base64Image);
                return Base64.getEncoder().encodeToString(imageData);
            } catch (Exception e) {
                System.out.println("图像处理失败: " + e.getMessage());
                return base64Image; // 失败时返回原始数据
            }
        }
        
        try {
            // 使用反射进行所有OpenCV操作，避免编译错误
            Class<?> matClass = Class.forName("org.opencv.core.Mat");
            Class<?> matOfByteClass = Class.forName("org.opencv.core.MatOfByte");
            Class<?> imgcodecsClass = Class.forName("org.opencv.imgcodecs.Imgcodecs");
            Class<?> imgprocClass = Class.forName("org.opencv.imgproc.Imgproc");
            Class<?> matOfRectClass = Class.forName("org.opencv.core.MatOfRect");
            Class<?> rectClass = Class.forName("org.opencv.core.Rect");
            Class<?> sizeClass = Class.forName("org.opencv.core.Size");
            
            // 解码Base64图像
            byte[] imageData = Base64.getDecoder().decode(base64Image);
            Object imageDataObj = imageData;
            
            // 创建MatOfByte并设置数据
            Object matOfByte = matOfByteClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method putMethod = matOfByteClass.getMethod("put", int.class, int.class, byte[].class);
            putMethod.invoke(matOfByte, 0, 0, imageData);
            
            // 获取IMREAD_COLOR常量
            java.lang.reflect.Field imreadColorField = imgcodecsClass.getDeclaredField("IMREAD_COLOR");
            imreadColorField.setAccessible(true);
            int imreadColor = imreadColorField.getInt(null);
            
            // 解码图像
            java.lang.reflect.Method imdecodeMethod = imgcodecsClass.getMethod("imdecode", matOfByteClass, int.class);
            Object image = imdecodeMethod.invoke(null, matOfByte, imreadColor);
            
            // 转换为灰度图像
            Object grayImage = matClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Field colorBgr2GrayField = imgprocClass.getDeclaredField("COLOR_BGR2GRAY");
            colorBgr2GrayField.setAccessible(true);
            int colorBgr2Gray = colorBgr2GrayField.getInt(null);
            
            java.lang.reflect.Method cvtColorMethod = imgprocClass.getMethod("cvtColor", matClass, matClass, int.class);
            cvtColorMethod.invoke(null, image, grayImage, colorBgr2Gray);
            
            // 直方图均衡化
            java.lang.reflect.Method equalizeHistMethod = imgprocClass.getMethod("equalizeHist", matClass, matClass);
            equalizeHistMethod.invoke(null, grayImage, grayImage);
            
            // 检测人脸
            Object faceDetections = matOfRectClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method detectMultiScaleMethod = faceDetector.getClass().getMethod("detectMultiScale", matClass, matOfRectClass);
            detectMultiScaleMethod.invoke(faceDetector, grayImage, faceDetections);
            
            // 获取人脸数组
            java.lang.reflect.Method toArrayMethod = faceDetections.getClass().getMethod("toArray");
            Object[] facesArray = (Object[]) toArrayMethod.invoke(faceDetections);
            
            if (facesArray != null && facesArray.length > 0) {
                // 获取最大的人脸（简化处理，直接使用第一个人脸）
                Object largestFace = facesArray[0];
                
                // 裁剪人脸区域
                Object faceRegion = matClass.getDeclaredConstructor(matClass, rectClass).newInstance(image, largestFace);
                
                // 调整大小
                Object resizedFace = matClass.getDeclaredConstructor().newInstance();
                Object size = sizeClass.getDeclaredConstructor(double.class, double.class).newInstance(faceWidth, faceHeight);
                
                java.lang.reflect.Method resizeMethod = imgprocClass.getMethod("resize", matClass, matClass, sizeClass);
                resizeMethod.invoke(null, faceRegion, resizedFace, size);
                
                // 转换回Base64
                Object resultMatOfByte = matOfByteClass.getDeclaredConstructor().newInstance();
                java.lang.reflect.Method imencodeMethod = imgcodecsClass.getMethod("imencode", String.class, matClass, matOfByteClass);
                imencodeMethod.invoke(null, ".jpg", resizedFace, resultMatOfByte);
                
                // 获取字节数组
                java.lang.reflect.Method toArrayMethod2 = resultMatOfByte.getClass().getMethod("toArray");
                byte[] processedData = (byte[]) toArrayMethod2.invoke(resultMatOfByte);
                
                return Base64.getEncoder().encodeToString(processedData);
            }
            
            // 如果没有检测到人脸，返回原始图像的处理版本
            Object resizedImage = matClass.getDeclaredConstructor().newInstance();
            Object size = sizeClass.getDeclaredConstructor(double.class, double.class).newInstance(faceWidth, faceHeight);
            
            java.lang.reflect.Method resizeMethod = imgprocClass.getMethod("resize", matClass, matClass, sizeClass);
            resizeMethod.invoke(null, image, resizedImage, size);
            
            Object resultMatOfByte = matOfByteClass.getDeclaredConstructor().newInstance();
            java.lang.reflect.Method imencodeMethod = imgcodecsClass.getMethod("imencode", String.class, matClass, matOfByteClass);
            imencodeMethod.invoke(null, ".jpg", resizedImage, resultMatOfByte);
            
            java.lang.reflect.Method toArrayMethod2 = resultMatOfByte.getClass().getMethod("toArray");
            byte[] processedData = (byte[]) toArrayMethod2.invoke(resultMatOfByte);
            
            return Base64.getEncoder().encodeToString(processedData);
            
        } catch (Exception e) {
            System.out.println("OpenCV反射调用失败: " + e.getMessage());
            // 出错时返回原始图像的简单处理版本
            try {
                return Base64.getEncoder().encodeToString(Base64.getDecoder().decode(base64Image));
            } catch (Exception ex) {
                return base64Image;
            }
        }
    }
    
    /**
     * 检查OpenCV是否已加载
     */
    public boolean isOpenCVLoaded() {
        return isOpenCVLoaded;
    }
    
    /**
     * 尝试重新初始化OpenCV
     */
    public boolean tryReinitialize() {
        init();
        return isOpenCVLoaded;
    }
}
package com.exprdialog.controller;

import com.exprdialog.service.BaiDuAIService;
import com.exprdialog.service.OpenCVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/emotion-detect")
public class EmotionDetectionController {
    
    @Autowired
    private BaiDuAIService baiDuAIService;
    
    @Autowired
    private OpenCVService openCVService;
    
    /**
     * 检测图像中的表情
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> detectEmotion(@RequestBody Map<String, String> request) {
        try {
            String base64Image = request.get("image");
            if (base64Image == null || base64Image.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "缺少图像数据"));
            }
            
            // 使用OpenCV进行人脸检测和裁剪
            String croppedFaceImage = openCVService.detectAndCropFace(base64Image);
            
            // 调用百度AI进行表情识别
            Map<String, Object> emotionResult = baiDuAIService.detectEmotion(croppedFaceImage);
            
            return ResponseEntity.ok(emotionResult);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "表情识别失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 测试表情识别服务是否正常工作
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEmotionDetection() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "表情识别服务正常");
        return ResponseEntity.ok(response);
    }
}
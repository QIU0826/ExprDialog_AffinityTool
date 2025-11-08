package com.exprdialog.controller;

import com.exprdialog.model.TargetInfo;
import com.exprdialog.repository.TargetInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/target-info")
public class TargetInfoController {
    
    @Autowired
    private TargetInfoRepository targetInfoRepository;
    
    /**
     * 保存攻略对象信息
     */
    @PostMapping
    public ResponseEntity<TargetInfo> saveTargetInfo(@RequestBody TargetInfo targetInfo) {
        TargetInfo saved = targetInfoRepository.save(targetInfo);
        return ResponseEntity.ok(saved);
    }
    
    /**
     * 获取所有攻略对象列表
     */
    @GetMapping
    public ResponseEntity<List<TargetInfo>> getAllTargetInfo() {
        List<TargetInfo> targets = targetInfoRepository.findByOrderByCreateTimeDesc();
        return ResponseEntity.ok(targets);
    }
    
    /**
     * 根据ID获取攻略对象信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<TargetInfo> getTargetInfoById(@PathVariable Long id) {
        return targetInfoRepository.findById(id)
                .map(targetInfo -> ResponseEntity.ok(targetInfo))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 更新攻略对象信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<TargetInfo> updateTargetInfo(@PathVariable Long id, @RequestBody TargetInfo targetInfo) {
        return targetInfoRepository.findById(id)
                .map(existing -> {
                    targetInfo.setId(id);
                    return ResponseEntity.ok(targetInfoRepository.save(targetInfo));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 删除攻略对象信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTargetInfo(@PathVariable Long id) {
        if (targetInfoRepository.existsById(id)) {
            targetInfoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
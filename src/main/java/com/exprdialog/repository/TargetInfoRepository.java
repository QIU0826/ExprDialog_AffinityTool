package com.exprdialog.repository;

import com.exprdialog.model.TargetInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TargetInfoRepository extends JpaRepository<TargetInfo, Long> {
    List<TargetInfo> findByOrderByCreateTimeDesc();
    TargetInfo findByNickname(String nickname);
}
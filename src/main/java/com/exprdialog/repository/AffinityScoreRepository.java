package com.exprdialog.repository;

import com.exprdialog.model.AffinityScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AffinityScoreRepository extends JpaRepository<AffinityScore, Long> {
    List<AffinityScore> findByTargetIdOrderByTimestampDesc(Long targetId);
    AffinityScore findTopByTargetIdOrderByTimestampDesc(Long targetId);
}
package com.exprdialog.repository;

import com.exprdialog.model.ConversationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationHistoryRepository extends JpaRepository<ConversationHistory, Long> {
    List<ConversationHistory> findByTargetIdOrderByTimestampDesc(Long targetId);
    List<ConversationHistory> findTop10ByTargetIdOrderByTimestampDesc(Long targetId);
}
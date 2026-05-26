package com.gerenciamento.pedidos.domain.repository;

import com.gerenciamento.pedidos.domain.entity.OperationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OperationHistoryRepository extends JpaRepository<OperationHistory, Long> {
    List<OperationHistory> findByEntityAndEntityId(String entity, Long entityId);
    List<OperationHistory> findByUserId(Long userId);
}
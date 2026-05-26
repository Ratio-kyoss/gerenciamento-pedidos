package com.gerenciamento.pedidos.application.usecase;

import com.gerenciamento.pedidos.domain.entity.OperationHistory;
import com.gerenciamento.pedidos.domain.repository.OperationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationHistoryUseCase {

    private final OperationHistoryRepository historyRepository;

    public List<OperationHistory> listAll() {
        return historyRepository.findAll();
    }

    public List<OperationHistory> listByEntity(String entity, Long entityId) {
        return historyRepository.findByEntityAndEntityId(entity, entityId);
    }

    public List<OperationHistory> listByUser(Long userId) {
        return historyRepository.findByUserId(userId);
    }
}
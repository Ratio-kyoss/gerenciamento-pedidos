package com.gerenciamento.pedidos.presentation.controller;

import com.gerenciamento.pedidos.application.usecase.OperationHistoryUseCase;
import com.gerenciamento.pedidos.domain.entity.OperationHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OperationHistoryController {

    private final OperationHistoryUseCase historyUseCase;

    @GetMapping
    public ResponseEntity<List<OperationHistory>> listAll() {
        return ResponseEntity.ok(historyUseCase.listAll());
    }

    @GetMapping("/{entity}/{id}")
    public ResponseEntity<List<OperationHistory>> listByEntity(
            @PathVariable String entity,
            @PathVariable Long id) {
        return ResponseEntity.ok(historyUseCase.listByEntity(entity, id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OperationHistory>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(historyUseCase.listByUser(userId));
    }
}
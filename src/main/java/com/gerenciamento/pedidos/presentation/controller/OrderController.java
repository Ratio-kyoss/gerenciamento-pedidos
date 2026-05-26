package com.gerenciamento.pedidos.presentation.controller;

import com.gerenciamento.pedidos.application.usecase.OrderUseCase;
import com.gerenciamento.pedidos.domain.entity.Order;
import com.gerenciamento.pedidos.presentation.dto.OrderRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OrderRequestDTO dto,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Order order = orderUseCase.createOrder(dto, userDetails.getUsername());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> listAll() {
        return ResponseEntity.ok(orderUseCase.listOrders());
    }

    @GetMapping("/my")
    public ResponseEntity<List<Order>> listMine(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderUseCase.listOrdersByUser(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderUseCase.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam String status,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Apenas ADMIN pode alterar status
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                return ResponseEntity.status(403).body("Apenas administradores podem alterar status.");
            }
            return ResponseEntity.ok(orderUseCase.updateStatus(id, status, userDetails.getUsername()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            // Verifica se é o dono do pedido ou admin
            Order order = orderUseCase.findById(id);
            if (!isAdmin && !order.getUser().getUsername().equals(userDetails.getUsername())) {
                return ResponseEntity.status(403).body("Você não tem permissão para cancelar este pedido.");
            }

            orderUseCase.cancelOrder(id, userDetails.getUsername());
            return ResponseEntity.ok("Pedido cancelado com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
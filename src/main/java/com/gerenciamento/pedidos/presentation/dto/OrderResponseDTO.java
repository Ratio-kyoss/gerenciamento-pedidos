package com.gerenciamento.pedidos.presentation.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private String username;
    private List<OrderItemResponseDTO> items;
    private String status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
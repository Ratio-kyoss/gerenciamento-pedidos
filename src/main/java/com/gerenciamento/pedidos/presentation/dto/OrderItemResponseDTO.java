package com.gerenciamento.pedidos.presentation.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
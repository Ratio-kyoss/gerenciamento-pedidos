package com.gerenciamento.pedidos.presentation.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDTO {
    private List<OrderItemRequestDTO> items;
}
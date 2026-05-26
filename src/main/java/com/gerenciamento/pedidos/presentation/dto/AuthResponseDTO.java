package com.gerenciamento.pedidos.presentation.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String username;
    private String role;
}
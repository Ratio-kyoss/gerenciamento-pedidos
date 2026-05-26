package com.gerenciamento.pedidos.presentation.dto;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String username;
    private String password;
    private String email;
    private String role;
}
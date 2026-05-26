package com.gerenciamento.pedidos.presentation.controller;

import com.gerenciamento.pedidos.application.usecase.UserUseCase;
import com.gerenciamento.pedidos.infrastructure.security.JwtUtil;
import com.gerenciamento.pedidos.presentation.dto.AuthRequestDTO;
import com.gerenciamento.pedidos.presentation.dto.AuthResponseDTO;
import com.gerenciamento.pedidos.presentation.dto.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserUseCase userUseCase;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequestDTO dto) {
        try {
            var user = userUseCase.createUser(dto);
            return ResponseEntity.ok("Usuário criado com sucesso: " + user.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO dto) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getUsername(), dto.getPassword()));

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next()
                    .getAuthority().replace("ROLE_", "");
            String token = jwtUtil.generateToken(userDetails.getUsername(), role);

            return ResponseEntity.ok(new AuthResponseDTO(token, userDetails.getUsername(), role));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Credenciais inválidas");
        }
    }
}
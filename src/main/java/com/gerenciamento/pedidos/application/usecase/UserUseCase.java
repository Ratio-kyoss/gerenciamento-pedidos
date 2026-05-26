package com.gerenciamento.pedidos.application.usecase;

import com.gerenciamento.pedidos.domain.entity.User;
import com.gerenciamento.pedidos.domain.repository.UserRepository;
import com.gerenciamento.pedidos.presentation.dto.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username já existe: " + dto.getUsername());
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado: " + dto.getEmail());
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setRole(User.Role.valueOf(dto.getRole() != null ? dto.getRole() : "USER"));

        return userRepository.save(user);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }
}
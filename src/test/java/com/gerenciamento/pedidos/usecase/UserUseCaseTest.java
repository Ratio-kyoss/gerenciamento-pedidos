package com.gerenciamento.pedidos.usecase;

import com.gerenciamento.pedidos.application.usecase.UserUseCase;
import com.gerenciamento.pedidos.domain.entity.User;
import com.gerenciamento.pedidos.domain.repository.UserRepository;
import com.gerenciamento.pedidos.presentation.dto.UserRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserUseCase userUseCase;

    private UserRequestDTO dto;
    private User user;

    @BeforeEach
    void setUp() {
        dto = new UserRequestDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@email.com");
        dto.setPassword("123456");
        dto.setRole("USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@email.com");
        user.setPassword("encoded_password");
        user.setRole(User.Role.USER);
    }

    // Teste 1 — Criar usuário com sucesso
    @Test
    void deveCriarUsuarioComSucesso() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userUseCase.createUser(dto);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Teste 2 — Erro ao criar usuário com username duplicado
    @Test
    void deveLancarExcecaoQuandoUsernameJaExiste() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userUseCase.createUser(dto));

        assertTrue(exception.getMessage().contains("Username já existe"));
        verify(userRepository, never()).save(any());
    }

    // Teste 3 — Erro ao criar usuário com email duplicado
    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userUseCase.createUser(dto));

        assertTrue(exception.getMessage().contains("Email já cadastrado"));
        verify(userRepository, never()).save(any());
    }

    // Teste 4 — Buscar usuário por ID com sucesso
    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userUseCase.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
    }

    // Teste 5 — Erro ao buscar usuário com ID inexistente
    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userUseCase.findById(99L));

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
    }

    // Teste 6 — Listar todos os usuários
    @Test
    void deveListarTodosOsUsuarios() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userUseCase.listUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }
}
package com.gerenciamento.pedidos.usecase;

import com.gerenciamento.pedidos.application.usecase.OrderUseCase;
import com.gerenciamento.pedidos.domain.entity.*;
import com.gerenciamento.pedidos.domain.repository.*;
import com.gerenciamento.pedidos.presentation.dto.OrderItemRequestDTO;
import com.gerenciamento.pedidos.presentation.dto.OrderRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OperationHistoryRepository historyRepository;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private User user;
    private Product product;
    private Order order;
    private OrderRequestDTO dto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(User.Role.USER);

        product = new Product();
        product.setId(1L);
        product.setName("Produto Teste");
        product.setPrice(new BigDecimal("50.00"));
        product.setStock(10);

        OrderItemRequestDTO itemDTO = new OrderItemRequestDTO();
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(2);

        dto = new OrderRequestDTO();
        dto.setItems(List.of(itemDTO));

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("50.00"));

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setTotalPrice(new BigDecimal("100.00"));
        order.setItems(new ArrayList<>(List.of(item)));
        item.setOrder(order);
    }

    // Teste 12 — Criar pedido com sucesso
    @Test
    void deveCriarPedidoComSucesso() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(historyRepository.save(any(OperationHistory.class))).thenReturn(new OperationHistory());

        Order result = orderUseCase.createOrder(dto, "testuser");

        assertNotNull(result);
        assertEquals(Order.Status.PENDING, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // Teste 13 — Erro ao criar pedido com estoque insuficiente
    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficienteNoPedido() {
        product.setStock(1);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderUseCase.createOrder(dto, "testuser"));

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
        verify(orderRepository, never()).save(any());
    }

    // Teste 14 — Cancelar pedido com sucesso
    @Test
    void deveCancelarPedidoComSucesso() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(historyRepository.save(any(OperationHistory.class))).thenReturn(new OperationHistory());

        assertDoesNotThrow(() -> orderUseCase.cancelOrder(1L, "testuser"));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // Teste 15 — Erro ao cancelar pedido já cancelado
    @Test
    void deveLancarExcecaoAoCancelarPedidoJaCancelado() {
        order.setStatus(Order.Status.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderUseCase.cancelOrder(1L, "testuser"));

        assertTrue(exception.getMessage().contains("já está cancelado"));
        verify(orderRepository, never()).save(any());
    }

    // Teste 16 — Erro ao cancelar pedido já entregue
    @Test
    void deveLancarExcecaoAoCancelarPedidoJaEntregue() {
        order.setStatus(Order.Status.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderUseCase.cancelOrder(1L, "testuser"));

        assertTrue(exception.getMessage().contains("já entregue"));
        verify(orderRepository, never()).save(any());
    }
}
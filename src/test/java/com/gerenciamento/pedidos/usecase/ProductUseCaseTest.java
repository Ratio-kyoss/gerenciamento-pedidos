package com.gerenciamento.pedidos.usecase;

import com.gerenciamento.pedidos.application.usecase.ProductUseCase;
import com.gerenciamento.pedidos.domain.entity.Product;
import com.gerenciamento.pedidos.domain.repository.ProductRepository;
import com.gerenciamento.pedidos.presentation.dto.ProductRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductUseCase productUseCase;

    private Product product;
    private ProductRequestDTO dto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Produto Teste");
        product.setDescription("Descrição teste");
        product.setPrice(new BigDecimal("99.90"));
        product.setStock(10);

        dto = new ProductRequestDTO();
        dto.setName("Produto Teste");
        dto.setDescription("Descrição teste");
        dto.setPrice(new BigDecimal("99.90"));
        dto.setStock(10);
    }

    // Teste 7 — Criar produto com sucesso
    @Test
    void deveCriarProdutoComSucesso() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productUseCase.createProduct(dto);

        assertNotNull(result);
        assertEquals("Produto Teste", result.getName());
        assertEquals(new BigDecimal("99.90"), result.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // Teste 8 — Atualizar estoque com sucesso
    @Test
    void deveAtualizarEstoqueComSucesso() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productUseCase.updateStock(1L, -3);

        assertEquals(7, result.getStock());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // Teste 9 — Erro ao atualizar estoque insuficiente
    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productUseCase.updateStock(1L, -20));

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
        verify(productRepository, never()).save(any());
    }

    // Teste 10 — Deletar produto com sucesso
    @Test
    void deveDeletarProdutoComSucesso() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        assertDoesNotThrow(() -> productUseCase.deleteProduct(1L));
        verify(productRepository, times(1)).delete(product);
    }

    // Teste 11 — Erro ao buscar produto inexistente
    @Test
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productUseCase.findById(99L));

        assertTrue(exception.getMessage().contains("Produto não encontrado"));
    }
}
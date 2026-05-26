package com.gerenciamento.pedidos.application.usecase;

import com.gerenciamento.pedidos.domain.entity.Product;
import com.gerenciamento.pedidos.domain.repository.ProductRepository;
import com.gerenciamento.pedidos.presentation.dto.ProductRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductRepository productRepository;

    public Product createProduct(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return productRepository.save(product);
    }

    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
    }

    public Product updateProduct(Long id, ProductRequestDTO dto) {
        Product product = findById(id);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }

    public Product updateStock(Long id, Integer quantity) {
        Product product = findById(id);
        if (product.getStock() + quantity < 0) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + product.getName());
        }
        product.setStock(product.getStock() + quantity);
        return productRepository.save(product);
    }
}
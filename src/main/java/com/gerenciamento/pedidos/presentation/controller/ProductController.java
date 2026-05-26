package com.gerenciamento.pedidos.presentation.controller;

import com.gerenciamento.pedidos.application.usecase.ProductUseCase;
import com.gerenciamento.pedidos.domain.entity.Product;
import com.gerenciamento.pedidos.presentation.dto.ProductRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductUseCase productUseCase;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductRequestDTO dto) {
        try {
            return ResponseEntity.ok(productUseCase.createProduct(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> listAll() {
        return ResponseEntity.ok(productUseCase.listProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productUseCase.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody ProductRequestDTO dto) {
        try {
            return ResponseEntity.ok(productUseCase.updateProduct(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            productUseCase.deleteProduct(id);
            return ResponseEntity.ok("Produto deletado com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable Long id,
                                         @RequestParam Integer quantity) {
        try {
            return ResponseEntity.ok(productUseCase.updateStock(id, quantity));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
package com.gerenciamento.pedidos.application.usecase;

import com.gerenciamento.pedidos.domain.entity.*;
import com.gerenciamento.pedidos.domain.repository.*;
import com.gerenciamento.pedidos.presentation.dto.OrderRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OperationHistoryRepository historyRepository;

    @Transactional
    public Order createOrder(OrderRequestDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (var itemDTO : dto.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemDTO.getProductId()));

            if (product.getStock() < itemDTO.getQuantity()) {
                throw new RuntimeException("Estoque insuficiente: " + product.getName());
            }

            product.setStock(product.getStock() - itemDTO.getQuantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(product.getPrice());

            items.add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        }

        order.setItems(items);
        order.setTotalPrice(total);

        Order saved = orderRepository.save(order);

        saveHistory("CREATE", "Order", saved.getId(), "Pedido criado por " + username, user);

        return saved;
    }

    public List<Order> listOrders() {
        return orderRepository.findAll();
    }

    public List<Order> listOrdersByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
    }

    @Transactional
    public Order updateStatus(Long id, String status, String username) {
        Order order = findById(id);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));

        order.setStatus(Order.Status.valueOf(status));
        Order updated = orderRepository.save(order);

        saveHistory("UPDATE", "Order", id, "Status atualizado para " + status + " por " + username, user);

        return updated;
    }

    @Transactional
    public void cancelOrder(Long id, String username) {
        Order order = findById(id);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));

        if (order.getStatus() == Order.Status.CANCELLED) {
            throw new RuntimeException("Pedido já está cancelado.");
        }

        if (order.getStatus() == Order.Status.DELIVERED) {
            throw new RuntimeException("Pedido já entregue não pode ser cancelado.");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);

        saveHistory("CANCEL", "Order", id, "Pedido cancelado por " + username, user);
    }

    private void saveHistory(String operation, String entity, Long entityId, String description, User user) {
        OperationHistory history = new OperationHistory();
        history.setOperation(operation);
        history.setEntity(entity);
        history.setEntityId(entityId);
        history.setDescription(description);
        history.setUser(user);
        historyRepository.save(history);
    }
}
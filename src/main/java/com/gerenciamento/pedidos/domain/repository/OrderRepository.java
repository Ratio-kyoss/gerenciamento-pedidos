package com.gerenciamento.pedidos.domain.repository;

import com.gerenciamento.pedidos.domain.entity.Order;
import com.gerenciamento.pedidos.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByStatus(Order.Status status);
    List<Order> findByUserOrderByCreatedAtDesc(User user);
}
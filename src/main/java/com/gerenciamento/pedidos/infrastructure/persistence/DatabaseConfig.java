package com.gerenciamento.pedidos.infrastructure.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.gerenciamento.pedidos.domain.repository"
)
public class DatabaseConfig {
    // O Spring Data JPA implementa automaticamente as interfaces de repositório do domínio
}
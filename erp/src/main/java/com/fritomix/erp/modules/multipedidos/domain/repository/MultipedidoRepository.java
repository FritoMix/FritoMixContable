package com.fritomix.erp.modules.multipedidos.domain.repository;

import com.fritomix.erp.modules.multipedidos.domain.entity.Multipedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MultipedidoRepository extends JpaRepository<Multipedido, Long> {
    Optional<Multipedido> findByNumero(String numero);
    List<Multipedido> findAllByOrderByCreatedAtDesc();
}

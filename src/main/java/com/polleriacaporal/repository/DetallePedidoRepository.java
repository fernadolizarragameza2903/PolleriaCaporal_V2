package com.polleriacaporal.repository;

import com.polleriacaporal.model.DetallePedido;
import com.polleriacaporal.model.Pedido;
import com.polleriacaporal.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad DetallePedido
 * Proporciona métodos CRUD para los detalles de pedidos
 */
@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    List<DetallePedido> findByPedido(Pedido pedido);
    List<DetallePedido> findByProducto(Producto producto);
}

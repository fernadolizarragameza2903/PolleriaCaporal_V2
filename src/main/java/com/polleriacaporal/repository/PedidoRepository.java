package com.polleriacaporal.repository;

import com.polleriacaporal.model.EstadoVenta;
import com.polleriacaporal.model.Pedido;
import com.polleriacaporal.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pedido
 * Proporciona métodos CRUD y consultas personalizadas
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuario(Usuario usuario);
    List<Pedido> findByEstado(EstadoVenta estado);
    List<Pedido> findByFechaPedidoBetween(LocalDateTime inicio, LocalDateTime fin);
    Page<Pedido> findByUsuario(Usuario usuario, Pageable pageable);
    Page<Pedido> findByEstado(EstadoVenta estado, Pageable pageable);

    // Búsqueda por id de usuario para facilitar filtrado sin cargar la entidad Usuario
    List<Pedido> findByUsuarioId(Long usuarioId);
    Page<Pedido> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("select distinct p from Pedido p " +
           "left join fetch p.usuario " +
           "left join fetch p.detalles d " +
           "left join fetch d.producto")
    List<Pedido> findAllWithUsuarioAndDetallesProducto();

    @Query("select p from Pedido p " +
           "left join fetch p.usuario " +
           "left join fetch p.detalles d " +
           "left join fetch d.producto " +
           "where p.id = :id")
    Optional<Pedido> findByIdWithUsuarioAndDetallesProducto(Long id);

    @Query("select d.producto.nombre, sum(d.cantidad) " +
           "from DetallePedido d " +
           "group by d.producto.nombre " +
           "order by sum(d.cantidad) desc")
    List<Object[]> findTopProductosVendidos();
}

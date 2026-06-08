package com.polleriacaporal.service;

import com.polleriacaporal.model.Pedido;
import com.polleriacaporal.model.Usuario;
import com.polleriacaporal.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.polleriacaporal.model.DetallePedido;

/**
 * Servicio para la gestión de Pedidos
 * Contiene la lógica de negocio para crear, actualizar, eliminar y consultar pedidos
 */
@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Obtiene todos los pedidos del sistema
     */
    @Transactional(readOnly = true)
    public List<Pedido> obtenerTodos() {
        List<Pedido> pedidos = pedidoRepository.findAllWithUsuarioAndDetallesProducto();
        pedidos.forEach(pedido -> {
            if (pedido.getUsuario() != null) {
                pedido.getUsuario().getUsername();
            }
            pedido.getDetalles().forEach(detalle -> {
                if (detalle.getProducto() != null) {
                    detalle.getProducto().getNombre();
                }
            });
        });
        return pedidos;
    }

    @Transactional(readOnly = true)
    public List<java.util.Map.Entry<String, Integer>> obtenerTopProductosVendidos() {
        return pedidoRepository.findTopProductosVendidos().stream()
            .map(row -> java.util.Map.entry((String) row[0], ((Number) row[1]).intValue()))
            .toList();
    }

    /**
     * Obtiene un pedido por su ID
     */
    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<Pedido> pedido = pedidoRepository.findByIdWithUsuarioAndDetallesProducto(id);
        pedido.ifPresent(p -> {
            if (p.getUsuario() != null) {
                p.getUsuario().getUsername();
            }
            p.getDetalles().forEach(detalle -> {
                if (detalle.getProducto() != null) {
                    detalle.getProducto().getNombre();
                }
            });
        });
        return pedido;
    }

    /**
     * Obtiene pedidos por usuario
     */
    @Transactional(readOnly = true)
    public List<Pedido> obtenerPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario);
    }

    /**
     * Guarda un pedido
     */
    public Pedido guardar(Pedido pedido) {
        // Asegurar que cada detalle tenga precio unitario y subtotal actualizado
        if (pedido.getDetalles() != null) {
            pedido.getDetalles().forEach(d -> {
                if (d.getProducto() != null && d.getProducto().getPrecio() != null) {
                    // Persist the unit price as stored in Producto (treated as IGV-included final price)
                    d.setPrecioUnitario(d.getProducto().getPrecio());
                }
                d.actualizarSubtotal();
            });
        }

        // Calcular totales (subtotal, igv, total) antes de persistir
        pedido.calcularTotal();

        return pedidoRepository.save(pedido);
    }

    /**
     * Actualiza el estado de un pedido y lo persiste
     */
    public Pedido actualizarEstado(Long pedidoId, com.polleriacaporal.model.EstadoVenta nuevoEstado) {
        var opt = pedidoRepository.findById(pedidoId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Pedido no encontrado: " + pedidoId);
        }
        Pedido pedido = opt.get();
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    /**
     * Elimina un pedido por ID
     */
    public void eliminarPorId(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Map.Entry<String, Integer>> obtenerTopProductos(int limite) {
        return pedidoRepository.findAll().stream()
            .flatMap(p -> p.getDetalles().stream())
            .collect(java.util.stream.Collectors.groupingBy(
                d -> d.getProducto().getNombre(),
                java.util.stream.Collectors.summingInt(DetallePedido::getCantidad)
            ))
            .entrySet().stream()
            .sorted(java.util.Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limite)
            .toList();
    }
}
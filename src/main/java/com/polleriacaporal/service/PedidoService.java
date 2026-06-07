package com.polleriacaporal.service;

import com.polleriacaporal.model.Pedido;
import com.polleriacaporal.model.Usuario;
import com.polleriacaporal.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        return pedidoRepository.findAll();
    }

    /**
     * Obtiene un pedido por su ID
     */
    @Transactional(readOnly = true)
    public Optional<Pedido> obtenerPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return pedidoRepository.findById(id);
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
}
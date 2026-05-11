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
        return pedidoRepository.save(pedido);
    }

    /**
     * Elimina un pedido por ID
     */
    public void eliminarPorId(Long id) {
        pedidoRepository.deleteById(id);
    }
}
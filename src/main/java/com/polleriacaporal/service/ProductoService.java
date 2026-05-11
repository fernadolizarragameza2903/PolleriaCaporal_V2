package com.polleriacaporal.service;

import com.polleriacaporal.model.Producto;
import com.polleriacaporal.repository.ProductoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @PostConstruct
    public void cargarDatosIniciales() {
        if (productoRepository.count() > 0) {
            return;
        }

        save(productoEjemplo("Pollo a la brasa", "Pollo", new BigDecimal("35.00"), 40, true));
        save(productoEjemplo("Papas fritas", "Papas", new BigDecimal("12.00"), 80, true));
        save(productoEjemplo("Gaseosa 500ml", "Bebidas", new BigDecimal("8.00"), 24, false));
    }

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return productoRepository.findById(id);
    }

    public Producto save(Producto producto) {
        if (producto.getEstado() == null) {
            producto.setEstado(Boolean.FALSE);
        }
        return productoRepository.save(producto);
    }

    public void deleteById(Long id) {
        Objects.requireNonNull(id, "id");
        productoRepository.deleteById(id);
    }

    private static Producto productoEjemplo(String nombre, String categoria, BigDecimal precio, int stock, boolean estado) {
        Producto p = new Producto(nombre, categoria, precio, stock);
        p.setEstado(estado);
        return p;
    }
}
package com.polleriacaporal.service;

import com.polleriacaporal.model.Producto;
import com.polleriacaporal.repository.ProductoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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

        save(new Producto(null, "Pollo a la brasa", "Pollo", new BigDecimal("35.00"), true));
        save(new Producto(null, "Papas fritas", "Papas", new BigDecimal("12.00"), true));
        save(new Producto(null, "Gaseosa 500ml", "Bebidas", new BigDecimal("8.00"), false));
    }

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto save(Producto producto) {
        if (producto.getEstado() == null) {
            producto.setEstado(Boolean.FALSE);
        }
        return productoRepository.save(producto);
    }

    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }
}

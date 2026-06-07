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

        save(productoEjemplo("1/4 de Pollo a la brasa con papas y ensalada", "Pollo a la brasa", new BigDecimal("20.00"), 50, true));
        save(productoEjemplo("1/2 Pollo a la brasa con papas y ensalada", "Pollo a la brasa", new BigDecimal("38.00"), 40, true));
        save(productoEjemplo("1 Pollo entero a la brasa con papas y ensalada", "Pollo a la brasa", new BigDecimal("70.00"), 20, true));

        save(productoEjemplo("Inka Kola 1.5 Litros", "Bebidas", new BigDecimal("12.00"), 60, true));
        save(productoEjemplo("Coca Cola 1.5 Litros", "Bebidas", new BigDecimal("12.00"), 60, true));
        save(productoEjemplo("Inka Kola 3 Litros", "Bebidas", new BigDecimal("20.00"), 40, true));

        save(productoEjemplo("Combo Mostrito (1/4 pollo + chaufa + papas)", "Promociones", new BigDecimal("24.00"), 30, true));

        save(productoEjemplo("Porción de papas fritas extra", "Extras", new BigDecimal("10.00"), 100, true));
        save(productoEjemplo("Porción de Arroz Chaufa extra", "Extras", new BigDecimal("12.00"), 80, true));

        save(productoEjemplo("Anticuchos (3 palitos con papa)", "Parrillas", new BigDecimal("18.00"), 50, true));
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
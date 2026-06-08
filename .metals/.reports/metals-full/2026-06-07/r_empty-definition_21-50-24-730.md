error id: file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/model/Producto.java:jakarta/validation/constraints/PositiveOrZero#
file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/model/Producto.java
empty definition using pc, found symbol in pc: jakarta/validation/constraints/PositiveOrZero#
semanticdb not found
empty definition using fallback
non-local guesses:

offset: 253
uri: file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/model/Producto.java
text:
```scala
package com.polleriacaporal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.@@PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Producto del sistema
 * Incluye información de nombre, precio, stock y categoría
 */
@Entity
@Table(name = "productos",
       indexes = {
           @Index(name = "idx_productos_categoria", columnList = "categoria"),
           @Index(name = "idx_productos_estado", columnList = "estado")
       }
)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es requerido")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    @Column(name = "categoria", length = 50)
    private String categoria;

    @NotNull(message = "El precio es requerido")
    @Positive(message = "El precio debe ser mayor a 0")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "El stock es requerido")
    @PositiveOrZero(message = "El stock debe ser mayor o igual a 0")
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;

    @Column(name = "estado", nullable = false)
    private Boolean estado = Boolean.TRUE;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "producto",
               cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               orphanRemoval = false,
               fetch = FetchType.LAZY)
    private List<DetallePedido> detallesPedidos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Constructores
    public Producto() {
        this.estado = Boolean.TRUE;
        this.stock = 0;
    }

    public Producto(String nombre, String categoria, BigDecimal precio, Integer stock) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.estado = Boolean.TRUE;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public List<DetallePedido> getDetallesPedidos() {
        return detallesPedidos;
    }

    public void setDetallesPedidos(List<DetallePedido> detallesPedidos) {
        this.detallesPedidos = detallesPedidos;
    }

    @Transient
    public java.math.BigDecimal getPrecioConIgv() {
        if (precio == null) {
            return java.math.BigDecimal.ZERO;
        }
        return precio.multiply(new java.math.BigDecimal("1.18")).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: jakarta/validation/constraints/PositiveOrZero#
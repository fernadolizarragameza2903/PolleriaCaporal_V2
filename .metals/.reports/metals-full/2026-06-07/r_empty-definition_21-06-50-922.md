error id: file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/model/DetallePedido.java:_empty_/ManyToOne#fetch#
file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/model/DetallePedido.java
empty definition using pc, found symbol in pc: _empty_/ManyToOne#fetch#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 909
uri: file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/model/DetallePedido.java
text:
```scala
package com.polleriacaporal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Entidad que representa un detalle de un pedido
 * Tabla intermedia entre Pedido y Producto
 * Contiene: cantidad, producto y subtotal
 */
@Entity
@Table(name = "detalles_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive(message = "La cantidad debe ser mayor a 0")
    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    // Relación ManyToOne: Varios detalles pertenecen a un pedido
    @ManyToOne(@@fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // Relación ManyToOne: Varios detalles pueden referir al mismo producto
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @PrePersist
    protected void onCreate() {
        if (subtotal == null && precioUnitario != null && cantidad != null) {
            subtotal = precioUnitario.multiply(new BigDecimal(cantidad)).setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }

    // Constructores
    public DetallePedido() {
    }

    public DetallePedido(Pedido pedido, Producto producto, Integer cantidad) {
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        this.subtotal = this.precioUnitario.multiply(new BigDecimal(cantidad)).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    // Métodos de negocio
    public void actualizarSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad)).setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        actualizarSubtotal();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        actualizarSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        if (producto != null) {
            this.precioUnitario = producto.getPrecio();
            actualizarSubtotal();
        }
    }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/ManyToOne#fetch#
error id: file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/model/Pedido.java:jakarta/validation/constraints/PositiveOrZero#
file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/model/Pedido.java
empty definition using pc, found symbol in pc: jakarta/validation/constraints/PositiveOrZero#
semanticdb not found
empty definition using fallback
non-local guesses:

offset: 107
uri: file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/model/Pedido.java
text:
```scala
package com.polleriacaporal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.@@PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Pedido realizado en el sistema
 * Contiene información del cliente, fecha, total y detalles del pedido
 */
@Entity
@Table(name = "pedidos",
       indexes = {
           @Index(name = "idx_pedidos_estado", columnList = "estado"),
           @Index(name = "idx_pedidos_fecha", columnList = "fecha_pedido"),
           @Index(name = "idx_pedidos_usuario", columnList = "usuario_id")
       }
)
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_pedido", nullable = false, updatable = false)
    private LocalDateTime fechaPedido;

    @Column(name = "cliente_nombre", length = 100)
    private String clienteNombre;

    @Column(name = "cliente_telefono", length = 20)
    private String clienteTelefono;

    @Column(name = "cliente_direccion", length = 200)
    private String clienteDireccion;

    @PositiveOrZero(message = "El subtotal no puede ser negativo")
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "igv", nullable = false, precision = 12, scale = 2)
    private BigDecimal igv = BigDecimal.ZERO;

    @PositiveOrZero(message = "El total no puede ser negativo")
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "nota", columnDefinition = "TEXT")
    private String nota;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoVenta estado = EstadoVenta.PENDIENTE;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_pedidos_usuario"))
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.EAGER)
    private List<DetallePedido> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaPedido = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (total == null) {
            total = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Constructores
    public Pedido() {
    }

    public Pedido(Usuario usuario, String clienteNombre, BigDecimal total) {
        this.usuario = usuario;
        this.clienteNombre = clienteNombre;
        this.total = total;
        this.estado = EstadoVenta.PENDIENTE;
    }

    // Métodos de negocio
    public void calcularTotal() {
        // Here detalles store subtotal values that already include IGV (precioUnitario is IGV-included).
        java.math.BigDecimal totalConIgv = detalles.stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        if (totalConIgv.compareTo(BigDecimal.ZERO) == 0) {
            this.subtotal = BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP);
            this.igv = BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP);
            this.total = BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP);
            return;
        }

        java.math.BigDecimal subtotalSinIgv = totalConIgv.divide(new BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP);
        java.math.BigDecimal igvCalc = totalConIgv.subtract(subtotalSinIgv).setScale(2, java.math.RoundingMode.HALF_UP);

        this.subtotal = subtotalSinIgv;
        this.igv = igvCalc;
        this.total = totalConIgv;
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }

    public void removerDetalle(DetallePedido detalle) {
        detalles.remove(detalle);
        detalle.setPedido(null);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
    }

    public String getClienteDireccion() {
        return clienteDireccion;
    }

    public void setClienteDireccion(String clienteDireccion) {
        this.clienteDireccion = clienteDireccion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", fechaPedido=" + fechaPedido +
                ", clienteNombre='" + clienteNombre + '\'' +
                ", total=" + total +
                ", estado=" + estado +
                '}';
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: jakarta/validation/constraints/PositiveOrZero#
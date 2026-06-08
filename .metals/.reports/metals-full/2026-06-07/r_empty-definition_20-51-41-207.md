error id: file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/controller/PedidoController.java:com/polleriacaporal/model/DetallePedido#
file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/controller/PedidoController.java
empty definition using pc, found symbol in pc: com/polleriacaporal/model/DetallePedido#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 76
uri: file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/controller/PedidoController.java
text:
```scala
package com.polleriacaporal.controller;

import com.polleriacaporal.model.@@DetallePedido;
import com.polleriacaporal.model.EstadoVenta;
import com.polleriacaporal.model.Pedido;
import com.polleriacaporal.model.Producto;
import com.polleriacaporal.model.Usuario;
import com.polleriacaporal.service.PedidoService;
import com.polleriacaporal.service.ProductoService;
import com.polleriacaporal.service.UsuarioService;
import com.polleriacaporal.repository.DetallePedidoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public PedidoController(PedidoService pedidoService, DetallePedidoRepository detallePedidoRepository, ProductoService productoService, UsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/actualizarEstado/{id}")
    public String actualizarEstado(@PathVariable Long id, @RequestParam String estado, RedirectAttributes ra) {
        try {
            EstadoVenta nuevo = EstadoVenta.valueOf(estado);
            pedidoService.actualizarEstado(id, nuevo);
            ra.addFlashAttribute("success", "Estado actualizado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo actualizar el estado");
        }
        return "redirect:/empleados/pedidos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable Long id, RedirectAttributes ra) {
        try {
            // Marcar como CANCELADO en lugar de eliminar físicamente
            pedidoService.actualizarEstado(id, EstadoVenta.CANCELADO);
            ra.addFlashAttribute("success", "Pedido cancelado");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo cancelar el pedido");
        }
        return "redirect:/empleados/pedidos";
    }

    // API REST para modales
    @Transactional(readOnly = true)
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerPedidoAPI(@PathVariable Long id, Authentication authentication) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(id).orElseThrow();
            if (!puedeAcceder(pedido, authentication)) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.ok(toDto(pedido));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizarPedidoAPI(@PathVariable Long id, @RequestBody Map<String, Object> body, Authentication authentication) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(id).orElseThrow();
            if (!puedeAcceder(pedido, authentication)) {
                return ResponseEntity.status(403).build();
            }
            
            if (body.containsKey("estado")) {
                pedido.setEstado(EstadoVenta.valueOf((String) body.get("estado")));
            }
            if (body.containsKey("clienteNombre")) {
                pedido.setClienteNombre(toNullableString(body.get("clienteNombre")));
            }
            if (body.containsKey("clienteTelefono")) {
                pedido.setClienteTelefono(toNullableString(body.get("clienteTelefono")));
            }
            if (body.containsKey("clienteDireccion")) {
                pedido.setClienteDireccion(toNullableString(body.get("clienteDireccion")));
            }
            if (body.containsKey("nota")) {
                pedido.setNota(toNullableString(body.get("nota")));
            }
            if (body.containsKey("productoId")) {
                Long productoId = toLong(body.get("productoId"));
                int cantidad = body.containsKey("cantidad") ? toInteger(body.get("cantidad")) : 1;
                if (cantidad <= 0) {
                    throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
                }
                Producto producto = productoService.findById(productoId).orElseThrow();
                DetallePedido detalle = new DetallePedido();
                detalle.setProducto(producto);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(producto.getPrecio());
                detalle.actualizarSubtotal();
                pedido.agregarDetalle(detalle);
            }
            
            pedidoService.guardar(pedido);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido actualizado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Transactional
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarPedidoAPI(@PathVariable Long id, Authentication authentication) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(id).orElseThrow();
            if (!puedeAcceder(pedido, authentication)) {
                return ResponseEntity.status(403).build();
            }
            pedido.setEstado(EstadoVenta.CANCELADO);
            pedidoService.guardar(pedido);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Transactional
    @DeleteMapping("/api/{id}/detalles/{detalleId}")
    @ResponseBody
    public ResponseEntity<?> eliminarDetallePedido(@PathVariable Long id, @PathVariable Long detalleId, Authentication authentication) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(id).orElseThrow();
            if (!puedeAcceder(pedido, authentication)) {
                return ResponseEntity.status(403).build();
            }
            pedido.getDetalles().removeIf(d -> {
                boolean eliminar = d.getId().equals(detalleId);
                if (eliminar) {
                    d.setPedido(null);
                }
                return eliminar;
            });
            pedido.calcularTotal();
            pedidoService.guardar(pedido);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto eliminado del pedido");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    private boolean puedeAcceder(Pedido pedido, Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        boolean esAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (esAdmin) {
            return true;
        }
        Usuario usuario = usuarioService.obtenerPorUsername(authentication.getName()).orElse(null);
        return usuario != null && pedido.getUsuario() != null && pedido.getUsuario().getId().equals(usuario.getId());
    }

    private Map<String, Object> toDto(Pedido pedido) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", pedido.getId());
        dto.put("clienteNombre", pedido.getClienteNombre());
        dto.put("clienteTelefono", pedido.getClienteTelefono());
        dto.put("clienteDireccion", pedido.getClienteDireccion());
        dto.put("fechaPedido", pedido.getFechaPedido());
        dto.put("subtotal", pedido.getSubtotal());
        dto.put("igv", pedido.getIgv());
        dto.put("total", pedido.getTotal());
        dto.put("nota", pedido.getNota());
        dto.put("estado", pedido.getEstado().name());
        if (pedido.getUsuario() != null) {
            dto.put("usuario", Map.of(
                    "id", pedido.getUsuario().getId(),
                    "username", pedido.getUsuario().getUsername()
            ));
        }
        List<Map<String, Object>> detalles = pedido.getDetalles().stream().map(detalle -> {
            Map<String, Object> detalleDto = new HashMap<>();
            detalleDto.put("id", detalle.getId());
            detalleDto.put("cantidad", detalle.getCantidad());
            detalleDto.put("precioUnitario", detalle.getPrecioUnitario());
            detalleDto.put("subtotal", detalle.getSubtotal());
            detalleDto.put("producto", Map.of(
                    "id", detalle.getProducto().getId(),
                    "nombre", detalle.getProducto().getNombre()
            ));
            return detalleDto;
        }).toList();
        dto.put("detalles", detalles);
        return dto;
    }

    private Long toLong(Object valor) {
        if (valor instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(valor.toString());
    }

    private int toInteger(Object valor) {
        if (valor instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(valor.toString());
    }

    private String toNullableString(Object valor) {
        return valor == null ? null : valor.toString();
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: com/polleriacaporal/model/DetallePedido#
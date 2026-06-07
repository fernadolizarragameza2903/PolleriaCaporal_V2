package com.polleriacaporal.service;

import com.polleriacaporal.model.DetallePedido;
import com.polleriacaporal.model.Pedido;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoPdfService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarPedidoPdf(Pedido pedido) {
        List<String> lineas = crearLineas(pedido);
        String contenido = crearContenidoPagina(lineas);
        return crearPdf(contenido);
    }

    private List<String> crearLineas(Pedido pedido) {
        List<String> lineas = new ArrayList<>();
        lineas.add("Polleria Caporal");
        lineas.add("Pedido #" + pedido.getId());
        lineas.add("Generado: " + FECHA.format(LocalDateTime.now()));
        lineas.add("");
        lineas.add("Fecha del pedido: " + formatearFecha(pedido.getFechaPedido()));
        lineas.add("Cliente: " + texto(pedido.getClienteNombre()));
        lineas.add("Telefono: " + texto(pedido.getClienteTelefono()));
        lineas.add("Mesa / direccion: " + texto(pedido.getClienteDireccion()));
        lineas.add("Atendido por: " + (pedido.getUsuario() == null ? "-" : texto(pedido.getUsuario().getUsername())));
        lineas.add("Estado: " + pedido.getEstado());
        lineas.add("Nota: " + texto(pedido.getNota()));
        lineas.add("");
        lineas.add("Productos");
        lineas.add("------------------------------------------------------------");
        lineas.add("Cant.  Producto                                P.Unit   Subtotal");

        for (DetallePedido detalle : pedido.getDetalles()) {
            String nombre = detalle.getProducto() == null ? "Producto" : detalle.getProducto().getNombre();
            lineas.add(String.format("%-6s %-38s %8s %8s",
                    detalle.getCantidad(),
                    recortar(texto(nombre), 38),
                    moneda(detalle.getPrecioUnitario()),
                    moneda(detalle.getSubtotal())));
        }

        lineas.add("------------------------------------------------------------");
        lineas.add("Subtotal: S/ " + moneda(pedido.getSubtotal()));
        lineas.add("IGV 18%:  S/ " + moneda(pedido.getIgv()));
        lineas.add("Total:    S/ " + moneda(pedido.getTotal()));
        return lineas;
    }

    private String crearContenidoPagina(List<String> lineas) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("BT\n");
        contenido.append("/F1 18 Tf\n");
        contenido.append("50 790 Td\n");
        for (int i = 0; i < lineas.size(); i++) {
            if (i == 1) {
                contenido.append("/F1 12 Tf\n");
            }
            contenido.append("(").append(escapar(lineas.get(i))).append(") Tj\n");
            contenido.append("0 -18 Td\n");
        }
        contenido.append("ET\n");
        return contenido.toString();
    }

    private byte[] crearPdf(String contenidoPagina) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        escribir(out, "%PDF-1.4\n");
        escribirObjeto(out, offsets, 1, "<< /Type /Catalog /Pages 2 0 R >>");
        escribirObjeto(out, offsets, 2, "<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
        escribirObjeto(out, offsets, 3, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>");
        escribirObjeto(out, offsets, 4, "<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>");

        byte[] contenidoBytes = contenidoPagina.getBytes(StandardCharsets.ISO_8859_1);
        offsets.add(out.size());
        escribir(out, "5 0 obj\n");
        escribir(out, "<< /Length " + contenidoBytes.length + " >>\nstream\n");
        out.writeBytes(contenidoBytes);
        escribir(out, "endstream\nendobj\n");

        int xref = out.size();
        escribir(out, "xref\n0 6\n");
        escribir(out, "0000000000 65535 f \n");
        for (Integer offset : offsets) {
            escribir(out, String.format("%010d 00000 n \n", offset));
        }
        escribir(out, "trailer\n<< /Size 6 /Root 1 0 R >>\n");
        escribir(out, "startxref\n" + xref + "\n%%EOF");
        return out.toByteArray();
    }

    private void escribirObjeto(ByteArrayOutputStream out, List<Integer> offsets, int numero, String cuerpo) {
        offsets.add(out.size());
        escribir(out, numero + " 0 obj\n" + cuerpo + "\nendobj\n");
    }

    private void escribir(ByteArrayOutputStream out, String texto) {
        out.writeBytes(texto.getBytes(StandardCharsets.ISO_8859_1));
    }

    private String formatearFecha(LocalDateTime fecha) {
        return fecha == null ? "-" : FECHA.format(fecha);
    }

    private String moneda(BigDecimal valor) {
        return valor == null ? "0.00" : valor.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private String texto(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    private String recortar(String valor, int maximo) {
        return valor.length() <= maximo ? valor : valor.substring(0, maximo - 3) + "...";
    }

    private String escapar(String valor) {
        return valor.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}

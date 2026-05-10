let pedidoActual = [];

// =========================
// 1. RENDER PRODUCTOS
// =========================
function renderizarProductosParaEmpleados(lista = productos) {
    const tabla = document.getElementById("tablaVentaEmpleados");
    if (!tabla) return;

    tabla.innerHTML = "";

    lista.forEach((p, index) => {
        tabla.innerHTML += `
            <tr>
                <td>
                    <span class="fw-bold">${p.nombre}</span><br>
                    <small class="text-muted">${p.categoria}</small>
                </td>
                <td>S/ ${p.precio.toFixed(2)}</td>
                <td>
                    <input type="number" id="cant-${index}" 
                        class="form-control form-control-sm" 
                        value="1" min="1">
                </td>
                <td>
                    <button class="btn btn-sm text-white"
                        style="background-color:#ff8c00;"
                        onclick="agregarAlTicket(${index})">
                        ➕ Agregar
                    </button>
                </td>
            </tr>
        `;
    });
}

// =========================
// 2. AGREGAR PRODUCTO
// =========================
window.agregarAlTicket = function(index) {
    const cantidadInput = document.getElementById(`cant-${index}`);
    const cantidad = parseInt(cantidadInput.value);
    const prod = productos[index];

    if (cantidad <= 0) {
        alert("⚠️ Cantidad inválida");
        return;
    }

    if (prod.stock && cantidad > prod.stock) {
        alert("⚠️ Stock insuficiente");
        return;
    }

    const existente = pedidoActual.find(p => p.nombre === prod.nombre);

    if (existente) {
        existente.cantidad += cantidad;
        existente.subtotal = existente.cantidad * existente.precio;
    } else {
        pedidoActual.push({
            nombre: prod.nombre,
            precio: prod.precio,
            cantidad: cantidad,
            subtotal: prod.precio * cantidad
        });
    }

    cantidadInput.value = 1;

    guardarPedido();
    actualizarPanelDerecho();
};

// =========================
// 3. ELIMINAR ITEM
// =========================
window.eliminarItem = function(nombre) {
    pedidoActual = pedidoActual.filter(p => p.nombre !== nombre);
    guardarPedido();
    actualizarPanelDerecho();
};

// =========================
// 4. CAMBIAR CANTIDAD
// =========================
window.cambiarCantidad = function(nombre, nuevaCantidad) {
    const item = pedidoActual.find(p => p.nombre === nombre);

    if (item && nuevaCantidad > 0) {
        item.cantidad = nuevaCantidad;
        item.subtotal = item.cantidad * item.precio;
        guardarPedido();
        actualizarPanelDerecho();
    }
};

// =========================
// 5. PANEL DERECHO 
// =========================
function actualizarPanelDerecho() {
    const contenedor = document.getElementById("resumenPedido");
    const totalTxt = document.getElementById("totalPagar");
    const igvTxt = document.getElementById("igvMonto");

    let total = 0;

    if (pedidoActual.length === 0) {
        contenedor.innerHTML = `<p class="text-muted text-center py-5">Sin productos</p>`;
        if (igvTxt) igvTxt.innerText = "S/ 0.00";
        totalTxt.innerText = "S/ 0.00";
        return;
    }

    contenedor.innerHTML = "";

    pedidoActual.forEach(item => {
        total += item.subtotal;

        contenedor.innerHTML += `
            <div class="d-flex justify-content-between align-items-center mb-2 bg-white p-2 rounded shadow-sm border-start border-warning border-4">
                
                <div>
                    <input type="number" min="1"
                        value="${item.cantidad}"
                        onchange="cambiarCantidad('${item.nombre}', this.value)"
                        style="width:60px">
                    
                    <span class="ms-2 fw-semibold">${item.nombre}</span>
                </div>

                <div>
                    <span class="fw-bold text-success me-2">
                        S/ ${item.subtotal.toFixed(2)}
                    </span>

                    <button class="btn btn-sm btn-danger"
                        onclick="eliminarItem('${item.nombre}')">
                        ❌
                    </button>
                </div>
            </div>
        `;
    });

    // ✅ CÁLCULO IGV
    const igv = total * 0.18;
    const totalConIGV = total + igv;

    // ✅ MOSTRAR IGV Y TOTAL FINAL
    if (igvTxt) {
        igvTxt.innerText = `S/ ${igv.toFixed(2)}`;
    }

    totalTxt.innerText = `S/ ${totalConIGV.toFixed(2)}`;
}

// =========================
// 6. BUSCADOR
// =========================
window.filtrarProductos = function(texto) {
    const filtrados = productos.filter(p =>
        p.nombre.toLowerCase().includes(texto.toLowerCase())
    );

    renderizarProductosParaEmpleados(filtrados);
};

// =========================
// 7. LOCAL STORAGE
// =========================
function guardarPedido() {
    localStorage.setItem("pedidoActual", JSON.stringify(pedidoActual));
}

function cargarPedido() {
    const data = localStorage.getItem("pedidoActual");

    if (data) {
        pedidoActual = JSON.parse(data);
        actualizarPanelDerecho();
    }
}

// =========================
// 8. PROCESAR PEDIDO
// =========================
window.procesarPedidoFinal = function() {
    const cliente = document.getElementById("nombreCliente").value;

    if (!cliente.trim()) {
        alert("⚠️ Ingresa el nombre del cliente");
        return;
    }

    if (pedidoActual.length === 0) {
        alert("⚠️ No hay productos");
        return;
    }

    if (!confirm("¿Confirmar pedido?")) return;

    let detalle = pedidoActual
        .map(p => `${p.cantidad}x ${p.nombre}`)
        .join("\n");

    let total = document.getElementById("totalPagar").innerText;

    alert(`✅ PEDIDO REALIZADO

Cliente: ${cliente}

Detalle:
${detalle}

TOTAL: ${total}`);

    pedidoActual = [];
    localStorage.removeItem("pedidoActual");
    document.getElementById("nombreCliente").value = "";

    actualizarPanelDerecho();
};

// =========================
// 9. INICIALIZACIÓN
// =========================
document.addEventListener("DOMContentLoaded", () => {
    renderizarProductosParaEmpleados();
    cargarPedido();
});

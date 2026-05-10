const ventas = [
  {id: 1, cliente: "Carlos", productos: "Pollo + Papas", total: 50, estado: "Entregado", fecha: "2026-04-01"},
  {id: 2, cliente: "Ana", productos: "Bebida", total: 35, estado: "Pendiente", fecha: "2026-04-01"},
  {id: 3, cliente: "Luis", productos: "Pollo + Bebida", total: 70, estado: "Entregado", fecha: "2026-04-02"}
];

function cargarVentas(fechaFiltro = null) {
  let tabla = document.getElementById("tablaVentas");
  tabla.innerHTML = ""; // Limpiar tabla
  let totalVentas = 0;
  let ventasSeleccionadas = 0;
  const hoy = new Date().toISOString().split('T')[0];
  const fechaBusqueda = fechaFiltro || hoy;

  ventas.forEach(v => {
    let color = v.estado === "Entregado" ? "success" : "warning";

    totalVentas += v.total;
    if (v.fecha === fechaBusqueda) {
      ventasSeleccionadas += v.total;
    }

    tabla.innerHTML += `
      <tr>
        <td>#${v.id}</td>
        <td>${v.cliente}</td>
        <td>${v.productos}</td>
        <td>S/ ${v.total}</td>
        <td><span class="badge bg-${color}">${v.estado}</span></td>
        <td>${v.fecha}</td>
      </tr>
    `;
  });

  document.getElementById("totalVentas").textContent = "S/ " + totalVentas;
  document.getElementById("ventasSeleccionadas").textContent = "S/ " + ventasSeleccionadas;
}

// Establecer la primera fecha disponible en el selector
document.addEventListener("DOMContentLoaded", function() {
  const inputFecha = document.getElementById("fechaSeleccionada");
  const fechasDisponibles = [...new Set(ventas.map(v => v.fecha))].sort();
  
  if (fechasDisponibles.length > 0) {
    inputFecha.value = fechasDisponibles[0];
  }
  
  cargarVentas(inputFecha.value);
  
  inputFecha.addEventListener("change", function() {
    cargarVentas(this.value);
  });
});
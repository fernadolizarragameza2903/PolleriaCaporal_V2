const reportes = [
  {id: 1, cliente: "Carlos", total: 50, estado: "Entregado", fecha: "2026-04-01"},
  {id: 2, cliente: "Ana", total: 35, estado: "Pendiente", fecha: "2026-04-01"},
  {id: 3, cliente: "Luis", total: 70, estado: "Entregado", fecha: "2026-04-02"}
];

function cargarReportes() {
  let tabla = document.getElementById("tablaReportes");
  let totalDia = 0;

  reportes.forEach(r => {
    let color = r.estado === "Entregado" ? "success" : "warning";

    totalDia += r.total;

    tabla.innerHTML += `
      <tr>
        <td>#${r.id}</td>
        <td>${r.cliente}</td>
        <td>S/ ${r.total}</td>
        <td><span class="badge bg-${color}">${r.estado}</span></td>
        <td>${r.fecha}</td>
      </tr>
    `;
  });

  document.getElementById("ventasDia").textContent = "S/ " + totalDia;
}

cargarReportes();
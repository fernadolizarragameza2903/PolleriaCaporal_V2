let usuarios = [
  {usuario: "admin", rol: "Administrador", estado: "Activo"},
  {usuario: "juan", rol: "Empleado", estado: "Activo"}
];

function mostrarUsuarios() {
  let tabla = document.getElementById("tablaUsuarios");
  tabla.innerHTML = "";

  usuarios.forEach((u, index) => {
    tabla.innerHTML += `
      <tr>
        <td>${u.usuario}</td>
        <td>${u.rol}</td>
        <td><span class="badge bg-success">${u.estado}</span></td>
        <td>
          <button class="btn btn-danger btn-sm" onclick="eliminarUsuario(${index})">Eliminar</button>
        </td>
      </tr>
    `;
  });
}

function agregarUsuario() {
  let usuario = document.getElementById("usuario").value;
  let rol = document.getElementById("rol").value;

  if (usuario === "") {
    alert("Ingrese un usuario");
    return;
  }

  usuarios.push({
    usuario: usuario,
    rol: rol === "admin" ? "Administrador" : "Empleado",
    estado: "Activo"
  });

  mostrarUsuarios();
  document.getElementById("usuario").value = "";
}

function eliminarUsuario(index) {
  usuarios.splice(index, 1);
  mostrarUsuarios();
}

mostrarUsuarios();
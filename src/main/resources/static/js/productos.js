let productoModal;

window.addEventListener("DOMContentLoaded", () => {
    const modalEl = document.getElementById("productoModal");
    if (modalEl) {
        productoModal = new bootstrap.Modal(modalEl);
    }
    
    document.getElementById("btnNuevoProducto").addEventListener("click", () => {
      
        productoModal.show();
    });
});

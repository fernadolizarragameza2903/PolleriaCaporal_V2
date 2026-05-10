/**
 * Script principal de la aplicación Pollería Caporal
 * Contiene funcionalidades comunes y utilidades
 */

document.addEventListener('DOMContentLoaded', function() {
    // Cerrar alertas automáticamente después de 5 segundos
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Inicializar tooltips de Bootstrap
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));

    // Resaltar navegación activa
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.sidebar-nav a');
    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });
});

/**
 * Función para confirmar eliminación
 */
function confirmarEliminacion(mensaje = '¿Está seguro de que desea continuar?') {
    return confirm(mensaje);
}

/**
 * Función para mostrar notificación
 */
function mostrarNotificacion(mensaje, tipo = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${tipo} alert-dismissible fade show`;
    alertDiv.setAttribute('role', 'alert');
    alertDiv.innerHTML = `
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.querySelector('.main-content');
    if (container) {
        container.insertBefore(alertDiv, container.firstChild);
        
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alertDiv);
            bsAlert.close();
        }, 5000);
    }
}

/**
 * Función para validar formularios
 */
function validarFormulario(formId) {
    const form = document.getElementById(formId);
    if (!form.checkValidity() === false) {
        event.preventDefault();
        event.stopPropagation();
    }
    form.classList.add('was-validated');
}

/**
 * Función para formatear moneda
 */
function formatearMoneda(valor) {
    return new Intl.NumberFormat('es-PE', {
        style: 'currency',
        currency: 'PEN'
    }).format(valor);
}

/**
 * Función para calcular total en tablas
 */
function calcularTotal(className) {
    const elementos = document.querySelectorAll(`.${className}`);
    let total = 0;
    elementos.forEach(el => {
        total += parseFloat(el.textContent.replace(/[^0-9.-]+/g, '')) || 0;
    });
    return total;
}

package com.polleriacaporal.model;

/**
 * Enumeración de roles de usuario del sistema
 * Compatible con Spring Security
 */
public enum RolUsuario {
    ROLE_ADMIN("Administrador"),
    ROLE_EMPLOYEE("Empleado");

    private final String descripcion;

    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

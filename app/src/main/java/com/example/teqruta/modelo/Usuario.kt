package com.example.teqruta.modelo

/**
 * Roles de los usuarios en el sistema TEQMED SpA.
 */
enum class RolUsuario(val nombreVisible: String) {
    TECNICO("Técnico de Servicio"),
    SUPERVISOR("Jefe de Servicio Técnico"),
    FINANZAS("Gerencia y Finanzas"),
    ADMINISTRADOR("Administrador del Sistema")
}

/**
 * Representa a un usuario del sistema (personal técnico, jefatura o administración).
 */
data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: RolUsuario,
    val idVehiculoAsignado: String? = null
)

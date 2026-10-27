package com.example.teqruta.modelo

/**
 * Tipo de propiedad del vehículo utilizado para las visitas.
 */
enum class TipoVehiculo(val descripcion: String) {
    EMPRESA("Empresa (Flota TEQMED)"),
    PARTICULAR("Particular (Técnico)")
}

/**
 * Tipo de combustible que utiliza el móvil.
 */
enum class TipoCombustible(val nombre: String) {
    DIESEL("Diésel"),
    GASOLINA_95("Gasolina 95")
}

/**
 * Representa a un vehículo asignado para los traslados en terreno.
 */
data class Vehiculo(
    val id: String,
    val patente: String,
    val marcaModelo: String,
    val tipo: TipoVehiculo,
    val tipoCombustible: TipoCombustible,
    val rendimientoKmPorLitro: Double,
    val nombreTecnicoPropietario: String? = null
)

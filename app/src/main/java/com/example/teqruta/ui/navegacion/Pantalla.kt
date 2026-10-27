package com.example.teqruta.ui.navegacion

/**
 * Rutas de las pantallas del sistema TeqRuta.
 */
sealed class Pantalla(val titulo: String) {
    object Login : Pantalla("Iniciar Sesión")
    object PanelTecnico : Pantalla("Panel Técnico")
    object SeleccionVehiculo : Pantalla("Asignar Vehículo")
    object IniciarTraslado : Pantalla("Iniciar Traslado")
    data class FinalizarTraslado(val idTraslado: String) : Pantalla("Finalizar Traslado")
    object HistorialTraslados : Pantalla("Mis Traslados")
    object AprobacionSupervisor : Pantalla("Auditoría de Traslados")
    data class DetalleAuditoria(val idTraslado: String) : Pantalla("Detalle de Auditoría")
    object ReporteRentabilidad : Pantalla("Reportes y Rentabilidad")
    object TarifasCombustible : Pantalla("Tarifas y Combustible")
    object GestionDestinos : Pantalla("Catastro de Destinos")
    object GestionVehiculos : Pantalla("Gestión de Vehículos")
}

package com.example.teqruta.modelo

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Estado del flujo de aprobación del traslado por el Jefe de Servicio.
 */
enum class EstadoTraslado(val etiqueta: String) {
    EN_CURSO("En Curso"),
    PENDIENTE_APROBACION("Pendiente Aprobación"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado")
}

/**
 * Estado de sincronización en el dispositivo (Soporte Offline 8 horas).
 */
enum class EstadoSincronizacion(val etiqueta: String) {
    LOCAL_PENDIENTE("Pendiente de Sincronizar"),
    SINCRONIZADO("Sincronizado")
}

/**
 * Representa el registro de un viaje en terreno realizado por un técnico.
 */
data class Traslado(
    val id: String,
    val idTecnico: String,
    val nombreTecnico: String,
    val idVehiculo: String,
    val patenteVehiculo: String,
    val tipoVehiculo: TipoVehiculo,
    val tipoCombustible: TipoCombustible,
    val rendimientoKmPorLitro: Double,
    val puntoPartida: String = "Sede Central Concepción",
    val destino: Destino,
    val odometroInicialKm: Double,
    val odometroFinalKm: Double? = null,
    val horaInicioEpoch: Long = System.currentTimeMillis(),
    val horaFinEpoch: Long? = null,
    val distanciaDeclaradaKm: Double = 0.0,
    val distanciaOficialKm: Double = destino.distanciaOficialKmDesdeBase,
    val gastoPeajes: Double = 0.0,
    val precioCombustiblePorLitro: Double = 0.0,
    val costoCombustibleCalculado: Double = 0.0,
    val costoTotalCalculado: Double = 0.0,
    val estado: EstadoTraslado = EstadoTraslado.EN_CURSO,
    val estadoSincronizacion: EstadoSincronizacion = EstadoSincronizacion.LOCAL_PENDIENTE,
    val observacionesTecnico: String = "",
    val observacionesSupervisor: String? = null,
    val validadoPor: String? = null,
    val fechaValidacionEpoch: Long? = null,
    val numeroComprobantePeaje: String? = null,
    val motivoDesviacion: String? = null
) {
    val diferenciaKm: Double
        get() = distanciaDeclaradaKm - distanciaOficialKm

    val porcentajeDesviacion: Double
        get() = if (distanciaOficialKm > 0 && distanciaDeclaradaKm > 0) {
            ((distanciaDeclaradaKm - distanciaOficialKm) / distanciaOficialKm) * 100.0
        } else 0.0

    val esDesviacionCritica: Boolean
        get() = porcentajeDesviacion > 15.0

    val esDesviacionModerada: Boolean
        get() = porcentajeDesviacion in 5.0..15.0

    val horaInicioFormateada: String
        get() = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(horaInicioEpoch))

    val horaFinFormateada: String
        get() = horaFinEpoch?.let {
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(it))
        } ?: "En curso"
}

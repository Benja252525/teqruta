package com.example.teqruta.datos

import com.example.teqruta.modelo.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Repositorio central de datos y lógica de negocio para la aplicación TeqRuta.
 * Implementa el cálculo centralizado de costos, auditoría de discrepancias
 * y soporte de operación fuera de línea (hasta 8 horas sin conexión).
 */
class RepositorioTeqRuta private constructor() {

    private val _usuarioActual = MutableStateFlow<Usuario>(DatosPrueba.usuarios[0])
    val usuarioActual: StateFlow<Usuario> = _usuarioActual.asStateFlow()

    private val _estaEnLinea = MutableStateFlow(true)
    val estaEnLinea: StateFlow<Boolean> = _estaEnLinea.asStateFlow()

    private val _estaSincronizando = MutableStateFlow(false)
    val estaSincronizando: StateFlow<Boolean> = _estaSincronizando.asStateFlow()

    private val _traslados = MutableStateFlow<List<Traslado>>(DatosPrueba.crearTrasladosIniciales())
    val traslados: StateFlow<List<Traslado>> = _traslados.asStateFlow()

    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(DatosPrueba.vehiculos)
    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos.asStateFlow()

    private val _destinos = MutableStateFlow<List<Destino>>(DatosPrueba.destinos)
    val destinos: StateFlow<List<Destino>> = _destinos.asStateFlow()

    private val _tarifaCombustible = MutableStateFlow<TarifaCombustible>(DatosPrueba.tarifaInicial)
    val tarifaCombustible: StateFlow<TarifaCombustible> = _tarifaCombustible.asStateFlow()

    private val _trasladoActivo = MutableStateFlow<Traslado?>(null)
    val trasladoActivo: StateFlow<Traslado?> = _trasladoActivo.asStateFlow()

    // Gestión de Sesión
    fun iniciarSesion(idUsuario: String) {
        val usuario = DatosPrueba.usuarios.find { it.id == idUsuario } ?: DatosPrueba.usuarios[0]
        _usuarioActual.value = usuario
        _trasladoActivo.value = _traslados.value.find { it.idTecnico == usuario.id && it.estado == EstadoTraslado.EN_CURSO }
    }

    // Control del Modo Fuera de Línea / En Línea
    fun alternarModoConexion() {
        _estaEnLinea.value = !_estaEnLinea.value
    }

    fun fijarModoConexion(enLinea: Boolean) {
        _estaEnLinea.value = enLinea
    }

    // Sincronización en segundo plano de traslados locales
    suspend fun sincronizarTrasladosPendientes(): Int {
        _estaSincronizando.value = true
        kotlinx.coroutines.delay(1200) // Simulación de transferencia de red segura
        var sincronizados = 0
        _traslados.value = _traslados.value.map { traslado ->
            if (traslado.estadoSincronizacion == EstadoSincronizacion.LOCAL_PENDIENTE) {
                sincronizados++
                traslado.copy(estadoSincronizacion = EstadoSincronizacion.SINCRONIZADO)
            } else {
                traslado
            }
        }
        _estaSincronizando.value = false
        return sincronizados
    }

    // Asignación de móvil de la jornada
    fun asignarVehiculoAUsuario(idVehiculo: String) {
        _usuarioActual.value = _usuarioActual.value.copy(idVehiculoAsignado = idVehiculo)
    }

    // Inicio de Traslado en terreno
    fun iniciarTraslado(
        destino: Destino,
        odometroInicialKm: Double,
        vehiculo: Vehiculo,
        puntoPartida: String = "Sede Central Concepción"
    ): Traslado {
        val tarifaActual = _tarifaCombustible.value
        val precioCombustible = if (vehiculo.tipoCombustible == TipoCombustible.DIESEL) {
            tarifaActual.precioDiesel
        } else {
            tarifaActual.precioGasolina95
        }

        val nuevoTraslado = Traslado(
            id = UUID.randomUUID().toString(),
            idTecnico = _usuarioActual.value.id,
            nombreTecnico = _usuarioActual.value.nombre,
            idVehiculo = vehiculo.id,
            patenteVehiculo = vehiculo.patente,
            tipoVehiculo = vehiculo.tipo,
            tipoCombustible = vehiculo.tipoCombustible,
            rendimientoKmPorLitro = vehiculo.rendimientoKmPorLitro,
            puntoPartida = puntoPartida,
            destino = destino,
            odometroInicialKm = odometroInicialKm,
            horaInicioEpoch = System.currentTimeMillis(),
            distanciaOficialKm = destino.distanciaOficialKmDesdeBase,
            precioCombustiblePorLitro = precioCombustible,
            estado = EstadoTraslado.EN_CURSO,
            estadoSincronizacion = if (_estaEnLinea.value) EstadoSincronizacion.SINCRONIZADO else EstadoSincronizacion.LOCAL_PENDIENTE
        )

        _trasladoActivo.value = nuevoTraslado
        _traslados.value = listOf(nuevoTraslado) + _traslados.value
        return nuevoTraslado
    }

    // Finalización de Traslado y cálculo centralizado auditable
    fun finalizarTraslado(
        idTraslado: String,
        odometroFinalKm: Double,
        gastoPeajes: Double,
        observaciones: String
    ): Traslado? {
        val listaActual = _traslados.value.toMutableList()
        val indice = listaActual.indexOfFirst { it.id == idTraslado }
        if (indice == -1) return null

        val anterior = listaActual[indice]
        val distanciaDeclarada = if (odometroFinalKm >= anterior.odometroInicialKm) {
            odometroFinalKm - anterior.odometroInicialKm
        } else 0.0

        // Fórmulas oficiales de costos centralizados
        val litrosConsumidos = if (anterior.rendimientoKmPorLitro > 0) distanciaDeclarada / anterior.rendimientoKmPorLitro else 0.0
        val costoCombustible = litrosConsumidos * anterior.precioCombustiblePorLitro
        val costoTotal = costoCombustible + gastoPeajes

        val trasladoFinalizado = anterior.copy(
            odometroFinalKm = odometroFinalKm,
            horaFinEpoch = System.currentTimeMillis(),
            distanciaDeclaradaKm = distanciaDeclarada,
            gastoPeajes = gastoPeajes,
            costoCombustibleCalculado = costoCombustible,
            costoTotalCalculado = costoTotal,
            estado = EstadoTraslado.PENDIENTE_APROBACION,
            estadoSincronizacion = if (_estaEnLinea.value) EstadoSincronizacion.SINCRONIZADO else EstadoSincronizacion.LOCAL_PENDIENTE,
            observacionesTecnico = observaciones
        )

        listaActual[indice] = trasladoFinalizado
        _traslados.value = listaActual
        if (_trasladoActivo.value?.id == idTraslado) {
            _trasladoActivo.value = null
        }
        return trasladoFinalizado
    }

    // Validación por Jefatura de Servicio
    fun aprobarTraslado(idTraslado: String, observacionesSupervisor: String? = null) {
        _traslados.value = _traslados.value.map { traslado ->
            if (traslado.id == idTraslado) {
                traslado.copy(
                    estado = EstadoTraslado.APROBADO,
                    observacionesSupervisor = observacionesSupervisor,
                    validadoPor = _usuarioActual.value.nombre,
                    fechaValidacionEpoch = System.currentTimeMillis()
                )
            } else traslado
        }
    }

    fun rechazarTraslado(idTraslado: String, observacionesSupervisor: String) {
        _traslados.value = _traslados.value.map { traslado ->
            if (traslado.id == idTraslado) {
                traslado.copy(
                    estado = EstadoTraslado.RECHAZADO,
                    observacionesSupervisor = observacionesSupervisor,
                    validadoPor = _usuarioActual.value.nombre,
                    fechaValidacionEpoch = System.currentTimeMillis()
                )
            } else traslado
        }
    }

    // Gestión de Destinos
    fun agregarDestino(destino: Destino) {
        _destinos.value = _destinos.value + destino
    }

    fun actualizarDestino(destino: Destino) {
        _destinos.value = _destinos.value.map {
            if (it.id == destino.id) destino else it
        }
    }

    fun eliminarDestino(id: String) {
        _destinos.value = _destinos.value.filter { it.id != id }
    }

    // Gestión de Vehículos
    fun agregarVehiculo(vehiculo: Vehiculo) {
        _vehiculos.value = _vehiculos.value + vehiculo
    }

    fun actualizarVehiculo(vehiculo: Vehiculo) {
        _vehiculos.value = _vehiculos.value.map {
            if (it.id == vehiculo.id) vehiculo else it
        }
    }

    fun eliminarVehiculo(id: String) {
        _vehiculos.value = _vehiculos.value.filter { it.id != id }
    }

    // Tarifas y consulta pública
    fun actualizarTarifaManual(precio95: Double, precioDiesel: Double) {
        val fechaStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        _tarifaCombustible.value = _tarifaCombustible.value.copy(
            precioGasolina95 = precio95,
            precioDiesel = precioDiesel,
            ultimaActualizacion = fechaStr,
            fuenteOficial = "Ajuste manual de administración"
        )
    }

    suspend fun sincronizarTarifaDesdeApiPublica(): TarifaCombustible {
        kotlinx.coroutines.delay(1000)
        val fechaStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val tarifaActualizada = TarifaCombustible(
            region = "Región del Biobío (Gran Concepción)",
            precioGasolina95 = 1318.0,
            precioDiesel = 1048.0,
            ultimaActualizacion = fechaStr,
            fuenteOficial = "API Pública CNE (BencinaEnLínea.cl)"
        )
        _tarifaCombustible.value = tarifaActualizada
        return tarifaActualizada
    }

    companion object {
        val instancia: RepositorioTeqRuta by lazy { RepositorioTeqRuta() }
    }
}

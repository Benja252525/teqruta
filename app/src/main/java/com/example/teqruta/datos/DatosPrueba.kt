package com.example.teqruta.datos

import com.example.teqruta.modelo.*
import java.util.UUID

/**
 * Datos iniciales simulados del entorno operativo de TEQMED SpA.
 */
object DatosPrueba {
    val usuarios = listOf(
        Usuario("u1", "Juan Pérez", "juan.perez@teqmed.cl", RolUsuario.TECNICO, idVehiculoAsignado = "v1"),
        Usuario("u2", "Carlos Soto", "carlos.soto@teqmed.cl", RolUsuario.TECNICO, idVehiculoAsignado = "v3"),
        Usuario("u3", "Marcelo Valenzuela", "marcelo.valenzuela@teqmed.cl", RolUsuario.SUPERVISOR),
        Usuario("u4", "Patricia Gómez", "patricia.gomez@teqmed.cl", RolUsuario.FINANZAS),
        Usuario("u5", "Administrador TEQMED", "admin@teqmed.cl", RolUsuario.ADMINISTRADOR)
    )

    val destinos = listOf(
        Destino("d1", "Hospital Guillermo Grant Benavente", "Hospital", "Concepción", "San Martín 1436", 4.5, 0.0),
        Destino("d2", "Clínica Sanatorio Alemán", "Clínica", "Concepción", "Pedro de Valdivia 801", 6.2, 0.0),
        Destino("d3", "Hospital Las Higueras", "Hospital", "Talcahuano", "Alto Horno 777", 15.8, 0.0),
        Destino("d4", "Clínica Andes Salud", "Clínica", "Concepción", "Av. Jorge Alessandri 2045", 5.0, 0.0),
        Destino("d5", "Hospital Clínico Herminda Martín", "Hospital", "Chillán", "Francisco Ramírez 10", 102.0, 4900.0),
        Destino("d6", "Centro Diálisis Renal Sur", "Centro de Diálisis", "Chillán", "Gamero 450", 98.5, 4900.0),
        Destino("d7", "Complejo Dr. Víctor Ríos Ruiz", "Hospital", "Los Ángeles", "Av. Ricardo Vicuña 147", 128.0, 3200.0),
        Destino("d8", "Hospital Dr. Hernán Henríquez Aravena", "Hospital", "Temuco", "Manuel Montt 115", 264.0, 9800.0)
    )

    val vehiculos = listOf(
        Vehiculo("v1", "CJ-WP-82", "Peugeot Partner HDi", TipoVehiculo.EMPRESA, TipoCombustible.DIESEL, 15.5),
        Vehiculo("v2", "LZ-TR-44", "Toyota Hilux 4x4", TipoVehiculo.EMPRESA, TipoCombustible.DIESEL, 11.2),
        Vehiculo("v3", "KH-BB-19", "Hyundai Accent 1.4", TipoVehiculo.PARTICULAR, TipoCombustible.GASOLINA_95, 14.2, "Juan Pérez"),
        Vehiculo("v4", "RJ-KL-55", "Suzuki Swift 1.2", TipoVehiculo.PARTICULAR, TipoCombustible.GASOLINA_95, 16.8, "Carlos Soto")
    )

    val tarifaInicial = TarifaCombustible(
        region = "Región del Biobío",
        precioGasolina95 = 1315.0,
        precioDiesel = 1045.0,
        ultimaActualizacion = "22/10/2026 08:30",
        fuenteOficial = "Comisión Nacional de Energía (CNE) - BencinaEnLínea"
    )

    fun crearTrasladosIniciales(): List<Traslado> {
        val ahora = System.currentTimeMillis()
        val unaHora = 3600 * 1000L
        val unDia = 24 * unaHora

        return listOf(
            // Traslado 1: Juan Pérez a Chillán con desviación leve (Conforme / Verde)
            Traslado(
                id = UUID.randomUUID().toString(),
                idTecnico = "u1",
                nombreTecnico = "Juan Pérez",
                idVehiculo = "v1",
                patenteVehiculo = "CJ-WP-82",
                tipoVehiculo = TipoVehiculo.EMPRESA,
                tipoCombustible = TipoCombustible.DIESEL,
                rendimientoKmPorLitro = 15.5,
                destino = destinos[4], // Hospital Herminda Martín (102 km)
                odometroInicialKm = 124500.0,
                odometroFinalKm = 124606.0,
                horaInicioEpoch = ahora - (2 * unDia) - (3 * unaHora),
                horaFinEpoch = ahora - (2 * unDia) - (1 * unaHora),
                distanciaDeclaradaKm = 106.0,
                distanciaOficialKm = 102.0,
                gastoPeajes = 4900.0,
                precioCombustiblePorLitro = 1045.0,
                costoCombustibleCalculado = (106.0 / 15.5) * 1045.0,
                costoTotalCalculado = ((106.0 / 15.5) * 1045.0) + 4900.0,
                estado = EstadoTraslado.APROBADO,
                estadoSincronizacion = EstadoSincronizacion.SINCRONIZADO,
                observacionesTecnico = "Mantenimiento preventivo equipo RX pabellón 2",
                observacionesSupervisor = "Conforme. Desvío menor por desvío de tránsito en Ruta del Itata.",
                validadoPor = "Marcelo Valenzuela",
                fechaValidacionEpoch = ahora - (2 * unDia)
            ),
            // Traslado 2: Carlos Soto a Los Ángeles con desviación alta (Alerta Roja > 15%)
            Traslado(
                id = UUID.randomUUID().toString(),
                idTecnico = "u2",
                nombreTecnico = "Carlos Soto",
                idVehiculo = "v4",
                patenteVehiculo = "RJ-KL-55",
                tipoVehiculo = TipoVehiculo.PARTICULAR,
                tipoCombustible = TipoCombustible.GASOLINA_95,
                rendimientoKmPorLitro = 16.8,
                destino = destinos[6], // Complejo Víctor Ríos Ruiz (128 km)
                odometroInicialKm = 88200.0,
                odometroFinalKm = 88358.0,
                horaInicioEpoch = ahora - (1 * unDia) - (4 * unaHora),
                horaFinEpoch = ahora - (1 * unDia) - (2 * unaHora),
                distanciaDeclaradaKm = 158.0, // Oficial 128 -> +30 km (+23.4%)
                distanciaOficialKm = 128.0,
                gastoPeajes = 3200.0,
                precioCombustiblePorLitro = 1315.0,
                costoCombustibleCalculado = (158.0 / 16.8) * 1315.0,
                costoTotalCalculado = ((158.0 / 16.8) * 1315.0) + 3200.0,
                estado = EstadoTraslado.PENDIENTE_APROBACION,
                estadoSincronizacion = EstadoSincronizacion.SINCRONIZADO,
                observacionesTecnico = "Revisión urgente de monitor multiparámetro en UCI.",
                observacionesSupervisor = null
            ),
            // Traslado 3: Juan Pérez a Hospital Las Higueras (Guardado localmente - Modo Offline)
            Traslado(
                id = UUID.randomUUID().toString(),
                idTecnico = "u1",
                nombreTecnico = "Juan Pérez",
                idVehiculo = "v1",
                patenteVehiculo = "CJ-WP-82",
                tipoVehiculo = TipoVehiculo.EMPRESA,
                tipoCombustible = TipoCombustible.DIESEL,
                rendimientoKmPorLitro = 15.5,
                destino = destinos[2], // Higueras (15.8 km)
                odometroInicialKm = 124606.0,
                odometroFinalKm = 124623.0,
                horaInicioEpoch = ahora - (3 * unaHora),
                horaFinEpoch = ahora - (2 * unaHora),
                distanciaDeclaradaKm = 17.0,
                distanciaOficialKm = 15.8,
                gastoPeajes = 0.0,
                precioCombustiblePorLitro = 1045.0,
                costoCombustibleCalculado = (17.0 / 15.5) * 1045.0,
                costoTotalCalculado = (17.0 / 15.5) * 1045.0,
                estado = EstadoTraslado.PENDIENTE_APROBACION,
                estadoSincronizacion = EstadoSincronizacion.LOCAL_PENDIENTE,
                observacionesTecnico = "Atención técnica bomba de infusión continua. Subterráneo sin cobertura.",
                observacionesSupervisor = null
            )
        )
    }
}

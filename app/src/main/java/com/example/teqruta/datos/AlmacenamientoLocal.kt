package com.example.teqruta.datos

import android.content.Context
import com.example.teqruta.modelo.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Gestor de persistencia física en el almacenamiento interno del dispositivo Android.
 * Garantiza que ante cierres de la app, reinicios o batería agotada durante
 * las 8 horas de trabajo en terreno, NO exista pérdida de datos (Offline-First).
 */
class AlmacenamientoLocal(private val contexto: Context) {

    private val archivoDatos = File(contexto.filesDir, "teqruta_datos_locales.json")

    fun guardarDatos(
        traslados: List<Traslado>,
        vehiculos: List<Vehiculo>,
        destinos: List<Destino>,
        tarifa: TarifaCombustible
    ) {
        try {
            val jsonPrincipal = JSONObject()

            // 1. Guardar Traslados
            val arrayTraslados = JSONArray()
            traslados.forEach { t ->
                val objT = JSONObject().apply {
                    put("id", t.id)
                    put("idTecnico", t.idTecnico)
                    put("nombreTecnico", t.nombreTecnico)
                    put("idVehiculo", t.idVehiculo)
                    put("patenteVehiculo", t.patenteVehiculo)
                    put("tipoVehiculo", t.tipoVehiculo.name)
                    put("tipoCombustible", t.tipoCombustible.name)
                    put("rendimientoKmPorLitro", t.rendimientoKmPorLitro)
                    put("puntoPartida", t.puntoPartida)
                    put("odometroInicialKm", t.odometroInicialKm)
                    put("odometroFinalKm", t.odometroFinalKm ?: JSONObject.NULL)
                    put("horaInicioEpoch", t.horaInicioEpoch)
                    put("horaFinEpoch", t.horaFinEpoch ?: JSONObject.NULL)
                    put("distanciaDeclaradaKm", t.distanciaDeclaradaKm)
                    put("distanciaOficialKm", t.distanciaOficialKm)
                    put("gastoPeajes", t.gastoPeajes)
                    put("precioCombustiblePorLitro", t.precioCombustiblePorLitro)
                    put("costoCombustibleCalculado", t.costoCombustibleCalculado)
                    put("costoTotalCalculado", t.costoTotalCalculado)
                    put("estado", t.estado.name)
                    put("estadoSincronizacion", t.estadoSincronizacion.name)
                    put("observacionesTecnico", t.observacionesTecnico)
                    put("observacionesSupervisor", t.observacionesSupervisor ?: JSONObject.NULL)
                    put("validadoPor", t.validadoPor ?: JSONObject.NULL)
                    put("fechaValidacionEpoch", t.fechaValidacionEpoch ?: JSONObject.NULL)
                    put("numeroComprobantePeaje", t.numeroComprobantePeaje ?: JSONObject.NULL)
                    put("motivoDesviacion", t.motivoDesviacion ?: JSONObject.NULL)

                    // Objeto Destino anidado
                    val objDest = JSONObject().apply {
                        put("id", t.destino.id)
                        put("nombre", t.destino.nombre)
                        put("tipoInstitucion", t.destino.tipoInstitucion)
                        put("ciudad", t.destino.ciudad)
                        put("direccion", t.destino.direccion)
                        put("distanciaOficialKmDesdeBase", t.destino.distanciaOficialKmDesdeBase)
                        put("peajeOficialEstimadoIda", t.destino.peajeOficialEstimadoIda)
                    }
                    put("destino", objDest)
                }
                arrayTraslados.put(objT)
            }
            jsonPrincipal.put("traslados", arrayTraslados)

            // 2. Guardar Vehículos
            val arrayVehiculos = JSONArray()
            vehiculos.forEach { v ->
                val objV = JSONObject().apply {
                    put("id", v.id)
                    put("patente", v.patente)
                    put("marcaModelo", v.marcaModelo)
                    put("tipo", v.tipo.name)
                    put("tipoCombustible", v.tipoCombustible.name)
                    put("rendimientoKmPorLitro", v.rendimientoKmPorLitro)
                    put("nombreTecnicoPropietario", v.nombreTecnicoPropietario ?: JSONObject.NULL)
                }
                arrayVehiculos.put(objV)
            }
            jsonPrincipal.put("vehiculos", arrayVehiculos)

            // 3. Guardar Destinos
            val arrayDestinos = JSONArray()
            destinos.forEach { d ->
                val objD = JSONObject().apply {
                    put("id", d.id)
                    put("nombre", d.nombre)
                    put("tipoInstitucion", d.tipoInstitucion)
                    put("ciudad", d.ciudad)
                    put("direccion", d.direccion)
                    put("distanciaOficialKmDesdeBase", d.distanciaOficialKmDesdeBase)
                    put("peajeOficialEstimadoIda", d.peajeOficialEstimadoIda)
                }
                arrayDestinos.put(objD)
            }
            jsonPrincipal.put("destinos", arrayDestinos)

            // 4. Guardar Tarifa
            val objTarifa = JSONObject().apply {
                put("region", tarifa.region)
                put("precioGasolina95", tarifa.precioGasolina95)
                put("precioDiesel", tarifa.precioDiesel)
                put("ultimaActualizacion", tarifa.ultimaActualizacion)
                put("fuenteOficial", tarifa.fuenteOficial)
            }
            jsonPrincipal.put("tarifa", objTarifa)

            archivoDatos.writeText(jsonPrincipal.toString(2))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cargarDatos(): DatosGuardados? {
        if (!archivoDatos.exists()) return null
        return try {
            val contenido = archivoDatos.readText()
            val json = JSONObject(contenido)

            // Leer Traslados
            val arrT = json.getJSONArray("traslados")
            val listaTraslados = mutableListOf<Traslado>()
            for (i in 0 until arrT.length()) {
                val o = arrT.getJSONObject(i)
                val od = o.getJSONObject("destino")
                val dest = Destino(
                    id = od.getString("id"),
                    nombre = od.getString("nombre"),
                    tipoInstitucion = od.getString("tipoInstitucion"),
                    ciudad = od.getString("ciudad"),
                    direccion = od.getString("direccion"),
                    distanciaOficialKmDesdeBase = od.getDouble("distanciaOficialKmDesdeBase"),
                    peajeOficialEstimadoIda = od.getDouble("peajeOficialEstimadoIda")
                )

                listaTraslados.add(
                    Traslado(
                        id = o.getString("id"),
                        idTecnico = o.getString("idTecnico"),
                        nombreTecnico = o.getString("nombreTecnico"),
                        idVehiculo = o.getString("idVehiculo"),
                        patenteVehiculo = o.getString("patenteVehiculo"),
                        tipoVehiculo = TipoVehiculo.valueOf(o.getString("tipoVehiculo")),
                        tipoCombustible = TipoCombustible.valueOf(o.getString("tipoCombustible")),
                        rendimientoKmPorLitro = o.getDouble("rendimientoKmPorLitro"),
                        puntoPartida = o.optString("puntoPartida", "Sede Central Concepción"),
                        destino = dest,
                        odometroInicialKm = o.getDouble("odometroInicialKm"),
                        odometroFinalKm = if (o.isNull("odometroFinalKm")) null else o.getDouble("odometroFinalKm"),
                        horaInicioEpoch = o.getLong("horaInicioEpoch"),
                        horaFinEpoch = if (o.isNull("horaFinEpoch")) null else o.getLong("horaFinEpoch"),
                        distanciaDeclaradaKm = o.getDouble("distanciaDeclaradaKm"),
                        distanciaOficialKm = o.getDouble("distanciaOficialKm"),
                        gastoPeajes = o.getDouble("gastoPeajes"),
                        precioCombustiblePorLitro = o.getDouble("precioCombustiblePorLitro"),
                        costoCombustibleCalculado = o.getDouble("costoCombustibleCalculado"),
                        costoTotalCalculado = o.getDouble("costoTotalCalculado"),
                        estado = EstadoTraslado.valueOf(o.getString("estado")),
                        estadoSincronizacion = EstadoSincronizacion.valueOf(o.getString("estadoSincronizacion")),
                        observacionesTecnico = o.optString("observacionesTecnico", ""),
                        observacionesSupervisor = if (o.isNull("observacionesSupervisor")) null else o.getString("observacionesSupervisor"),
                        validadoPor = if (o.isNull("validadoPor")) null else o.getString("validadoPor"),
                        fechaValidacionEpoch = if (o.isNull("fechaValidacionEpoch")) null else o.getLong("fechaValidacionEpoch"),
                        numeroComprobantePeaje = if (o.isNull("numeroComprobantePeaje")) null else o.getString("numeroComprobantePeaje"),
                        motivoDesviacion = if (o.isNull("motivoDesviacion")) null else o.getString("motivoDesviacion")
                    )
                )
            }

            // Leer Vehículos
            val arrV = json.getJSONArray("vehiculos")
            val listaVehiculos = mutableListOf<Vehiculo>()
            for (i in 0 until arrV.length()) {
                val o = arrV.getJSONObject(i)
                listaVehiculos.add(
                    Vehiculo(
                        id = o.getString("id"),
                        patente = o.getString("patente"),
                        marcaModelo = o.getString("marcaModelo"),
                        tipo = TipoVehiculo.valueOf(o.getString("tipo")),
                        tipoCombustible = TipoCombustible.valueOf(o.getString("tipoCombustible")),
                        rendimientoKmPorLitro = o.getDouble("rendimientoKmPorLitro"),
                        nombreTecnicoPropietario = if (o.isNull("nombreTecnicoPropietario")) null else o.getString("nombreTecnicoPropietario")
                    )
                )
            }

            // Leer Destinos
            val arrD = json.getJSONArray("destinos")
            val listaDestinos = mutableListOf<Destino>()
            for (i in 0 until arrD.length()) {
                val o = arrD.getJSONObject(i)
                listaDestinos.add(
                    Destino(
                        id = o.getString("id"),
                        nombre = o.getString("nombre"),
                        tipoInstitucion = o.getString("tipoInstitucion"),
                        ciudad = o.getString("ciudad"),
                        direccion = o.getString("direccion"),
                        distanciaOficialKmDesdeBase = o.getDouble("distanciaOficialKmDesdeBase"),
                        peajeOficialEstimadoIda = o.getDouble("peajeOficialEstimadoIda")
                    )
                )
            }

            // Leer Tarifa
            val ot = json.getJSONObject("tarifa")
            val tarifa = TarifaCombustible(
                region = ot.getString("region"),
                precioGasolina95 = ot.getDouble("precioGasolina95"),
                precioDiesel = ot.getDouble("precioDiesel"),
                ultimaActualizacion = ot.getString("ultimaActualizacion"),
                fuenteOficial = ot.getString("fuenteOficial")
            )

            DatosGuardados(listaTraslados, listaVehiculos, listaDestinos, tarifa)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class DatosGuardados(
    val traslados: List<Traslado>,
    val vehiculos: List<Vehiculo>,
    val destinos: List<Destino>,
    val tarifa: TarifaCombustible
)

package com.example.teqruta.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.teqruta.datos.RepositorioTeqRuta
import com.example.teqruta.modelo.EstadoSincronizacion
import com.example.teqruta.modelo.RolUsuario
import com.example.teqruta.ui.componentes.BarraSuperiorTeqRuta
import com.example.teqruta.ui.navegacion.Pantalla
import com.example.teqruta.ui.pantallas.*
import kotlinx.coroutines.launch

data class ElementoNavegacion(
    val titulo: String,
    val icono: ImageVector,
    val pantalla: Pantalla
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AplicacionTeqRuta(
    repositorio: RepositorioTeqRuta = RepositorioTeqRuta.instancia
) {
    val usuarioActual by repositorio.usuarioActual.collectAsState()
    val estaEnLinea by repositorio.estaEnLinea.collectAsState()
    val traslados by repositorio.traslados.collectAsState()

    val cantidadPendientesSincronizar = traslados.count { it.estadoSincronizacion == EstadoSincronizacion.LOCAL_PENDIENTE }
    val alcanceCorrutina = rememberCoroutineScope()
    val estadoSnackbar = remember { SnackbarHostState() }

    // Pila de Navegación
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Login) }
    var pilaPantallas by remember { mutableStateOf(listOf<Pantalla>(Pantalla.Login)) }

    fun navegarA(nuevaPantalla: Pantalla) {
        pilaPantallas = pilaPantallas + nuevaPantalla
        pantallaActual = nuevaPantalla
    }

    fun retroceder() {
        if (pilaPantallas.size > 1) {
            val nuevaPila = pilaPantallas.dropLast(1)
            pilaPantallas = nuevaPila
            pantallaActual = nuevaPila.last()
        }
    }

    fun reiniciarA(nuevaPantalla: Pantalla) {
        pilaPantallas = listOf(nuevaPantalla)
        pantallaActual = nuevaPantalla
    }

    // Pestañas según el rol activo
    val elementosNavegacion = when (usuarioActual.rol) {
        RolUsuario.TECNICO -> listOf(
            ElementoNavegacion("Mi Panel", Icons.Default.Dashboard, Pantalla.PanelTecnico),
            ElementoNavegacion("Historial", Icons.Default.History, Pantalla.HistorialTraslados),
            ElementoNavegacion("Vehículo", Icons.Default.DirectionsCar, Pantalla.SeleccionVehiculo)
        )
        RolUsuario.SUPERVISOR -> listOf(
            ElementoNavegacion("Auditoría", Icons.AutoMirrored.Filled.FactCheck, Pantalla.AprobacionSupervisor),
            ElementoNavegacion("Rentabilidad", Icons.Default.Analytics, Pantalla.ReporteRentabilidad),
            ElementoNavegacion("Tarifas", Icons.Default.LocalGasStation, Pantalla.TarifasCombustible)
        )
        RolUsuario.FINANZAS -> listOf(
            ElementoNavegacion("Rentabilidad", Icons.Default.Analytics, Pantalla.ReporteRentabilidad),
            ElementoNavegacion("Tarifas", Icons.Default.LocalGasStation, Pantalla.TarifasCombustible),
            ElementoNavegacion("Destinos", Icons.Default.LocalHospital, Pantalla.GestionDestinos)
        )
        RolUsuario.ADMINISTRADOR -> listOf(
            ElementoNavegacion("Tarifas", Icons.Default.LocalGasStation, Pantalla.TarifasCombustible),
            ElementoNavegacion("Destinos", Icons.Default.LocalHospital, Pantalla.GestionDestinos),
            ElementoNavegacion("Vehículos", Icons.Default.DirectionsCar, Pantalla.GestionVehiculos),
            ElementoNavegacion("Auditoría", Icons.AutoMirrored.Filled.FactCheck, Pantalla.AprobacionSupervisor)
        )
    }

    val puedeRetroceder = pilaPantallas.size > 1 && pantallaActual !is Pantalla.Login

    Scaffold(
        snackbarHost = { SnackbarHost(estadoSnackbar) },
        topBar = {
            if (pantallaActual !is Pantalla.Login) {
                BarraSuperiorTeqRuta(
                    titulo = pantallaActual.titulo,
                    puedeRetroceder = puedeRetroceder,
                    alRetroceder = { retroceder() },
                    estaEnLinea = estaEnLinea,
                    cantidadPendientesSincronizar = cantidadPendientesSincronizar,
                    usuarioActual = usuarioActual,
                    alAlternarModoConexion = {
                        repositorio.alternarModoConexion()
                        alcanceCorrutina.launch {
                            estadoSnackbar.showSnackbar(
                                if (!estaEnLinea) "Modo En Línea activado." else "Modo Fuera de Línea (Offline) activado. Registros se guardarán en el dispositivo."
                            )
                        }
                    },
                    alSincronizar = {
                        if (!estaEnLinea) {
                            alcanceCorrutina.launch {
                                estadoSnackbar.showSnackbar("Sin conexión a internet. Active el modo en línea para sincronizar.")
                            }
                        } else {
                            alcanceCorrutina.launch {
                                val cant = repositorio.sincronizarTrasladosPendientes()
                                estadoSnackbar.showSnackbar("Sincronización completa: $cant registro(s) actualizados.")
                            }
                        }
                    },
                    alCerrarSesion = {
                        reiniciarA(Pantalla.Login)
                    }
                )
            }
        },
        bottomBar = {
            if (pantallaActual !is Pantalla.Login) {
                NavigationBar {
                    elementosNavegacion.forEach { elemento ->
                        val estaSeleccionado = pantallaActual::class == elemento.pantalla::class
                        NavigationBarItem(
                            selected = estaSeleccionado,
                            onClick = { reiniciarA(elemento.pantalla) },
                            icon = { Icon(elemento.icono, contentDescription = elemento.titulo) },
                            label = { Text(elemento.titulo) }
                        )
                    }
                }
            }
        }
    ) { paddingInterior ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterior)
        ) {
            when (val pantalla = pantallaActual) {
                is Pantalla.Login -> {
                    PantallaLogin(
                        repositorio = repositorio,
                        alIniciarSesionExitoso = { rol ->
                            val destino = when (rol) {
                                RolUsuario.TECNICO -> Pantalla.PanelTecnico
                                RolUsuario.SUPERVISOR -> Pantalla.AprobacionSupervisor
                                RolUsuario.FINANZAS -> Pantalla.ReporteRentabilidad
                                RolUsuario.ADMINISTRADOR -> Pantalla.TarifasCombustible
                            }
                            reiniciarA(destino)
                        }
                    )
                }

                is Pantalla.PanelTecnico -> {
                    PantallaPanelTecnico(
                        repositorio = repositorio,
                        alNavegarAIniciarTraslado = { navegarA(Pantalla.IniciarTraslado) },
                        alNavegarAFinalizarTraslado = { id -> navegarA(Pantalla.FinalizarTraslado(id)) },
                        alNavegarAHistorial = { navegarA(Pantalla.HistorialTraslados) },
                        alNavegarASeleccionVehiculo = { navegarA(Pantalla.SeleccionVehiculo) }
                    )
                }

                is Pantalla.SeleccionVehiculo -> {
                    PantallaSeleccionVehiculo(
                        repositorio = repositorio,
                        alConfirmarVehiculo = {
                            if (puedeRetroceder) retroceder() else reiniciarA(Pantalla.PanelTecnico)
                        }
                    )
                }

                is Pantalla.IniciarTraslado -> {
                    PantallaIniciarTraslado(
                        repositorio = repositorio,
                        alIniciarTrasladoExitoso = {
                            reiniciarA(Pantalla.PanelTecnico)
                        }
                    )
                }

                is Pantalla.FinalizarTraslado -> {
                    PantallaFinalizarTraslado(
                        idTraslado = pantalla.idTraslado,
                        repositorio = repositorio,
                        alFinalizarTrasladoExitoso = {
                            reiniciarA(Pantalla.PanelTecnico)
                        }
                    )
                }

                is Pantalla.HistorialTraslados -> {
                    PantallaHistorialTraslados(
                        repositorio = repositorio,
                        alNavegarADetalleTraslado = { id ->
                            navegarA(Pantalla.DetalleAuditoria(id))
                        }
                    )
                }

                is Pantalla.AprobacionSupervisor -> {
                    PantallaAprobacionSupervisor(
                        repositorio = repositorio,
                        alNavegarADetalleAuditoria = { id ->
                            navegarA(Pantalla.DetalleAuditoria(id))
                        }
                    )
                }

                is Pantalla.DetalleAuditoria -> {
                    PantallaDetalleAuditoria(
                        idTraslado = pantalla.idTraslado,
                        repositorio = repositorio,
                        alFinalizarAuditoria = {
                            retroceder()
                        }
                    )
                }

                is Pantalla.ReporteRentabilidad -> {
                    PantallaReporteRentabilidad(
                        repositorio = repositorio
                    )
                }

                is Pantalla.TarifasCombustible -> {
                    PantallaTarifasCombustible(
                        repositorio = repositorio
                    )
                }

                is Pantalla.GestionDestinos -> {
                    PantallaGestionDestinos(
                        repositorio = repositorio
                    )
                }

                is Pantalla.GestionVehiculos -> {
                    PantallaGestionVehiculos(
                        repositorio = repositorio
                    )
                }
            }
        }
    }
}

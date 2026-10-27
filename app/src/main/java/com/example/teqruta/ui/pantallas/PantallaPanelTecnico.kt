package com.example.teqruta.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqruta.datos.RepositorioTeqRuta
import com.example.teqruta.modelo.EstadoSincronizacion
import com.example.teqruta.modelo.EstadoTraslado
import com.example.teqruta.ui.componentes.TarjetaMetrica
import kotlinx.coroutines.launch

@Composable
fun PantallaPanelTecnico(
    repositorio: RepositorioTeqRuta,
    alNavegarAIniciarTraslado: () -> Unit,
    alNavegarAFinalizarTraslado: (String) -> Unit,
    alNavegarAHistorial: () -> Unit,
    alNavegarASeleccionVehiculo: () -> Unit
) {
    val usuarioActual by repositorio.usuarioActual.collectAsState()
    val estaEnLinea by repositorio.estaEnLinea.collectAsState()
    val estaSincronizando by repositorio.estaSincronizando.collectAsState()
    val traslados by repositorio.traslados.collectAsState()
    val trasladoActivo by repositorio.trasladoActivo.collectAsState()
    val vehiculos by repositorio.vehiculos.collectAsState()

    val alcanceCorrutina = rememberCoroutineScope()
    var mensajeSincronizacion by remember { mutableStateOf<String?>(null) }

    val misTraslados = traslados.filter { it.idTecnico == usuarioActual.id }
    val trasladosPendientesSincronizar = misTraslados.filter { it.estadoSincronizacion == EstadoSincronizacion.LOCAL_PENDIENTE }
    val vehiculoAsignado = vehiculos.find { it.id == usuarioActual.idVehiculoAsignado } ?: vehiculos.firstOrNull()

    // Métricas del técnico
    val totalKm = misTraslados.filter { it.estado != EstadoTraslado.EN_CURSO }.sumOf { it.distanciaDeclaradaKm }
    val cantidadTraslados = misTraslados.size
    val gastoTotal = misTraslados.filter { it.estado != EstadoTraslado.EN_CURSO }.sumOf { it.costoTotalCalculado }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Alerta de datos pendientes de sincronización
        if (trasladosPendientesSincronizar.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${trasladosPendientesSincronizar.size} traslado(s) guardado(s) localmente",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFBF360C)
                        )
                        Text(
                            text = if (estaEnLinea) "Conexión detectada. Puede sincronizar con la central." else "Modo terreno sin señal. Los datos están protegidos en el dispositivo.",
                            fontSize = 11.sp,
                            color = Color(0xFF795548)
                        )
                    }
                    if (estaEnLinea) {
                        Button(
                            onClick = {
                                alcanceCorrutina.launch {
                                    val cant = repositorio.sincronizarTrasladosPendientes()
                                    mensajeSincronizacion = "Se sincronizaron $cant traslado(s) correctamente."
                                }
                            },
                            enabled = !estaSincronizando,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (estaSincronizando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Sincronizar", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        if (mensajeSincronizacion != null) {
            Text(
                text = mensajeSincronizacion!!,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Tarjeta de Vehículo en uso
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Vehículo para la jornada:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = vehiculoAsignado?.let { "${it.marcaModelo} (${it.patente})" } ?: "Sin vehículo asignado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = vehiculoAsignado?.tipo?.descripcion ?: "Asigne un vehículo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = alNavegarASeleccionVehiculo) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Cambiar Vehículo")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Traslado en Curso (si existe)
        if (trasladoActivo != null) {
            val traslado = trasladoActivo!!
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1565C0)
                        ) {
                            Text(
                                text = "TRASLADO EN CURSO",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = traslado.horaInicioFormateada,
                            fontSize = 12.sp,
                            color = Color(0xFF0D47A1)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Destino: ${traslado.destino.nombre}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                    Text(
                        text = "${traslado.destino.ciudad} · ${traslado.destino.direccion}",
                        fontSize = 13.sp,
                        color = Color(0xFF1E88E5)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Odómetro Inicial: ${String.format("%.1f", traslado.odometroInicialKm)} km",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Distancia Oficial Registrada: ${traslado.distanciaOficialKm} km",
                        fontSize = 12.sp,
                        color = Color(0xFF555555)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { alNavegarAFinalizarTraslado(traslado.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finalizar Traslado y Registrar Llegada")
                    }
                }
            }
        } else {
            // Botón para INICIAR traslado
            Button(
                onClick = alNavegarAIniciarTraslado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Iniciar Nuevo Traslado",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Resumen de Mi Jornada:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaMetrica(
                titulo = "Kilómetros",
                valor = "${String.format("%.1f", totalKm)} km",
                icono = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
            TarjetaMetrica(
                titulo = "Traslados",
                valor = "$cantidadTraslados",
                icono = Icons.AutoMirrored.Filled.AltRoute,
                colorIcono = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TarjetaMetrica(
            titulo = "Costo Total Movilización Estimado",
            valor = "$${String.format("%,.0f", gastoTotal)} CLP",
            subtitulo = "Combustible y peajes auditables",
            icono = Icons.Default.Payments,
            colorIcono = Color(0xFF2E7D32),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = alNavegarAHistorial,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.History, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Historial Completo de Traslados")
        }
    }
}

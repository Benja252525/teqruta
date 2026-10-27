package com.example.teqruta.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqruta.datos.RepositorioTeqRuta
import com.example.teqruta.modelo.EstadoTraslado
import com.example.teqruta.ui.componentes.EtiquetaEstadoSincronizacion
import com.example.teqruta.ui.componentes.EtiquetaEstadoTraslado
import com.example.teqruta.ui.componentes.IndicadorDesviacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorialTraslados(
    repositorio: RepositorioTeqRuta,
    alNavegarADetalleTraslado: (String) -> Unit
) {
    val usuarioActual by repositorio.usuarioActual.collectAsState()
    val traslados by repositorio.traslados.collectAsState()

    var filtroSeleccionado by remember { mutableStateOf<EstadoTraslado?>(null) }

    val misTraslados = traslados.filter { it.idTecnico == usuarioActual.id }
    val trasladosFiltrados = if (filtroSeleccionado == null) misTraslados else misTraslados.filter { it.estado == filtroSeleccionado }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Historial de Mis Traslados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Consulte el estado de sincronización y aprobación de sus traslados.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = filtroSeleccionado == null,
                onClick = { filtroSeleccionado = null },
                label = { Text("Todos (${misTraslados.size})", fontSize = 11.sp) }
            )
            FilterChip(
                selected = filtroSeleccionado == EstadoTraslado.PENDIENTE_APROBACION,
                onClick = { filtroSeleccionado = EstadoTraslado.PENDIENTE_APROBACION },
                label = { Text("Pendientes", fontSize = 11.sp) }
            )
            FilterChip(
                selected = filtroSeleccionado == EstadoTraslado.APROBADO,
                onClick = { filtroSeleccionado = EstadoTraslado.APROBADO },
                label = { Text("Aprobados", fontSize = 11.sp) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (trasladosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron traslados para este criterio.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trasladosFiltrados) { traslado ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { alNavegarADetalleTraslado(traslado.id) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = traslado.destino.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                EtiquetaEstadoTraslado(estado = traslado.estado)
                            }

                            Text(
                                text = "${traslado.destino.ciudad} · ${traslado.horaInicioFormateada}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Distancia: ${String.format("%.1f", traslado.distanciaDeclaradaKm)} km",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Vehículo: ${traslado.patenteVehiculo} (${traslado.tipoVehiculo.descripcion.take(8)})",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$${String.format("%,.0f", traslado.costoTotalCalculado)} CLP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF2E7D32)
                                    )
                                    EtiquetaEstadoSincronizacion(estado = traslado.estadoSincronizacion)
                                }
                            }

                            if (traslado.distanciaDeclaradaKm > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                IndicadorDesviacion(
                                    distanciaDeclaradaKm = traslado.distanciaDeclaradaKm,
                                    distanciaOficialKm = traslado.distanciaOficialKm
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.teqruta.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.*
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
import com.example.teqruta.ui.componentes.EtiquetaEstadoTraslado
import com.example.teqruta.ui.componentes.IndicadorDesviacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAprobacionSupervisor(
    repositorio: RepositorioTeqRuta,
    alNavegarADetalleAuditoria: (String) -> Unit
) {
    val traslados by repositorio.traslados.collectAsState()
    var pestanaSeleccionada by remember { mutableIntStateOf(0) } // 0: Pendientes, 1: Desviaciones, 2: Historial

    val trasladosPendientes = traslados.filter { it.estado == EstadoTraslado.PENDIENTE_APROBACION }
    val trasladosConDesviacion = traslados.filter { it.porcentajeDesviacion > 10.0 }

    val trasladosMostrados = when (pestanaSeleccionada) {
        0 -> trasladosPendientes
        1 -> trasladosConDesviacion
        else -> traslados.filter { it.estado != EstadoTraslado.EN_CURSO }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Bandeja de Auditoría y Validación",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Supervisión de traslados en terreno, costos y detección de discrepancias.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        SecondaryTabRow(selectedTabIndex = pestanaSeleccionada) {
            Tab(
                selected = pestanaSeleccionada == 0,
                onClick = { pestanaSeleccionada = 0 },
                text = { Text("Pendientes (${trasladosPendientes.size})") }
            )
            Tab(
                selected = pestanaSeleccionada == 1,
                onClick = { pestanaSeleccionada = 1 },
                text = { Text("Desviaciones (${trasladosConDesviacion.size})") }
            )
            Tab(
                selected = pestanaSeleccionada == 2,
                onClick = { pestanaSeleccionada = 2 },
                text = { Text("Historial (${traslados.size})") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (trasladosMostrados.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay traslados en esta categoría.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(trasladosMostrados) { traslado ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (traslado.esDesviacionCritica && traslado.estado == EstadoTraslado.PENDIENTE_APROBACION)
                                Color(0xFFFFEBEE)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { alNavegarADetalleAuditoria(traslado.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = traslado.nombreTecnico,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                EtiquetaEstadoTraslado(estado = traslado.estado)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Destino: ${traslado.destino.nombre} (${traslado.destino.ciudad})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Fecha: ${traslado.horaInicioFormateada} · Móvil: ${traslado.patenteVehiculo}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Declarado: ${String.format("%.1f", traslado.distanciaDeclaradaKm)} km  (Oficial: ${traslado.distanciaOficialKm} km)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Costo Total: $${String.format("%,.0f", traslado.costoTotalCalculado)} CLP",
                                        fontSize = 12.sp,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            IndicadorDesviacion(
                                distanciaDeclaradaKm = traslado.distanciaDeclaradaKm,
                                distanciaOficialKm = traslado.distanciaOficialKm
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { alNavegarADetalleAuditoria(traslado.id) }
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ManageSearch, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Auditar y Dictaminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

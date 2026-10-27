package com.example.teqruta.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.teqruta.ui.componentes.EtiquetaEstadoSincronizacion
import com.example.teqruta.ui.componentes.EtiquetaEstadoTraslado
import com.example.teqruta.ui.componentes.IndicadorDesviacion

@Composable
fun PantallaDetalleAuditoria(
    idTraslado: String,
    repositorio: RepositorioTeqRuta,
    alFinalizarAuditoria: () -> Unit
) {
    val traslados by repositorio.traslados.collectAsState()
    val traslado = traslados.find { it.id == idTraslado }

    if (traslado == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Traslado no encontrado.")
        }
        return
    }

    var observacionesSupervisor by remember { mutableStateOf(traslado.observacionesSupervisor ?: "") }
    var mensajeAccion by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Auditoría de Traslado",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Técnico: ${traslado.nombreTecnico}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            EtiquetaEstadoTraslado(estado = traslado.estado)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comparativa de distancias
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Comparativa de Recorrido",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Destino visitado:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${traslado.destino.nombre} (${traslado.destino.ciudad})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Distancia Oficial Catastrada:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${traslado.distanciaOficialKm} km", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Distancia Declarada (Odómetro):", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${String.format("%.1f", traslado.distanciaDeclaradaKm)} km", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Diferencia neta detectada:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val diff = traslado.distanciaDeclaradaKm - traslado.distanciaOficialKm
                    Text(
                        text = "${if (diff >= 0) "+" else ""}${String.format("%.1f", diff)} km",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (traslado.esDesviacionCritica) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                IndicadorDesviacion(distanciaDeclaradaKm = traslado.distanciaDeclaradaKm, distanciaOficialKm = traslado.distanciaOficialKm)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Desglose de costos centralizados
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Auditoría de Costos Calculados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Vehículo y Combustible:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${traslado.patenteVehiculo} (${traslado.tipoCombustible.nombre})", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Rendimiento registrado:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${traslado.rendimientoKmPorLitro} km/litro", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tarifa Combustible aplicada:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$${String.format("%,.0f", traslado.precioCombustiblePorLitro)} CLP / litro", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Gasto Combustible estimado:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$${String.format("%,.0f", traslado.costoCombustibleCalculado)} CLP", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Gasto en Peajes declarado:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$${String.format("%,.0f", traslado.gastoPeajes)} CLP", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("COSTO TOTAL AUDITABLE:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "$${String.format("%,.0f", traslado.costoTotalCalculado)} CLP",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Observaciones del Técnico
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Declaración del Técnico en Terreno:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (traslado.observacionesTecnico.isNotBlank()) traslado.observacionesTecnico else "Sin observaciones registradas por el técnico.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                EtiquetaEstadoSincronizacion(estado = traslado.estadoSincronizacion)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = observacionesSupervisor,
            onValueChange = { observacionesSupervisor = it },
            label = { Text("Dictamen / Observaciones del Jefe de Servicio") },
            placeholder = { Text("Ej: Aprobado conforme desvío justificado por reparaciones viales...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

        if (traslado.validadoPor != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dictaminado por: ${traslado.validadoPor}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (mensajeAccion != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeAccion!!,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    repositorio.rechazarTraslado(
                        idTraslado = traslado.id,
                        observacionesSupervisor = observacionesSupervisor.ifBlank { "Rechazado por desviación sin justificación." }
                    )
                    mensajeAccion = "Traslado rechazado con observación."
                    alFinalizarAuditoria()
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Rechazar")
            }

            Button(
                onClick = {
                    repositorio.aprobarTraslado(
                        idTraslado = traslado.id,
                        observacionesSupervisor = observacionesSupervisor.ifBlank { "Aprobado sin reparos." }
                    )
                    mensajeAccion = "Traslado aprobado correctamente."
                    alFinalizarAuditoria()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Aprobar")
            }
        }
    }
}

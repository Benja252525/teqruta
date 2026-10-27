package com.example.teqruta.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqruta.datos.RepositorioTeqRuta
import com.example.teqruta.ui.componentes.IndicadorDesviacion

@Composable
fun PantallaFinalizarTraslado(
    idTraslado: String,
    repositorio: RepositorioTeqRuta,
    alFinalizarTrasladoExitoso: () -> Unit
) {
    val traslados by repositorio.traslados.collectAsState()
    val estaEnLinea by repositorio.estaEnLinea.collectAsState()
    val traslado = traslados.find { it.id == idTraslado }

    if (traslado == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Traslado no encontrado o ya finalizado.")
        }
        return
    }

    val odometroSugerido = traslado.odometroInicialKm + traslado.destino.distanciaOficialKmDesdeBase
    var textoOdometroFinal by remember { mutableStateOf(String.format("%.1f", odometroSugerido).replace(",", ".")) }
    var textoGastoPeajes by remember { mutableStateOf(traslado.destino.peajeOficialEstimadoIda.toInt().toString()) }
    var textoObservaciones by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val odometroFinal = textoOdometroFinal.toDoubleOrNull() ?: 0.0
    val distanciaDeclarada = if (odometroFinal >= traslado.odometroInicialKm) odometroFinal - traslado.odometroInicialKm else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Finalizar Traslado en Terreno",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Llegada a ${traslado.destino.nombre} (${traslado.destino.ciudad})",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta resumen del viaje
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Hora de Salida:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(traslado.horaInicioFormateada, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Odómetro de Salida:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${String.format("%.1f", traslado.odometroInicialKm)} km", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Distancia Oficial Teórica:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${traslado.distanciaOficialKm} km", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Vehículo y Combustible:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${traslado.patenteVehiculo} (${traslado.rendimientoKmPorLitro} km/l)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textoOdometroFinal,
            onValueChange = {
                textoOdometroFinal = it
                mensajeError = null
            },
            label = { Text("Kilometraje Final Odómetro (km)") },
            leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = mensajeError != null
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (distanciaDeclarada > 0) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Distancia Calculada:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(
                            text = "${String.format("%.1f", distanciaDeclarada)} km",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    IndicadorDesviacion(
                        distanciaDeclaradaKm = distanciaDeclarada,
                        distanciaOficialKm = traslado.distanciaOficialKm
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textoGastoPeajes,
            onValueChange = { textoGastoPeajes = it },
            label = { Text("Gasto en Peajes ($ CLP)") },
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("Monto abonado en peajes durante el traslado.") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = textoObservaciones,
            onValueChange = { textoObservaciones = it },
            label = { Text("Observaciones / Motivo de Visita") },
            placeholder = { Text("Ej: Mantenimiento autoclave, calibración desfibrilador, etc.") },
            leadingIcon = { Icon(Icons.Default.NoteAlt, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

        if (mensajeError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val odoFinal = textoOdometroFinal.toDoubleOrNull()
                if (odoFinal == null || odoFinal <= traslado.odometroInicialKm) {
                    mensajeError = "El odómetro final debe ser mayor al inicial (${traslado.odometroInicialKm} km)."
                    return@Button
                }

                val peajes = textoGastoPeajes.toDoubleOrNull() ?: 0.0

                repositorio.finalizarTraslado(
                    idTraslado = traslado.id,
                    odometroFinalKm = odoFinal,
                    gastoPeajes = peajes,
                    observaciones = textoObservaciones
                )
                alFinalizarTrasladoExitoso()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (estaEnLinea) "Guardar y Enviar a Supervisión" else "Guardar Localmente (Modo Offline)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

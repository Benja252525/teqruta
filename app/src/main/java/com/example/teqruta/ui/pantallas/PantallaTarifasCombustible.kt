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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqruta.datos.RepositorioTeqRuta
import kotlinx.coroutines.launch

@Composable
fun PantallaTarifasCombustible(
    repositorio: RepositorioTeqRuta
) {
    val tarifaCombustible by repositorio.tarifaCombustible.collectAsState()
    val estaEnLinea by repositorio.estaEnLinea.collectAsState()
    val alcanceCorrutina = rememberCoroutineScope()

    var estaActualizandoApi by remember { mutableStateOf(false) }
    var mensajeExitoApi by remember { mutableStateOf<String?>(null) }

    var edicionGasolina95 by remember { mutableStateOf(tarifaCombustible.precioGasolina95.toInt().toString()) }
    var edicionDiesel by remember { mutableStateOf(tarifaCombustible.precioDiesel.toInt().toString()) }
    var edicionManualAbierta by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Tarifas Centralizadas y Combustible",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Valores auditables para el cálculo automático de los traslados de TEQMED SpA.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de Precios Oficiales Vigentes
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tarifaCombustible.region,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.Default.LocalGasStation,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Gasolina 95 Oct.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "$${String.format("%,.0f", tarifaCombustible.precioGasolina95)} / L",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Diésel Grado A", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "$${String.format("%,.0f", tarifaCombustible.precioDiesel)} / L",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Fuente oficial: ${tarifaCombustible.fuenteOficial}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Última sincronización: ${tarifaCombustible.ultimaActualizacion}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de sincronización con API pública CNE
        Button(
            onClick = {
                alcanceCorrutina.launch {
                    estaActualizandoApi = true
                    val actualizada = repositorio.sincronizarTarifaDesdeApiPublica()
                    edicionGasolina95 = actualizada.precioGasolina95.toInt().toString()
                    edicionDiesel = actualizada.precioDiesel.toInt().toString()
                    estaActualizandoApi = false
                    mensajeExitoApi = "Tarifa regional actualizada exitosamente desde API pública CNE."
                }
            },
            enabled = estaEnLinea && !estaActualizandoApi,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estaActualizandoApi) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Consultando API CNE...")
            } else {
                Icon(Icons.Default.CloudSync, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Actualizar desde API Pública CNE")
            }
        }

        if (mensajeExitoApi != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = mensajeExitoApi!!,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ajuste manual por el Administrador
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ajuste Manual Administrativo",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { edicionManualAbierta = !edicionManualAbierta }) {
                        Icon(
                            imageVector = if (edicionManualAbierta) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                }

                if (edicionManualAbierta) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = edicionGasolina95,
                        onValueChange = { edicionGasolina95 = it },
                        label = { Text("Precio Gasolina 95 ($/Litro)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = edicionDiesel,
                        onValueChange = { edicionDiesel = it },
                        label = { Text("Precio Diésel ($/Litro)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val g95 = edicionGasolina95.toDoubleOrNull() ?: tarifaCombustible.precioGasolina95
                            val dsl = edicionDiesel.toDoubleOrNull() ?: tarifaCombustible.precioDiesel
                            repositorio.actualizarTarifaManual(g95, dsl)
                            mensajeExitoApi = "Tarifa fijada manualmente por la administración."
                            edicionManualAbierta = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Guardar Tarifa Centralizada")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Peajes oficiales de referencia
        Text(
            text = "Peajes Oficiales de Referencia (Rutas Biobío):",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        val tramosPeajes = listOf(
            "Ruta del Itata (Concepción - Chillán)" to "$4.900 CLP",
            "Autopista del Bosque / Ruta 5 (Los Ángeles)" to "$3.200 CLP",
            "Ruta de La Araucanía (Temuco)" to "$9.800 CLP",
            "Ruta Coronel - Arauco (Peaje Chivilingo)" to "$2.400 CLP"
        )

        tramosPeajes.forEach { (tramo, precio) ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(tramo, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(precio, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        }
    }
}

package com.example.teqruta.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.teqruta.modelo.Destino
import java.util.UUID

@Composable
fun PantallaGestionDestinos(
    repositorio: RepositorioTeqRuta
) {
    val destinos by repositorio.destinos.collectAsState()
    var dialogoAbierto by remember { mutableStateOf(false) }
    var destinoParaEditar by remember { mutableStateOf<Destino?>(null) }

    var nombre by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Hospital") }
    var distanciaOficial by remember { mutableStateOf("") }
    var peajeOficial by remember { mutableStateOf("") }

    fun abrirDialogo(dest: Destino? = null) {
        destinoParaEditar = dest
        if (dest != null) {
            nombre = dest.nombre
            ciudad = dest.ciudad
            direccion = dest.direccion
            tipo = dest.tipoInstitucion
            distanciaOficial = dest.distanciaOficialKmDesdeBase.toString()
            peajeOficial = dest.peajeOficialEstimadoIda.toInt().toString()
        } else {
            nombre = ""
            ciudad = "Concepción"
            direccion = ""
            tipo = "Clínica"
            distanciaOficial = "10.0"
            peajeOficial = "0"
        }
        dialogoAbierto = true
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { abrirDialogo(null) },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.AddLocation, contentDescription = "Agregar Destino")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Catastro de Clínicas y Hospitales",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Red asistencial atendida y distancias oficiales auditadas desde la sede Concepción.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(destinos) { dest ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = dest.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "${dest.tipoInstitucion} · ${dest.ciudad} (${dest.direccion})",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row {
                                    Text(
                                        text = "Distancia Oficial: ${dest.distanciaOficialKmDesdeBase} km",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (dest.peajeOficialEstimadoIda > 0) {
                                        Text(
                                            text = " · Peaje base: $${String.format("%,.0f", dest.peajeOficialEstimadoIda)}",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            IconButton(onClick = { abrirDialogo(dest) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { repositorio.eliminarDestino(dest.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (dialogoAbierto) {
        AlertDialog(
            onDismissRequest = { dialogoAbierto = false },
            title = {
                Text(if (destinoParaEditar == null) "Nuevo Centro Asistencial" else "Editar Destino")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre de la Institución") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = ciudad,
                        onValueChange = { ciudad = it },
                        label = { Text("Ciudad (ej. Concepción, Chillán)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = distanciaOficial,
                        onValueChange = { distanciaOficial = it },
                        label = { Text("Distancia Oficial desde Sede (km)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = peajeOficial,
                        onValueChange = { peajeOficial = it },
                        label = { Text("Peaje Oficial Estimado ($ CLP)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dist = distanciaOficial.toDoubleOrNull() ?: 10.0
                        val tolls = peajeOficial.toDoubleOrNull() ?: 0.0
                        if (destinoParaEditar == null) {
                            repositorio.agregarDestino(
                                Destino(
                                    id = UUID.randomUUID().toString(),
                                    nombre = nombre.ifBlank { "Nuevo Destino" },
                                    tipoInstitucion = tipo,
                                    ciudad = ciudad.ifBlank { "Concepción" },
                                    direccion = direccion,
                                    distanciaOficialKmDesdeBase = dist,
                                    peajeOficialEstimadoIda = tolls
                                )
                            )
                        } else {
                            repositorio.actualizarDestino(
                                destinoParaEditar!!.copy(
                                    nombre = nombre,
                                    ciudad = ciudad,
                                    direccion = direccion,
                                    tipoInstitucion = tipo,
                                    distanciaOficialKmDesdeBase = dist,
                                    peajeOficialEstimadoIda = tolls
                                )
                            )
                        }
                        dialogoAbierto = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogoAbierto = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

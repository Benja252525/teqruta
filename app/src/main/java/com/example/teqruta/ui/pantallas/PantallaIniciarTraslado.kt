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
import com.example.teqruta.modelo.Destino

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaIniciarTraslado(
    repositorio: RepositorioTeqRuta,
    alIniciarTrasladoExitoso: () -> Unit
) {
    val destinos by repositorio.destinos.collectAsState()
    val vehiculos by repositorio.vehiculos.collectAsState()
    val usuarioActual by repositorio.usuarioActual.collectAsState()
    val traslados by repositorio.traslados.collectAsState()

    val vehiculoAsignado = vehiculos.find { it.id == usuarioActual.idVehiculoAsignado } ?: vehiculos.firstOrNull()

    // Sugerir odómetro según último viaje
    val ultimoTraslado = traslados.firstOrNull { it.idTecnico == usuarioActual.id && it.odometroFinalKm != null }
    val odometroPorDefecto = ultimoTraslado?.odometroFinalKm?.toString() ?: "124623.0"

    var destinoSeleccionado by remember { mutableStateOf<Destino?>(destinos.firstOrNull()) }
    var puntoPartida by remember { mutableStateOf("Sede Central Concepción") }
    var textoOdometroInicial by remember { mutableStateOf(odometroPorDefecto) }
    var textoBusqueda by remember { mutableStateOf("") }
    var menuDesplegado by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val destinosFiltrados = destinos.filter {
        it.nombre.contains(textoBusqueda, ignoreCase = true) ||
        it.ciudad.contains(textoBusqueda, ignoreCase = true) ||
        it.tipoInstitucion.contains(textoBusqueda, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Registro Rápido de Salida",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Indique el centro de salud de destino y confirme el odómetro del móvil.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = puntoPartida,
            onValueChange = { puntoPartida = it },
            label = { Text("Punto de Partida") },
            leadingIcon = { Icon(Icons.Default.MyLocation, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Selector de Centro de Salud
        ExposedDropdownMenuBox(
            expanded = menuDesplegado,
            onExpandedChange = { menuDesplegado = !menuDesplegado }
        ) {
            OutlinedTextField(
                value = destinoSeleccionado?.let { "${it.nombre} (${it.ciudad})" } ?: "Seleccionar institución",
                onValueChange = {},
                readOnly = true,
                label = { Text("Centro Asistencial / Destino") },
                leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuDesplegado) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = menuDesplegado,
                onDismissRequest = { menuDesplegado = false }
            ) {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    placeholder = { Text("Buscar clínica, hospital o comuna...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true
                )

                destinosFiltrados.forEach { destino ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(text = destino.nombre, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${destino.tipoInstitucion} · ${destino.ciudad} (Oficial: ${destino.distanciaOficialKmDesdeBase} km)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            destinoSeleccionado = destino
                            menuDesplegado = false
                        }
                    )
                }
            }
        }

        // Datos auditados del destino
        destinoSeleccionado?.let { dest ->
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Distancia Oficial Auditada:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${dest.distanciaOficialKmDesdeBase} km",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (dest.peajeOficialEstimadoIda > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Peaje oficial estimado (ida):",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$${String.format("%,.0f", dest.peajeOficialEstimadoIda)} CLP",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta informativa del vehículo
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Vehículo: ${vehiculoAsignado?.marcaModelo ?: "Sin asignar"} (${vehiculoAsignado?.patente ?: "--"})",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${vehiculoAsignado?.tipo?.descripcion} · ${vehiculoAsignado?.tipoCombustible?.nombre}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textoOdometroInicial,
            onValueChange = {
                textoOdometroInicial = it
                mensajeError = null
            },
            label = { Text("Kilometraje Inicial Odómetro (km)") },
            leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = mensajeError != null,
            supportingText = {
                if (mensajeError != null) {
                    Text(text = mensajeError!!, color = MaterialTheme.colorScheme.error)
                } else {
                    Text("Ingrese el valor exacto del tablero del vehículo.")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val odometro = textoOdometroInicial.toDoubleOrNull()
                if (odometro == null || odometro <= 0) {
                    mensajeError = "Ingrese un kilometraje válido mayor a 0."
                    return@Button
                }
                if (destinoSeleccionado == null) {
                    mensajeError = "Debe seleccionar un destino asistencial."
                    return@Button
                }
                if (vehiculoAsignado == null) {
                    mensajeError = "Debe tener un móvil asignado para iniciar."
                    return@Button
                }

                repositorio.iniciarTraslado(
                    destino = destinoSeleccionado!!,
                    odometroInicialKm = odometro,
                    vehiculo = vehiculoAsignado,
                    puntoPartida = puntoPartida
                )
                alIniciarTrasladoExitoso()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Confirmar Salida e Iniciar Traslado", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

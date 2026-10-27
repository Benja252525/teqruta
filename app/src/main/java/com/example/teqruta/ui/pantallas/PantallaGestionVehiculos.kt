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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqruta.datos.RepositorioTeqRuta
import com.example.teqruta.modelo.TipoCombustible
import com.example.teqruta.modelo.TipoVehiculo
import com.example.teqruta.modelo.Vehiculo
import java.util.UUID

@Composable
fun PantallaGestionVehiculos(
    repositorio: RepositorioTeqRuta
) {
    val vehiculos by repositorio.vehiculos.collectAsState()
    var dialogoAbierto by remember { mutableStateOf(false) }
    var vehiculoParaEditar by remember { mutableStateOf<Vehiculo?>(null) }

    var patente by remember { mutableStateOf("") }
    var marcaModelo by remember { mutableStateOf("") }
    var tipoVehiculo by remember { mutableStateOf(TipoVehiculo.EMPRESA) }
    var tipoCombustible by remember { mutableStateOf(TipoCombustible.DIESEL) }
    var textoRendimiento by remember { mutableStateOf("") }
    var nombreTecnico by remember { mutableStateOf("") }

    fun abrirDialogo(veh: Vehiculo? = null) {
        vehiculoParaEditar = veh
        if (veh != null) {
            patente = veh.patente
            marcaModelo = veh.marcaModelo
            tipoVehiculo = veh.tipo
            tipoCombustible = veh.tipoCombustible
            textoRendimiento = veh.rendimientoKmPorLitro.toString()
            nombreTecnico = veh.nombreTecnicoPropietario ?: ""
        } else {
            patente = ""
            marcaModelo = ""
            tipoVehiculo = TipoVehiculo.EMPRESA
            tipoCombustible = TipoCombustible.DIESEL
            textoRendimiento = "14.5"
            nombreTecnico = ""
        }
        dialogoAbierto = true
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { abrirDialogo(null) },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Vehículo")
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
                text = "Gestión de Flota y Vehículos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Control de móviles corporativos TEQMED y particulares con rendimientos de combustible.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(vehiculos) { veh ->
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
                                        text = veh.marcaModelo,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (veh.tipo == TipoVehiculo.EMPRESA) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                                    ) {
                                        Text(
                                            text = if (veh.tipo == TipoVehiculo.EMPRESA) "Empresa" else "Particular",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (veh.tipo == TipoVehiculo.EMPRESA) Color(0xFF2E7D32) else Color(0xFFE65100),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "Patente: ${veh.patente} · ${veh.tipoCombustible.nombre}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "Rendimiento promedio: ${veh.rendimientoKmPorLitro} km/litro",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (veh.tipo == TipoVehiculo.PARTICULAR && !veh.nombreTecnicoPropietario.isNullOrBlank()) {
                                    Text(
                                        text = "Asignado a: ${veh.nombreTecnicoPropietario}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            IconButton(onClick = { abrirDialogo(veh) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { repositorio.eliminarVehiculo(veh.id) }) {
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
                Text(if (vehiculoParaEditar == null) "Nuevo Vehículo" else "Editar Vehículo")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = patente,
                        onValueChange = { patente = it.uppercase() },
                        label = { Text("Patente (ej. CJ-WP-82)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = marcaModelo,
                        onValueChange = { marcaModelo = it },
                        label = { Text("Marca y Modelo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = tipoVehiculo == TipoVehiculo.EMPRESA,
                            onClick = { tipoVehiculo = TipoVehiculo.EMPRESA }
                        )
                        Text("Empresa TEQMED", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(
                            selected = tipoVehiculo == TipoVehiculo.PARTICULAR,
                            onClick = { tipoVehiculo = TipoVehiculo.PARTICULAR }
                        )
                        Text("Particular", fontSize = 13.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = tipoCombustible == TipoCombustible.DIESEL,
                            onClick = { tipoCombustible = TipoCombustible.DIESEL }
                        )
                        Text("Diésel", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(
                            selected = tipoCombustible == TipoCombustible.GASOLINA_95,
                            onClick = { tipoCombustible = TipoCombustible.GASOLINA_95 }
                        )
                        Text("Gasolina 95", fontSize = 13.sp)
                    }

                    OutlinedTextField(
                        value = textoRendimiento,
                        onValueChange = { textoRendimiento = it },
                        label = { Text("Rendimiento (km/litro)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (tipoVehiculo == TipoVehiculo.PARTICULAR) {
                        OutlinedTextField(
                            value = nombreTecnico,
                            onValueChange = { nombreTecnico = it },
                            label = { Text("Nombre del Técnico Propietario") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rend = textoRendimiento.toDoubleOrNull() ?: 14.0
                        if (vehiculoParaEditar == null) {
                            repositorio.agregarVehiculo(
                                Vehiculo(
                                    id = UUID.randomUUID().toString(),
                                    patente = patente.ifBlank { "AA-BB-00" },
                                    marcaModelo = marcaModelo.ifBlank { "Vehículo" },
                                    tipo = tipoVehiculo,
                                    tipoCombustible = tipoCombustible,
                                    rendimientoKmPorLitro = rend,
                                    nombreTecnicoPropietario = if (tipoVehiculo == TipoVehiculo.PARTICULAR) nombreTecnico.ifBlank { null } else null
                                )
                            )
                        } else {
                            repositorio.actualizarVehiculo(
                                vehiculoParaEditar!!.copy(
                                    patente = patente,
                                    marcaModelo = marcaModelo,
                                    tipo = tipoVehiculo,
                                    tipoCombustible = tipoCombustible,
                                    rendimientoKmPorLitro = rend,
                                    nombreTecnicoPropietario = if (tipoVehiculo == TipoVehiculo.PARTICULAR) nombreTecnico.ifBlank { null } else null
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

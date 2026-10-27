package com.example.teqruta.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.teqruta.modelo.EstadoTraslado
import com.example.teqruta.modelo.TipoVehiculo
import com.example.teqruta.ui.componentes.TarjetaMetrica

@Composable
fun PantallaReporteRentabilidad(
    repositorio: RepositorioTeqRuta
) {
    val traslados by repositorio.traslados.collectAsState()
    val destinos by repositorio.destinos.collectAsState()

    var mensajeExportacion by remember { mutableStateOf<String?>(null) }

    val trasladosCompletados = traslados.filter { it.estado == EstadoTraslado.APROBADO || it.estado == EstadoTraslado.PENDIENTE_APROBACION }

    val gastoTotal = trasladosCompletados.sumOf { it.costoTotalCalculado }
    val totalKm = trasladosCompletados.sumOf { it.distanciaDeclaradaKm }
    val costoMedioPorVisita = if (trasladosCompletados.isNotEmpty()) gastoTotal / trasladosCompletados.size else 0.0

    // Desglose por tipo de vehículo
    val costoFlotaEmpresa = trasladosCompletados.filter { it.tipoVehiculo == TipoVehiculo.EMPRESA }.sumOf { it.costoTotalCalculado }
    val costoVehiculosParticulares = trasladosCompletados.filter { it.tipoVehiculo == TipoVehiculo.PARTICULAR }.sumOf { it.costoTotalCalculado }

    // Agrupación por cliente / destino
    val trasladosPorDestino = destinos.map { dest ->
        val viajesDestino = trasladosCompletados.filter { it.destino.id == dest.id }
        val costoAcumulado = viajesDestino.sumOf { it.costoTotalCalculado }
        Triple(dest, viajesDestino.size, costoAcumulado)
    }.filter { it.second > 0 }.sortedByDescending { it.third }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Costos y Rentabilidad por Contrato",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Información auditada de movilización para evaluación financiera y cobro a clientes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaMetrica(
                titulo = "Gasto Total Movilización",
                valor = "$${String.format("%,.0f", gastoTotal)}",
                subtitulo = "Combustible y peajes",
                icono = Icons.Default.AttachMoney,
                colorIcono = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )
            TarjetaMetrica(
                titulo = "Costo Medio / Visita",
                valor = "$${String.format("%,.0f", costoMedioPorVisita)}",
                subtitulo = "${trasladosCompletados.size} visitas registradas",
                icono = Icons.Default.Analytics,
                colorIcono = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Distribución por flota
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Distribución del Costo por Flota:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Vehículos Empresa (TEQMED):", fontSize = 12.sp)
                    Text("$${String.format("%,.0f", costoFlotaEmpresa)} CLP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Reembolso a Técnicos (Particulares):", fontSize = 12.sp)
                    Text("$${String.format("%,.0f", costoVehiculosParticulares)} CLP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Detalle de Costos por Institución de Salud:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(trasladosPorDestino) { (dest, cantidadVisitas, costoAcumulado) ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = dest.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${dest.ciudad} · ${cantidadVisitas} visita(s) técnica(s)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$${String.format("%,.0f", costoAcumulado)} CLP",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "Dist. base: ${dest.distanciaOficialKmDesdeBase} km",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (mensajeExportacion != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = mensajeExportacion!!,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                mensajeExportacion = "Reporte preparado para exportar a la intranet y ERP contable."
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Exportar Reporte para Facturación ERP", fontWeight = FontWeight.Bold)
        }
    }
}

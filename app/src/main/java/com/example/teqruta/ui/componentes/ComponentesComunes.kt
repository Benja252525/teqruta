package com.example.teqruta.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqruta.modelo.EstadoSincronizacion
import com.example.teqruta.modelo.EstadoTraslado
import com.example.teqruta.modelo.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperiorTeqRuta(
    titulo: String,
    puedeRetroceder: Boolean = false,
    alRetroceder: () -> Unit = {},
    estaEnLinea: Boolean,
    cantidadPendientesSincronizar: Int,
    usuarioActual: Usuario?,
    alAlternarModoConexion: () -> Unit,
    alSincronizar: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                if (usuarioActual != null) {
                    Text(
                        text = "${usuarioActual.nombre} (${usuarioActual.rol.nombreVisible})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        navigationIcon = {
            if (puedeRetroceder) {
                IconButton(onClick = alRetroceder) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 12.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            // Indicador / Botón de Conectividad (En línea vs Offline)
            FilterChip(
                selected = estaEnLinea,
                onClick = alAlternarModoConexion,
                label = {
                    Text(
                        text = if (estaEnLinea) "En línea" else "Offline",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (estaEnLinea) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2E7D32),
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White,
                    containerColor = Color(0xFFC62828),
                    labelColor = Color.White,
                    iconColor = Color.White
                ),
                modifier = Modifier.padding(end = 4.dp)
            )

            // Contador de sincronizaciones pendientes
            BadgedBox(
                badge = {
                    if (cantidadPendientesSincronizar > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ) {
                            Text("$cantidadPendientesSincronizar")
                        }
                    }
                },
                modifier = Modifier.padding(end = 4.dp)
            ) {
                IconButton(onClick = alSincronizar) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sincronizar",
                        tint = if (cantidadPendientesSincronizar > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Cerrar sesión
            IconButton(onClick = alCerrarSesion) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar sesión"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    )
}

@Composable
fun EtiquetaEstadoSincronizacion(estado: EstadoSincronizacion) {
    val estaSincronizado = estado == EstadoSincronizacion.SINCRONIZADO
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (estaSincronizado) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = if (estaSincronizado) Icons.Default.CloudDone else Icons.Default.CloudOff,
                contentDescription = null,
                tint = if (estaSincronizado) Color(0xFF2E7D32) else Color(0xFFE65100),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = estado.etiqueta,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (estaSincronizado) Color(0xFF1B5E20) else Color(0xFFBF360C)
            )
        }
    }
}

@Composable
fun EtiquetaEstadoTraslado(estado: EstadoTraslado) {
    val (fondo, textoColor) = when (estado) {
        EstadoTraslado.EN_CURSO -> Color(0xFFE3F2FD) to Color(0xFF0D47A1)
        EstadoTraslado.PENDIENTE_APROBACION -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
        EstadoTraslado.APROBADO -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
        EstadoTraslado.RECHAZADO -> Color(0xFFFFEBEE) to Color(0xFFB71C1C)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = fondo
    ) {
        Text(
            text = estado.etiqueta,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textoColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun IndicadorDesviacion(
    distanciaDeclaradaKm: Double,
    distanciaOficialKm: Double,
    modifier: Modifier = Modifier
) {
    val diferencia = distanciaDeclaradaKm - distanciaOficialKm
    val porcentaje = if (distanciaOficialKm > 0) (diferencia / distanciaOficialKm) * 100 else 0.0

    val (color, mensaje) = when {
        porcentaje > 15.0 -> Color(0xFFD32F2F) to "Desviación Crítica (+${String.format("%.1f", porcentaje)}%)"
        porcentaje > 5.0 -> Color(0xFFF57C00) to "Desviación Moderada (+${String.format("%.1f", porcentaje)}%)"
        porcentaje < -5.0 -> Color(0xFF1976D2) to "Menor a oficial (${String.format("%.1f", porcentaje)}%)"
        else -> Color(0xFF388E3C) to "Conforme con oficial (±${String.format("%.1f", kotlin.math.abs(porcentaje))}%)"
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = mensaje,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TarjetaMetrica(
    titulo: String,
    valor: String,
    subtitulo: String? = null,
    icono: ImageVector,
    colorIcono: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorIcono.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icono, contentDescription = null, tint = colorIcono)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = titulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (subtitulo != null) {
                    Text(text = subtitulo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

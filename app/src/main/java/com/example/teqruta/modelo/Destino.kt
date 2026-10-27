package com.example.teqruta.modelo

/**
 * Representa un centro asistencial, clínica u hospital atendido por TEQMED SpA.
 */
data class Destino(
    val id: String,
    val nombre: String,
    val tipoInstitucion: String, // Hospital, Clínica, Centro de Diálisis
    val ciudad: String,          // Concepción, Chillán, Los Ángeles, Temuco
    val direccion: String,
    val distanciaOficialKmDesdeBase: Double, // Distancia teórica auditada desde la sede Concepción
    val peajeOficialEstimadoIda: Double       // Costo oficial de peajes en el tramo
)

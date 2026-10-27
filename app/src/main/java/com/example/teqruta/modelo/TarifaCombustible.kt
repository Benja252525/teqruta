package com.example.teqruta.modelo

/**
 * Representa las tarifas oficiales centralizadas de combustible vigentes en la Región del Biobío.
 */
data class TarifaCombustible(
    val region: String = "Región del Biobío",
    val precioGasolina95: Double = 1315.0, // CLP por litro
    val precioDiesel: Double = 1045.0,     // CLP por litro
    val ultimaActualizacion: String = "22/10/2026 08:30",
    val fuenteOficial: String = "Comisión Nacional de Energía (CNE - BencinaEnLínea)"
)

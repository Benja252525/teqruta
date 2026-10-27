# TeqRuta - TEQMED SpA

Aplicación móvil Android nativa (Kotlin + Jetpack Compose) para el **control y auditoría de traslados en terreno** de técnicos de servicio de equipamiento médico en la Región del Biobío, Chile.

## Problema que Resuelve

TEQMED SpA mantiene equipos médicos en hospitales y clínicas de Concepción, Chillán, Los Ángeles y Temuco. Los técnicos registraban combustible, peajes y kilometraje de forma manual en libretas a fin de mes, generando discrepancias entre lo declarado y lo real. La empresa no conocía el costo real de movilización por cliente/contrato.

## Funcionalidades Principales

| Módulo | Funcionalidad |
|--------|--------------|
| **Técnico en Terreno** | Registro rápido de salida/llegada con odómetro, destino y peajes |
| **Modo Offline** | Opera hasta 8 horas sin conexión, sincroniza automáticamente al reconectar |
| **Auditoría** | Compara km declarados vs. distancia oficial con semáforo de desviaciones |
| **Supervisión** | Aprobación/rechazo de traslados por el Jefe de Servicio |
| **Finanzas** | Reportes de costos por cliente/contrato para facturación |
| **Administración** | Gestión de catálogos: destinos, vehículos, tarifas CNE |

## Stack Tecnológico

- **Lenguaje:** Kotlin 2.2.10
- **UI:** Jetpack Compose + Material 3
- **Build:** Gradle 9.6.0 + AGP 9.4.1
- **Min SDK:** 24 (Android 7.0+)
- **Target SDK:** 37

## Estructura del Proyecto

```
app/src/main/java/com/example/teqruta/
├── modelo/           # Entidades: Usuario, Vehiculo, Destino, Traslado, TarifaCombustible
├── datos/            # Repositorio central de lógica de negocio y datos de prueba
└── ui/
    ├── componentes/  # Componentes reutilizables (barra superior, indicadores)
    ├── navegacion/   # Definición de pantallas/rutas
    └── pantallas/    # 12 pantallas completas del sistema
```

## Pantallas

1. **PantallaLogin** — Autenticación y selección de perfil
2. **PantallaPanelTecnico** — Dashboard del técnico con estado del día
3. **PantallaSeleccionVehiculo** — Asignación de vehículo de jornada
4. **PantallaIniciarTraslado** — Registro rápido de salida
5. **PantallaFinalizarTraslado** — Registro de llegada y gastos
6. **PantallaHistorialTraslados** — Historial con estados de sincronización
7. **PantallaAprobacionSupervisor** — Bandeja de auditoría
8. **PantallaDetalleAuditoria** — Comparativa y dictamen
9. **PantallaReporteRentabilidad** — Costos por contrato
10. **PantallaTarifasCombustible** — Tarifas y API CNE
11. **PantallaGestionDestinos** — ABM de centros asistenciales
12. **PantallaGestionVehiculos** — ABM de flota vehicular

## Compilación

```bash
./gradlew assembleDebug
```

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Autor

Proyecto desarrollado para la asignatura DSY1105 — TEQMED SpA.

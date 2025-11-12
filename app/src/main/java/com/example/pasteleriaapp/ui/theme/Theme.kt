package com.example.pasteleriaapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- TU PALETA DE COLORES CLAROS ---
private val LightColorScheme = lightColorScheme(
    primary = TituloMain,              // El rosa principal para botones, etc.
    onPrimary = Surface,               // Texto/iconos sobre el rosa (Blanco)
    primaryContainer = PastelStrawberry, // Contenedor rosa pálido
    onPrimaryContainer = Ink,          // Texto sobre el contenedor rosa (Negro)

    secondary = TituloSecondary,       // El verde/menta secundario
    onSecondary = Surface,             // Texto sobre el verde (Blanco)
    secondaryContainer = PastelMint,   // Contenedor menta pálido
    onSecondaryContainer = Ink,        // Texto sobre el contenedor menta (Negro)

    tertiary = TituloTertiary,         // El dorado terciario
    onTertiary = Surface,              // Texto sobre el dorado (Blanco)

    background = Surface,              // Fondo principal de la app (Blanco)
    onBackground = Ink,                // Texto sobre el fondo (Negro)

    surface = Surface,                 // Color de Cards, TopAppBar (Blanco)
    onSurface = Ink,                   // Texto principal sobre las superficies (Negro)

    surfaceVariant = SurfaceAlt,       // Fondo de OutlinedTextField (Blanco Hueso)
    onSurfaceVariant = InkMuted,       // Texto secundario, bordes (Gris)

    outline = InkMuted                 // Bordes (Gris)
)

// --- TU PALETA DE COLORES OSCUROS ---
private val DarkColorScheme = darkColorScheme(
    primary = DarkTituloMain,          // Rosa brillante para modo oscuro
    onPrimary = DarkInk,               // Texto claro sobre el rosa
    primaryContainer = TituloMain,     // Contenedor rosa (el color original)
    onPrimaryContainer = DarkInk,      // Texto claro

    secondary = TituloSecondary,       // Verde/menta
    onSecondary = DarkInk,             // Texto claro
    secondaryContainer = TituloSecondary.copy(alpha = 0.3f), // Verde oscuro traslúcido
    onSecondaryContainer = PastelMint, // Texto menta pálido

    tertiary = TituloTertiary,         // Dorado
    onTertiary = DarkInk,              // Texto claro

    background = DarkSurface,          // Fondo oscuro (Casi negro)
    onBackground = DarkInk,            // Texto principal (Casi blanco)

    surface = DarkSurface,             // Color de Cards, TopAppBar (Casi negro)
    onSurface = DarkInk,               // Texto principal (Casi blanco)

    surfaceVariant = DarkSurfaceAlt,   // Fondo de OutlinedTextField (Gris oscuro)
    onSurfaceVariant = InkMuted,       // Texto secundario (Gris claro)

    outline = InkMuted                 // Bordes (Gris claro)
)

@Composable
fun PasteleriaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
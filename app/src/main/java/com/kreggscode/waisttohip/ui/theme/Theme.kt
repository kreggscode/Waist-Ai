package com.kreggscode.waisttohip.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = Color.White,
    primaryContainer = CoralLight,
    onPrimaryContainer = CoralDark,
    
    secondary = Mint,
    onSecondary = Color.Black,
    secondaryContainer = MintLight,
    onSecondaryContainer = MintDark,
    
    tertiary = Lavender,
    onTertiary = Color.Black,
    tertiaryContainer = LavenderLight,
    onTertiaryContainer = LavenderDark,
    
    background = Vanilla,
    onBackground = TextPrimary,
    
    surface = Color.White,
    onSurface = TextPrimary,
    surfaceVariant = Vanilla,
    onSurfaceVariant = TextSecondary,
    
    error = HighRisk,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFF0F0F0),
    
    scrim = Color(0x66000000)
)

private val DarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4A148C),
    onPrimaryContainer = Color(0xFFE1BEE7),
    
    secondary = NeonCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF006064),
    onSecondaryContainer = Color(0xFFB2EBF2),
    
    tertiary = NeonPink,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF880E4F),
    onTertiaryContainer = Color(0xFFF8BBD0),
    
    background = Color(0xFF121212),
    onBackground = Color(0xFFE8E8E8),
    
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB8B8B8),
    
    error = HighRisk,
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    outline = Color(0xFF5A5A5A),
    outlineVariant = Color(0xFF3A3A3A),
    
    scrim = Color(0x99000000)
)

@Composable
fun CurveAndFuelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    val systemUiController = rememberSystemUiController()
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Make the app edge-to-edge
            WindowCompat.setDecorFitsSystemWindows(window, false)
            
            // Set status bar and navigation bar colors to transparent
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = !darkTheme
            )
            systemUiController.setNavigationBarColor(
                color = Color.Transparent,
                darkIcons = !darkTheme,
                navigationBarContrastEnforced = false
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

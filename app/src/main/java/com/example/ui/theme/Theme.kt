package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OceanBlueDark,
    onPrimary = OceanOnBlueDark,
    primaryContainer = OceanBlueContainerDark,
    onPrimaryContainer = OceanOnBlueContainerDark,
    secondary = SecondaryBlueDark,
    onSecondary = OnSecondaryBlueDark,
    secondaryContainer = SecondaryBlueContainerDark,
    tertiary = CoralAccentDark,
    onTertiary = OnCoralAccentDark,
    tertiaryContainer = CoralContainerDark,
    background = OceanBackgroundDark,
    surface = OceanSurfaceDark,
    onBackground = OceanOnBackgroundDark,
    onSurface = OceanOnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = OceanBlueLight,
    onPrimary = OceanOnBlueLight,
    primaryContainer = OceanBlueContainer,
    onPrimaryContainer = OceanOnBlueContainer,
    secondary = SecondaryBlueLight,
    onSecondary = OnSecondaryBlueLight,
    secondaryContainer = SecondaryBlueContainer,
    tertiary = CoralAccentLight,
    onTertiary = OnCoralAccentLight,
    tertiaryContainer = CoralContainer,
    background = OceanBackgroundLight,
    surface = OceanSurfaceLight,
    onBackground = OceanOnBackgroundLight,
    onSurface = OceanOnSurfaceLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    // We default dynamicColor to false to maintain the specific brand identity of IkanKu (Deep Ocean)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Force LightColorScheme to keep 100% visual styling consistency with the original web design (always light mode)
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        androidx.compose.material3.Surface(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            color = colorScheme.background,
            content = content
        )
    }
}

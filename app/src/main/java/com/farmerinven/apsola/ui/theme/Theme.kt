package com.farmerinven.apsola.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGold,
    secondary = SecondaryOrange,
    tertiary = InfoBlue,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF2C2C2C),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = StatusRed
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGold,
    secondary = SecondaryOrange,
    tertiary = InfoBlue,
    background = Color(0xFFFFFBFE),
    surface = CardWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextGray,
    onSurface = TextGray,
    error = StatusRed
)

private val FarmerColorScheme = lightColorScheme(
    primary = PrimaryGold,
    secondary = SecondaryOrange,
    tertiary = InfoBlue,
    background = BackgroundBeige,
    surface = CardWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextGray,
    onSurface = TextGray,
    error = StatusRed
)

@Composable
fun FarmerInventoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeMode: String = "light",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        "dark" -> DarkColorScheme
        "farmer" -> FarmerColorScheme
        "light" -> LightColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
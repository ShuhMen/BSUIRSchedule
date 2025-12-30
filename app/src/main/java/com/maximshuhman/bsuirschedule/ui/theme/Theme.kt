package com.maximshuhman.bsuirschedule.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.maximshuhman.bsuirschedule.ApplicationThemes
import com.maximshuhman.bsuirschedule.R

private val mulishFontFamily = FontFamily(
    Font(R.font.mulish_regular),
    Font(R.font.mulish_medium, FontWeight.Medium),
    Font(R.font.mulish_semi_bold, FontWeight.SemiBold),
    Font(R.font.mulish_bold, FontWeight.Bold)
)

val MulishTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = mulishFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)


private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    secondary = DarkColors.Gray,
    tertiary = Blue,
    background = Black,
    onBackground = White,
    onSecondary = DarkColors.DarkGray,
    onPrimary = White,
    outlineVariant = DarkColors.dividerColor,
    surface = DarkColors.Gray,
    onTertiary = White,
    onSurface = DarkColors.DarkGray,
)

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    secondary = LightColors.Gray,
    tertiary = Blue,
    background = White,
    onBackground = Black,
    onSecondary = LightColors.DarkGray,
    onPrimary = White,
    outlineVariant = LightColors.dividerColor,
    surface = Blue,
    onTertiary = White,
    onSurface = LightColors.DarkGray,
)


private val PancakeColorScheme = lightColorScheme(
    primary = Pancake,
    secondary = PancakesColors.Gray,
    tertiary = Pancake,
    background = PancakesColors.background,
    onBackground = Black,
    onSecondary = PancakesColors.DarkGray,
    onPrimary = White,
    outlineVariant = PancakesColors.dividerColor,
    surface = Pancake,
    onTertiary = White,
    onSurface = PancakesColors.DarkGray,
)


@Composable
fun BSUIRScheduleTheme(
    theme: ApplicationThemes = ApplicationThemes.SystemTheme,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        ApplicationThemes.SystemTheme -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
        ApplicationThemes.DarkTheme -> DarkColorScheme
        ApplicationThemes.LightTheme -> LightColorScheme
        ApplicationThemes.PancakesTheme -> PancakeColorScheme
    }

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, view)
        insetsController.isAppearanceLightStatusBars = false

    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = MulishTypography,
        content = content
    )
}

fun Color.isLight(): Boolean {
    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue)
    return luminance > 0.5
}
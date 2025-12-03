package com.erdemyesilcicek.contactapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.erdemyesilcicek.contactapp.constants.AppColors

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.Surface,
    secondary = AppColors.TextSecondary,
    background = AppColors.Background,
    surface = AppColors.Surface,
    onBackground = AppColors.TextPrimary,
    onSurface = AppColors.TextPrimary,
    error = AppColors.Error,
    onError = AppColors.Surface
)

@Composable
fun ContactAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
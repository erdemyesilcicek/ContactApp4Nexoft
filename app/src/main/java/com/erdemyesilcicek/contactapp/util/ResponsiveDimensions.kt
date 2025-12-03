package com.erdemyesilcicek.contactapp.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Responsive Dimension Provider
 * Ekran boyutuna göre dinamik olarak ölçeklenen değerler sağlar
 */

data class WindowSize(
    val width: WindowType,
    val height: WindowType
)

enum class WindowType {
    Compact,  // < 600dp (Telefonlar)
    Medium,   // 600dp - 840dp (Küçük tabletler, katlanabilir cihazlar)
    Expanded  // > 840dp (Büyük tabletler)
}

data class Dimensions(
    // Screen Padding
    val screenHorizontalPadding: Dp,
    val screenVerticalPadding: Dp,
    
    // Padding
    val paddingXSmall: Dp,
    val paddingSmall: Dp,
    val paddingMedium: Dp,
    val paddingLarge: Dp,
    val paddingXLarge: Dp,
    val paddingXXLarge: Dp,
    
    // Avatar Sizes
    val avatarSizeSmall: Dp,
    val avatarSizeMedium: Dp,
    val avatarSizeLarge: Dp,
    
    // Icon Sizes
    val iconSizeSmall: Dp,
    val iconSizeMedium: Dp,
    val iconSizeLarge: Dp,
    val iconSizeXLarge: Dp,
    
    // Search Bar
    val searchBarHeight: Dp,
    val searchBarCornerRadius: Dp,
    val searchIconSize: Dp,
    
    // Text Field
    val textFieldHeight: Dp,
    val textFieldCornerRadius: Dp,
    
    // Button
    val buttonHeight: Dp,
    val buttonCornerRadius: Dp,
    
    // Card
    val cardCornerRadius: Dp,
    
    // Corner Radius
    val cornerRadiusSmall: Dp,
    val cornerRadiusMedium: Dp,
    val cornerRadiusLarge: Dp,
    
    // Elevation
    val elevationSmall: Dp,
    val elevationMedium: Dp,
    
    // Divider
    val dividerThickness: Dp,
    
    // Contact Item
    val contactItemHeight: Dp,
    val contactItemAvatarSize: Dp,
    
    // Success Check
    val successCheckSize: Dp,
    
    // Bottom Sheet
    val bottomSheetCornerRadius: Dp,
    val bottomSheetItemHeight: Dp,
    
    // TopBar
    val topBarHeight: Dp,
    val topBarAddButtonSize: Dp,
    
    // Font Sizes
    val fontSizeSmall: TextUnit,
    val fontSizeMedium: TextUnit,
    val fontSizeNormal: TextUnit,
    val fontSizeLarge: TextUnit,
    val fontSizeXLarge: TextUnit,
    val fontSizeXXLarge: TextUnit,
    val fontSizeTitle: TextUnit
)

// Compact (Telefon) boyutları
private val compactDimensions = Dimensions(
    screenHorizontalPadding = 16.dp,
    screenVerticalPadding = 16.dp,
    
    paddingXSmall = 4.dp,
    paddingSmall = 8.dp,
    paddingMedium = 16.dp,
    paddingLarge = 24.dp,
    paddingXLarge = 32.dp,
    paddingXXLarge = 48.dp,
    
    avatarSizeSmall = 40.dp,
    avatarSizeMedium = 80.dp,
    avatarSizeLarge = 100.dp,
    
    iconSizeSmall = 16.dp,
    iconSizeMedium = 24.dp,
    iconSizeLarge = 28.dp,
    iconSizeXLarge = 48.dp,
    
    searchBarHeight = 36.dp,
    searchBarCornerRadius = 10.dp,
    searchIconSize = 16.dp,
    
    textFieldHeight = 52.dp,
    textFieldCornerRadius = 8.dp,
    
    buttonHeight = 50.dp,
    buttonCornerRadius = 12.dp,
    
    cardCornerRadius = 12.dp,
    
    cornerRadiusSmall = 8.dp,
    cornerRadiusMedium = 12.dp,
    cornerRadiusLarge = 16.dp,
    
    elevationSmall = 2.dp,
    elevationMedium = 4.dp,
    
    dividerThickness = 0.5.dp,
    
    contactItemHeight = 64.dp,
    contactItemAvatarSize = 40.dp,
    
    successCheckSize = 120.dp,
    
    bottomSheetCornerRadius = 16.dp,
    bottomSheetItemHeight = 56.dp,
    
    topBarHeight = 56.dp,
    topBarAddButtonSize = 28.dp,
    
    fontSizeSmall = 12.sp,
    fontSizeMedium = 14.sp,
    fontSizeNormal = 16.sp,
    fontSizeLarge = 17.sp,
    fontSizeXLarge = 20.sp,
    fontSizeXXLarge = 28.sp,
    fontSizeTitle = 34.sp
)

// Medium (Küçük tablet) boyutları
private val mediumDimensions = Dimensions(
    screenHorizontalPadding = 24.dp,
    screenVerticalPadding = 24.dp,
    
    paddingXSmall = 6.dp,
    paddingSmall = 12.dp,
    paddingMedium = 20.dp,
    paddingLarge = 28.dp,
    paddingXLarge = 40.dp,
    paddingXXLarge = 56.dp,
    
    avatarSizeSmall = 48.dp,
    avatarSizeMedium = 96.dp,
    avatarSizeLarge = 120.dp,
    
    iconSizeSmall = 20.dp,
    iconSizeMedium = 28.dp,
    iconSizeLarge = 32.dp,
    iconSizeXLarge = 56.dp,
    
    searchBarHeight = 44.dp,
    searchBarCornerRadius = 12.dp,
    searchIconSize = 20.dp,
    
    textFieldHeight = 60.dp,
    textFieldCornerRadius = 10.dp,
    
    buttonHeight = 56.dp,
    buttonCornerRadius = 14.dp,
    
    cardCornerRadius = 14.dp,
    
    cornerRadiusSmall = 10.dp,
    cornerRadiusMedium = 14.dp,
    cornerRadiusLarge = 20.dp,
    
    elevationSmall = 3.dp,
    elevationMedium = 6.dp,
    
    dividerThickness = 0.5.dp,
    
    contactItemHeight = 72.dp,
    contactItemAvatarSize = 48.dp,
    
    successCheckSize = 140.dp,
    
    bottomSheetCornerRadius = 20.dp,
    bottomSheetItemHeight = 64.dp,
    
    topBarHeight = 64.dp,
    topBarAddButtonSize = 32.dp,
    
    fontSizeSmall = 14.sp,
    fontSizeMedium = 16.sp,
    fontSizeNormal = 18.sp,
    fontSizeLarge = 19.sp,
    fontSizeXLarge = 24.sp,
    fontSizeXXLarge = 32.sp,
    fontSizeTitle = 40.sp
)

// Expanded (Büyük tablet) boyutları
private val expandedDimensions = Dimensions(
    screenHorizontalPadding = 32.dp,
    screenVerticalPadding = 32.dp,
    
    paddingXSmall = 8.dp,
    paddingSmall = 16.dp,
    paddingMedium = 24.dp,
    paddingLarge = 32.dp,
    paddingXLarge = 48.dp,
    paddingXXLarge = 64.dp,
    
    avatarSizeSmall = 56.dp,
    avatarSizeMedium = 112.dp,
    avatarSizeLarge = 140.dp,
    
    iconSizeSmall = 24.dp,
    iconSizeMedium = 32.dp,
    iconSizeLarge = 40.dp,
    iconSizeXLarge = 64.dp,
    
    searchBarHeight = 52.dp,
    searchBarCornerRadius = 14.dp,
    searchIconSize = 24.dp,
    
    textFieldHeight = 68.dp,
    textFieldCornerRadius = 12.dp,
    
    buttonHeight = 64.dp,
    buttonCornerRadius = 16.dp,
    
    cardCornerRadius = 16.dp,
    
    cornerRadiusSmall = 12.dp,
    cornerRadiusMedium = 16.dp,
    cornerRadiusLarge = 24.dp,
    
    elevationSmall = 4.dp,
    elevationMedium = 8.dp,
    
    dividerThickness = 1.dp,
    
    contactItemHeight = 80.dp,
    contactItemAvatarSize = 56.dp,
    
    successCheckSize = 160.dp,
    
    bottomSheetCornerRadius = 24.dp,
    bottomSheetItemHeight = 72.dp,
    
    topBarHeight = 72.dp,
    topBarAddButtonSize = 36.dp,
    
    fontSizeSmall = 16.sp,
    fontSizeMedium = 18.sp,
    fontSizeNormal = 20.sp,
    fontSizeLarge = 22.sp,
    fontSizeXLarge = 28.sp,
    fontSizeXXLarge = 36.sp,
    fontSizeTitle = 48.sp
)

val LocalDimensions = compositionLocalOf { compactDimensions }

@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    return WindowSize(
        width = when {
            screenWidth < 600.dp -> WindowType.Compact
            screenWidth < 840.dp -> WindowType.Medium
            else -> WindowType.Expanded
        },
        height = when {
            screenHeight < 480.dp -> WindowType.Compact
            screenHeight < 900.dp -> WindowType.Medium
            else -> WindowType.Expanded
        }
    )
}

@Composable
fun ProvideAppDimensions(content: @Composable () -> Unit) {
    val windowSize = rememberWindowSize()
    
    val dimensions = remember(windowSize) {
        when (windowSize.width) {
            WindowType.Compact -> compactDimensions
            WindowType.Medium -> mediumDimensions
            WindowType.Expanded -> expandedDimensions
        }
    }
    
    CompositionLocalProvider(LocalDimensions provides dimensions) {
        content()
    }
}

/**
 * Dimensions'a kolay erişim için extension
 */
object AppDimens {
    val current: Dimensions
        @Composable
        get() = LocalDimensions.current
}

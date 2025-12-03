package com.erdemyesilcicek.contactapp.presentation.screens.success

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.erdemyesilcicek.contactapp.constants.AppAssets
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.util.AppDimens
import kotlinx.coroutines.delay

@Composable
fun SuccessScreen(
    onNavigateToContactList: () -> Unit
) {
    val dimens = AppDimens.current
    
    // Lottie animation composition
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(AppAssets.SUCCESS_ANIMATION)
    )
    
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )
    
    // Auto-navigate after delay
    LaunchedEffect(Unit) {
        delay(AppAssets.SUCCESS_ANIMATION_DURATION)
        onNavigateToContactList()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lottie Animation or Fallback
            if (composition != null) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(dimens.successCheckSize)
                )
            } else {
                // Fallback if Lottie animation is not loaded
                SuccessCheckFallback()
            }
            
            Spacer(modifier = Modifier.height(dimens.paddingLarge))
            
            Text(
                text = AppStrings.ALL_DONE,
                fontSize = dimens.fontSizeXXLarge,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(dimens.paddingSmall))
            
            Text(
                text = AppStrings.CONTACT_SAVED,
                fontSize = dimens.fontSizeMedium,
                fontWeight = FontWeight.Normal,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SuccessCheckFallback() {
    val dimens = AppDimens.current
    
    Box(
        modifier = Modifier
            .size(dimens.successCheckSize)
            .clip(CircleShape)
            .background(AppColors.Success),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Success",
            modifier = Modifier.size(dimens.iconSizeXLarge),
            tint = AppColors.Surface
        )
    }
}

package com.erdemyesilcicek.contactapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.util.AppDimens

@Composable
fun ContactSectionHeader(
    letter: Char,
    modifier: Modifier = Modifier
) {
    val dimens = AppDimens.current
    
    Text(
        text = letter.toString(),
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.Background)
            .padding(
                horizontal = dimens.screenHorizontalPadding,
                vertical = dimens.paddingSmall
            ),
        fontSize = dimens.fontSizeMedium,
        fontWeight = FontWeight.Medium,
        color = AppColors.TextSecondary
    )
}

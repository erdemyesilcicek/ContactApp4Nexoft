package com.erdemyesilcicek.contactapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.util.AppDimens

@Composable
fun ContactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    val dimens = AppDimens.current
    val isPhoneField = placeholder == AppStrings.PHONE_NUMBER
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.textFieldHeight)
            .clip(RoundedCornerShape(dimens.searchBarCornerRadius))
            .background(AppColors.SearchBarBackground)
            .padding(horizontal = dimens.paddingMedium),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = TextStyle(
                    fontSize = dimens.fontSizeLarge,
                    color = AppColors.SearchPlaceholder
                )
            )
        }
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = dimens.fontSizeLarge,
                color = AppColors.TextPrimary
            ),
            singleLine = true,
            cursorBrush = SolidColor(AppColors.Primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPhoneField) KeyboardType.Phone else KeyboardType.Text
            )
        )
    }
}

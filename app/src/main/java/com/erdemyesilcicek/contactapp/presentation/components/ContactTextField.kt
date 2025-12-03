package com.erdemyesilcicek.contactapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontSize = dimens.fontSizeLarge,
                        color = AppColors.TextTertiary
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.textFieldHeight),
            textStyle = TextStyle(
                fontSize = dimens.fontSizeLarge,
                color = AppColors.TextPrimary
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = AppColors.Primary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(dimens.textFieldCornerRadius),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPhoneField) KeyboardType.Phone else KeyboardType.Text
            )
        )
        
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.paddingMedium),
                thickness = dimens.dividerThickness,
                color = AppColors.Divider
            )
        }
    }
}

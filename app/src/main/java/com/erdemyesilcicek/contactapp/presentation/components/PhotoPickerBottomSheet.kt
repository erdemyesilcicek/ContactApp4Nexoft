package com.erdemyesilcicek.contactapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.util.AppDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPickerBottomSheet(
    onDismiss: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    val dimens = AppDimens.current
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.BottomSheetBackground,
        shape = RoundedCornerShape(
            topStart = dimens.bottomSheetCornerRadius,
            topEnd = dimens.bottomSheetCornerRadius
        ),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimens.paddingMedium)
        ) {
            BottomSheetItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = AppStrings.CAMERA,
                        modifier = Modifier.size(dimens.iconSizeMedium),
                        tint = AppColors.TextPrimary
                    )
                },
                text = AppStrings.CAMERA,
                onClick = {
                    onCameraClick()
                    onDismiss()
                }
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = dimens.paddingMedium),
                thickness = dimens.dividerThickness,
                color = AppColors.BottomSheetDivider
            )
            
            BottomSheetItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = AppStrings.GALLERY,
                        modifier = Modifier.size(dimens.iconSizeMedium),
                        tint = AppColors.TextPrimary
                    )
                },
                text = AppStrings.GALLERY,
                onClick = {
                    onGalleryClick()
                    onDismiss()
                }
            )
            
            Spacer(modifier = Modifier.height(dimens.paddingMedium))
            
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.paddingMedium)
            ) {
                Text(
                    text = AppStrings.CANCEL,
                    fontSize = dimens.fontSizeLarge,
                    fontWeight = FontWeight.Normal,
                    color = AppColors.CancelText
                )
            }
            
            Spacer(modifier = Modifier.height(dimens.paddingMedium))
        }
    }
}

@Composable
private fun BottomSheetItem(
    icon: @Composable () -> Unit,
    text: String,
    onClick: () -> Unit
) {
    val dimens = AppDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.bottomSheetItemHeight)
            .clip(RoundedCornerShape(dimens.cardCornerRadius))
            .background(AppColors.BottomSheetBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                onClick = onClick
            )
            .padding(horizontal = dimens.paddingLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        
        Spacer(modifier = Modifier.width(dimens.paddingMedium))
        
        Text(
            text = text,
            fontSize = dimens.fontSizeLarge,
            fontWeight = FontWeight.Normal,
            color = AppColors.TextPrimary
        )
    }
}

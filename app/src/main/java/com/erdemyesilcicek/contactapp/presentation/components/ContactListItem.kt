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
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.data.model.Contact
import com.erdemyesilcicek.contactapp.util.AppDimens

@Composable
fun ContactListItem(
    contact: Contact,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    val dimens = AppDimens.current
    
    val interactionSource = remember { MutableInteractionSource() }
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Surface)
                .clickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(),
                    onClick = onClick
                )
                .padding(
                    horizontal = dimens.screenHorizontalPadding,
                    vertical = dimens.paddingSmall
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(
                photoUri = contact.photoUri?.toString(),
                initials = contact.initials,
                size = dimens.contactItemAvatarSize,
                showPlaceholderIcon = false
            )
            
            Spacer(modifier = Modifier.width(dimens.paddingMedium))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.fullName,
                    fontSize = dimens.fontSizeMedium,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextPrimary
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = contact.phoneNumber,
                    fontSize = dimens.fontSizeSmall,
                    fontWeight = FontWeight.Normal,
                    color = AppColors.TextSecondary
                )
            }
        }
        
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = dimens.screenHorizontalPadding + dimens.contactItemAvatarSize + dimens.paddingMedium),
                thickness = dimens.dividerThickness,
                color = AppColors.Divider
            )
        }
    }
}

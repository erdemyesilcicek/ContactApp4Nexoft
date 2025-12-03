package com.erdemyesilcicek.contactapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.util.AppDimens

@Composable
fun EmptyContactsState(
    onCreateContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimens = AppDimens.current
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Gray circle with person outline icon
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            modifier = Modifier.size(dimens.avatarSizeLarge),
            tint = AppColors.AvatarIconColor
        )
        
        Spacer(modifier = Modifier.height(dimens.paddingMedium))
        
        Text(
            text = AppStrings.NO_CONTACTS,
            fontSize = dimens.fontSizeXLarge,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(dimens.paddingXSmall))
        
        Text(
            text = AppStrings.NO_CONTACTS_DESCRIPTION,
            fontSize = dimens.fontSizeMedium,
            fontWeight = FontWeight.Normal,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(dimens.paddingMedium))
        
        TextButton(onClick = onCreateContactClick) {
            Text(
                text = AppStrings.CREATE_NEW_CONTACT,
                fontSize = dimens.fontSizeMedium,
                fontWeight = FontWeight.Normal,
                color = AppColors.Primary
            )
        }
    }
}

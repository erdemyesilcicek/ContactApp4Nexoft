package com.erdemyesilcicek.contactapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.util.AppDimens

enum class BottomSheetTopBarType {
    ADD_CONTACT,
    EDIT_CONTACT,
    CONTACT_DETAIL
}

@Composable
fun BottomSheetTopBar(
    type: BottomSheetTopBarType,
    onCancel: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null,
    isDoneEnabled: Boolean = true,
    // For Contact Detail type
    showDropdownMenu: Boolean = false,
    onShowDropdown: (() -> Unit)? = null,
    onHideDropdown: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    val dimens = AppDimens.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Drag Handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(36.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp))
                .background(AppColors.AvatarIconColor)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        when (type) {
            BottomSheetTopBarType.ADD_CONTACT,
            BottomSheetTopBarType.EDIT_CONTACT -> {
                FormTopBarContent(
                    title = if (type == BottomSheetTopBarType.ADD_CONTACT) 
                        AppStrings.NEW_CONTACT_TITLE 
                    else 
                        AppStrings.EDIT_CONTACT_TITLE,
                    onCancel = onCancel ?: {},
                    onDone = onDone ?: {},
                    isDoneEnabled = isDoneEnabled
                )
            }
            BottomSheetTopBarType.CONTACT_DETAIL -> {
                DetailTopBarContent(
                    showDropdownMenu = showDropdownMenu,
                    onShowDropdown = onShowDropdown ?: {},
                    onHideDropdown = onHideDropdown ?: {},
                    onEditClick = onEditClick ?: {},
                    onDeleteClick = onDeleteClick ?: {}
                )
            }
        }
    }
}

@Composable
private fun FormTopBarContent(
    title: String,
    onCancel: () -> Unit,
    onDone: () -> Unit,
    isDoneEnabled: Boolean
) {
    val dimens = AppDimens.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onCancel,
            modifier = Modifier.width(80.dp)
        ) {
            Text(
                text = AppStrings.CANCEL,
                fontSize = dimens.fontSizeLarge,
                fontWeight = FontWeight.Normal,
                color = AppColors.CancelText
            )
        }
        
        Text(
            text = title,
            fontSize = dimens.fontSizeLarge,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        
        TextButton(
            onClick = onDone,
            enabled = isDoneEnabled,
            modifier = Modifier.width(80.dp)
        ) {
            Text(
                text = AppStrings.DONE,
                fontSize = dimens.fontSizeLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isDoneEnabled) AppColors.DoneTextEnabled else AppColors.DoneTextDisabled
            )
        }
    }
}

@Composable
private fun DetailTopBarContent(
    showDropdownMenu: Boolean,
    onShowDropdown: () -> Unit,
    onHideDropdown: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dimens = AppDimens.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        Box {
            IconButton(onClick = onShowDropdown) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = AppColors.TextPrimary
                )
            }
            
            DropdownMenu(
                expanded = showDropdownMenu,
                onDismissRequest = onHideDropdown
            ) {
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = AppColors.TextPrimary,
                                modifier = Modifier.size(dimens.iconSizeSmall)
                            )
                            Spacer(modifier = Modifier.width(dimens.paddingSmall))
                            Text(
                                text = AppStrings.EDIT,
                                color = AppColors.TextPrimary
                            )
                        }
                    },
                    onClick = onEditClick
                )
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = AppColors.Error,
                                modifier = Modifier.size(dimens.iconSizeSmall)
                            )
                            Spacer(modifier = Modifier.width(dimens.paddingSmall))
                            Text(
                                text = AppStrings.DELETE,
                                color = AppColors.Error
                            )
                        }
                    },
                    onClick = onDeleteClick
                )
            }
        }
    }
}

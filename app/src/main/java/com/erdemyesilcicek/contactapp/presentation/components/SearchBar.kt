package com.erdemyesilcicek.contactapp.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.util.AppDimens

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = AppStrings.SEARCH_PLACEHOLDER,
    searchHistory: List<String> = emptyList(),
    onHistoryItemClick: (String) -> Unit = {},
    onRemoveHistoryItem: (String) -> Unit = {},
    onClearHistory: () -> Unit = {},
    onSearch: (String) -> Unit = {}
) {
    val dimens = AppDimens.current
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.textFieldHeight)
                .clip(RoundedCornerShape(dimens.searchBarCornerRadius))
                .background(AppColors.SearchBarBackground)
                .padding(horizontal = dimens.paddingMedium),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(dimens.iconSizeMedium),
                    tint = AppColors.SearchPlaceholder
                )
                
                Spacer(modifier = Modifier.width(dimens.paddingSmall))
                
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                fontSize = dimens.fontSizeLarge,
                                color = AppColors.SearchPlaceholder
                            )
                        )
                    }
                    
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                            },
                        textStyle = TextStyle(
                            fontSize = dimens.fontSizeLarge,
                            color = AppColors.TextPrimary
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(AppColors.Primary),
                        onTextLayout = { },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Search
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onSearch = {
                                if (query.isNotBlank()) {
                                    onSearch(query)
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    )
                }
                
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(dimens.iconSizeMedium)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            modifier = Modifier.size(dimens.iconSizeSmall),
                            tint = AppColors.SearchPlaceholder
                        )
                    }
                }
            }
        }
        
        // Search History Dropdown
        AnimatedVisibility(
            visible = isFocused && query.isEmpty() && searchHistory.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SearchHistoryDropdown(
                history = searchHistory,
                onItemClick = { item ->
                    onHistoryItemClick(item)
                    focusManager.clearFocus()
                },
                onRemoveItem = onRemoveHistoryItem,
                onClearHistory = onClearHistory
            )
        }
    }
}

@Composable
private fun SearchHistoryDropdown(
    history: List<String>,
    onItemClick: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    val dimens = AppDimens.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimens.paddingSmall)
            .clip(RoundedCornerShape(dimens.cornerRadiusMedium))
            .background(AppColors.Surface)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.paddingMedium, vertical = dimens.paddingSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = AppStrings.RECENT_SEARCHES,
                fontSize = dimens.fontSizeSmall,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextSecondary
            )
            
            TextButton(onClick = onClearHistory) {
                Text(
                    text = AppStrings.CLEAR_HISTORY,
                    fontSize = dimens.fontSizeSmall,
                    color = AppColors.Primary
                )
            }
        }
        
        HorizontalDivider(
            color = AppColors.Divider,
            thickness = dimens.dividerThickness
        )
        
        // History Items
        history.take(5).forEach { item ->
            SearchHistoryItem(
                text = item,
                onClick = { onItemClick(item) },
                onRemove = { onRemoveItem(item) }
            )
        }
    }
}

@Composable
private fun SearchHistoryItem(
    text: String,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val dimens = AppDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = dimens.paddingMedium, vertical = dimens.paddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(dimens.iconSizeMedium),
            tint = AppColors.TextSecondary
        )
        
        Spacer(modifier = Modifier.width(dimens.paddingMedium))
        
        Text(
            text = text,
            fontSize = dimens.fontSizeMedium,
            color = AppColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(dimens.iconSizeMedium)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                modifier = Modifier.size(dimens.iconSizeSmall),
                tint = AppColors.TextSecondary
            )
        }
    }
}

package com.erdemyesilcicek.contactapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.util.AppDimens

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = AppStrings.SEARCH_PLACEHOLDER
) {
    val dimens = AppDimens.current
    
    Box(
        modifier = modifier
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
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = dimens.fontSizeLarge,
                        color = AppColors.TextPrimary
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(AppColors.Primary)
                )
            }
        }
    }
}

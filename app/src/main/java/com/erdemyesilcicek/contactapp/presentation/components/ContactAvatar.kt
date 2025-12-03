package com.erdemyesilcicek.contactapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.erdemyesilcicek.contactapp.R
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.util.AppDimens

// Avatar renk paleti - iOS tarzı pastel renkler
private val avatarColors = listOf(
    Color(0xFFE3F2FD) to Color(0xFF1976D2), // Açık mavi arka plan, koyu mavi yazı
    Color(0xFFFCE4EC) to Color(0xFFC2185B), // Açık pembe arka plan, koyu pembe yazı
    Color(0xFFE8F5E9) to Color(0xFF388E3C), // Açık yeşil arka plan, koyu yeşil yazı
    Color(0xFFFFF3E0) to Color(0xFFE65100), // Açık turuncu arka plan, koyu turuncu yazı
    Color(0xFFF3E5F5) to Color(0xFF7B1FA2), // Açık mor arka plan, koyu mor yazı
    Color(0xFFE0F7FA) to Color(0xFF00838F), // Açık cyan arka plan, koyu cyan yazı
    Color(0xFFFFFDE7) to Color(0xFFF9A825), // Açık sarı arka plan, koyu sarı yazı
    Color(0xFFEFEBE9) to Color(0xFF5D4037), // Açık kahve arka plan, koyu kahve yazı
)

// Fotoğraflı avatarlar için border renk paleti
private val photoBorderColors = listOf(
    Color(0xFFFFB6C1), // Açık pembe
    Color(0xFFDDA0DD), // Mor-pembe
    Color(0xFFB0C4DE), // Açık mavi-gri
)

private fun getAvatarColors(initials: String): Pair<Color, Color> {
    val index = initials.hashCode().let { if (it < 0) -it else it } % avatarColors.size
    return avatarColors[index]
}

private fun getPhotoBorderColor(initials: String): Color {
    val index = initials.hashCode().let { if (it < 0) -it else it } % photoBorderColors.size
    return photoBorderColors[index]
}

@Composable
fun ContactAvatar(
    photoUri: String?,
    initials: String,
    size: Dp? = null,
    showPlaceholderIcon: Boolean = true,
    isInDeviceContacts: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val dimens = AppDimens.current
    val avatarSize = size ?: dimens.avatarSizeMedium
    val interactionSource = remember { MutableInteractionSource() }
    val (backgroundColor, textColor) = remember(initials) { getAvatarColors(initials) }
    val photoBorderColor = remember(initials) { getPhotoBorderColor(initials) }
    val hasPhoto = !photoUri.isNullOrBlank()
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(avatarSize)
                .then(
                    if (hasPhoto) {
                        Modifier.border(1.5.dp, photoBorderColor, CircleShape)
                    } else {
                        Modifier
                    }
                )
                .clip(CircleShape)
                .background(if (hasPhoto || showPlaceholderIcon) AppColors.AvatarBackground else backgroundColor)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = rememberRipple(),
                            onClick = onClick
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (hasPhoto) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Contact photo",
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else if (showPlaceholderIcon) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default avatar",
                    modifier = Modifier.size(avatarSize * 0.5f),
                    tint = AppColors.AvatarIconColor
                )
            } else {
                Text(
                    text = initials,
                    fontSize = dimens.fontSizeMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            }
        }
        
        // Device contacts badge - sağ alt köşe
        if (isInDeviceContacts) {
            val badgeSize = avatarSize * 0.4f
            Image(
                painter = painterResource(id = R.drawable.ic_device_contacts),
                contentDescription = "In device contacts",
                modifier = Modifier
                    .size(badgeSize)
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

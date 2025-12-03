package com.erdemyesilcicek.contactapp.presentation.screens.contactdetail

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.data.model.Contact
import com.erdemyesilcicek.contactapp.presentation.components.BottomSheetTopBar
import com.erdemyesilcicek.contactapp.presentation.components.BottomSheetTopBarType
import com.erdemyesilcicek.contactapp.presentation.components.ContactAvatar
import com.erdemyesilcicek.contactapp.presentation.components.DeleteConfirmationBottomSheet
import com.erdemyesilcicek.contactapp.util.AppDimens
import com.erdemyesilcicek.contactapp.util.ContactUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailBottomSheet(
    contactId: String,
    onDismiss: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onDeleted: () -> Unit,
    viewModel: ContactDetailViewModel = hiltViewModel<ContactDetailViewModel, ContactDetailViewModel.Factory>(
        key = contactId,
        creationCallback = { factory -> factory.create(contactId) }
    )
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dimens = AppDimens.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    
    var isContentVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        isContentVisible = true
    }
    
    val contentScale by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "contentScale"
    )
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "contentAlpha"
    )
    
    var pendingSaveToPhone by remember { mutableStateOf(false) }
    
    val writeContactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && pendingSaveToPhone) {
            state.contact?.let { contact ->
                scope.launch {
                    val success = ContactUtils.saveContactToPhone(context, contact)
                    if (success) {
                        viewModel.markAsSavedToPhone()
                    }
                }
            }
        }
        pendingSaveToPhone = false
    }
    
    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            onDeleted()
        }
    }
    
    LaunchedEffect(state.showSavedToPhoneMessage) {
        if (state.showSavedToPhoneMessage) {
            snackbarHostState.showSnackbar(AppStrings.USER_ADDED_TO_PHONE)
            viewModel.hidePhoneMessage()
        }
    }
    
    if (state.showDeleteDialog) {
        DeleteConfirmationBottomSheet(
            onDismiss = { viewModel.hideDeleteDialog() },
            onConfirm = { viewModel.deleteContact() }
        )
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Surface,
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        dragHandle = null,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .graphicsLayer {
                    scaleX = contentScale
                    scaleY = contentScale
                    alpha = contentAlpha
                }
        ) {
            AnimatedVisibility(
                visible = isContentVisible,
                enter = fadeIn(animationSpec = tween(300)) + 
                       slideInVertically(
                           initialOffsetY = { -it / 2 },
                           animationSpec = spring(
                               dampingRatio = Spring.DampingRatioMediumBouncy,
                               stiffness = Spring.StiffnessLow
                           )
                       ),
                exit = fadeOut()
            ) {
                BottomSheetTopBar(
                    type = BottomSheetTopBarType.CONTACT_DETAIL,
                    showDropdownMenu = state.showDropdownMenu,
                    onShowDropdown = { viewModel.showDropdownMenu() },
                    onHideDropdown = { viewModel.hideDropdownMenu() },
                    onEditClick = {
                        viewModel.hideDropdownMenu()
                        state.contact?.let {
                            onNavigateToEdit(it.id)
                        }
                    },
                    onDeleteClick = { viewModel.showDeleteDialog() }
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(AppColors.Surface)
            ) {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.Primary)
                    }
                } else {
                    state.contact?.let { contact ->
                        this@Column.AnimatedVisibility(
                            visible = isContentVisible,
                            enter = fadeIn(animationSpec = tween(400, delayMillis = 100)) +
                                    slideInVertically(
                                        initialOffsetY = { it },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    ),
                            exit = fadeOut()
                        ) {
                            ContactDetailContent(
                                contact = contact,
                                isSavedToPhone = state.isSavedToPhone,
                                onSaveToPhone = {
                                    pendingSaveToPhone = true
                                    writeContactsPermissionLauncher.launch(Manifest.permission.WRITE_CONTACTS)
                                }
                            )
                        }
                    }
                }
                
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun ContactDetailContent(
    contact: Contact,
    isSavedToPhone: Boolean,
    onSaveToPhone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimens = AppDimens.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(dimens.paddingLarge))

        ContactAvatar(
            photoUri = contact.photoUri?.toString(),
            initials = contact.initials,
            size = dimens.avatarSizeLarge,
            showPlaceholderIcon = contact.photoUri == null
        )
        
        Spacer(modifier = Modifier.height(dimens.paddingLarge))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Surface)
        ) {
            ContactDetailField(
                value = contact.firstName,
                showDivider = true
            )
            
            ContactDetailField(
                value = contact.lastName,
                showDivider = true
            )
            
            ContactDetailField(
                value = contact.phoneNumber,
                showDivider = false
            )
        }
        
        Spacer(modifier = Modifier.height(dimens.paddingXLarge))
        
        Button(
            onClick = onSaveToPhone,
            enabled = !isSavedToPhone,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.paddingLarge)
                .height(dimens.buttonHeight),
            shape = RoundedCornerShape(dimens.cornerRadiusMedium),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Surface,
                contentColor = AppColors.TextPrimary,
                disabledContainerColor = AppColors.Surface,
                disabledContentColor = AppColors.TextSecondary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = dimens.elevationSmall
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Phone,
                contentDescription = null,
                modifier = Modifier.size(dimens.iconSizeSmall)
            )
            Spacer(modifier = Modifier.width(dimens.paddingSmall))
            Text(
                text = AppStrings.SAVE_TO_PHONE,
                fontSize = dimens.fontSizeMedium,
                fontWeight = FontWeight.Medium
            )
        }
        
        if (isSavedToPhone) {
            Spacer(modifier = Modifier.height(dimens.paddingSmall))
            Text(
                text = AppStrings.ALREADY_SAVED_TO_PHONE,
                fontSize = dimens.fontSizeSmall,
                color = AppColors.TextSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(dimens.paddingLarge))
    }
}

@Composable
private fun ContactDetailField(
    value: String,
    showDivider: Boolean
) {
    val dimens = AppDimens.current
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = value,
            fontSize = dimens.fontSizeLarge,
            color = AppColors.TextPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimens.paddingMedium,
                    vertical = dimens.paddingMedium
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

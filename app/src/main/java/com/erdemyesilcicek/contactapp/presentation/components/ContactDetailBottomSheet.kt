package com.erdemyesilcicek.contactapp.presentation.components

import android.Manifest
import android.content.ContentProviderOperation
import android.content.Context
import android.provider.ContactsContract
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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.data.model.Contact
import com.erdemyesilcicek.contactapp.presentation.screens.contactdetail.ContactDetailViewModel
import com.erdemyesilcicek.contactapp.util.AppDimens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailBottomSheet(
    contactId: String,
    onDismiss: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onDeleted: () -> Unit,
    viewModel: ContactDetailViewModel = hiltViewModel<ContactDetailViewModel, ContactDetailViewModel.Factory>(
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
    
    // Animation states
    var isContentVisible by remember { mutableStateOf(false) }
    
    // Trigger content animation after sheet appears
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        isContentVisible = true
    }
    
    // Content scale animation
    val contentScale by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "contentScale"
    )
    
    // Content alpha animation
    val contentAlpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "contentAlpha"
    )
    
    var pendingSaveToPhone by remember { mutableStateOf(false) }
    
    // Permission launcher for WRITE_CONTACTS
    val writeContactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && pendingSaveToPhone) {
            state.contact?.let { contact ->
                scope.launch {
                    val success = saveContactToPhone(context, contact)
                    if (success) {
                        viewModel.markAsSavedToPhone()
                    }
                }
            }
        }
        pendingSaveToPhone = false
    }
    
    // Handle deletion
    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            onDeleted()
        }
    }
    
    // Show snackbar when saved to phone
    LaunchedEffect(state.showSavedToPhoneMessage) {
        if (state.showSavedToPhoneMessage) {
            snackbarHostState.showSnackbar(AppStrings.USER_ADDED_TO_PHONE)
            viewModel.hidePhoneMessage()
        }
    }
    
    // Delete confirmation bottom sheet
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
            // Top Bar with animation
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
                ContactDetailTopBar(
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
            
            // Content
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
private fun ContactDetailTopBar(
    showDropdownMenu: Boolean,
    onShowDropdown: () -> Unit,
    onHideDropdown: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dimens = AppDimens.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
    ) {
        // Handle bar (iOS style)
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(36.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp))
                .background(AppColors.AvatarIconColor)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Top bar with menu
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
        
        // Avatar
        ContactAvatar(
            photoUri = contact.photoUri?.toString(),
            initials = contact.initials,
            size = dimens.avatarSizeLarge,
            showPlaceholderIcon = contact.photoUri == null
        )
        
        Spacer(modifier = Modifier.height(dimens.paddingLarge))
        
        // Info Fields
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
        
        // Save to Phone Button
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

private fun saveContactToPhone(context: Context, contact: Contact): Boolean {
    return try {
        val operations = ArrayList<ContentProviderOperation>()
        
        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )
        
        // Add name
        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.firstName)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contact.lastName)
                .build()
        )
        
        // Add phone number
        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build()
        )
        
        context.contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

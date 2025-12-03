package com.erdemyesilcicek.contactapp.presentation.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.presentation.screens.editcontact.EditContactViewModel
import com.erdemyesilcicek.contactapp.util.AppDimens
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactBottomSheet(
    contactId: String,
    onDismiss: () -> Unit,
    onContactUpdated: () -> Unit,
    viewModel: EditContactViewModel = hiltViewModel<EditContactViewModel, EditContactViewModel.Factory>(
        creationCallback = { factory -> factory.create(contactId) }
    )
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dimens = AppDimens.current
    val scope = rememberCoroutineScope()
    
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
    
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoUri?.let { uri ->
                viewModel.onPhotoSelected(uri)
            }
        }
    }
    
    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.onPhotoSelected(it)
        }
    }
    
    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            tempPhotoUri = uri
            uri?.let { cameraLauncher.launch(it) }
        }
    }
    
    // Handle saved state
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onContactUpdated()
            viewModel.resetSavedState()
        }
    }
    
    // Photo picker sheet
    if (state.showPhotoPickerSheet) {
        PhotoPickerBottomSheet(
            onDismiss = { viewModel.hidePhotoPicker() },
            onCameraClick = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onGalleryClick = {
                galleryLauncher.launch("image/*")
            }
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
                EditContactTopBar(
                    onCancel = {
                        scope.launch {
                            isContentVisible = false
                            kotlinx.coroutines.delay(150)
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    onDone = { viewModel.updateContact() },
                    isDoneEnabled = state.isValid && !state.isSaving
                )
            }
            
            // Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(AppColors.Background)
            ) {
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.Primary)
                    }
                } else {
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(dimens.paddingLarge))

                            // Avatar Section
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ContactAvatar(
                                    photoUri = state.photoUri?.toString(),
                                    initials = "",
                                    size = dimens.avatarSizeLarge,
                                    showPlaceholderIcon = true,
                                    onClick = { viewModel.showPhotoPicker() }
                                )

                                Spacer(modifier = Modifier.height(dimens.paddingSmall))

                                TextButton(onClick = { viewModel.showPhotoPicker() }) {
                                    Text(
                                        text = AppStrings.CHANGE_PHOTO,
                                        fontSize = dimens.fontSizeMedium,
                                        fontWeight = FontWeight.Normal,
                                        color = AppColors.TextLink
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(dimens.paddingLarge))

                            // Form Fields
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AppColors.Surface)
                            ) {
                                ContactTextField(
                                    value = state.firstName,
                                    onValueChange = viewModel::onFirstNameChange,
                                    placeholder = AppStrings.FIRST_NAME,
                                    showDivider = true
                                )

                                ContactTextField(
                                    value = state.lastName,
                                    onValueChange = viewModel::onLastNameChange,
                                    placeholder = AppStrings.LAST_NAME,
                                    showDivider = true
                                )

                                ContactTextField(
                                    value = state.phoneNumber,
                                    onValueChange = viewModel::onPhoneNumberChange,
                                    placeholder = AppStrings.PHONE_NUMBER,
                                    showDivider = false
                                )
                            }

                            Spacer(modifier = Modifier.height(dimens.paddingXXLarge))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditContactTopBar(
    onCancel: () -> Unit,
    onDone: () -> Unit,
    isDoneEnabled: Boolean
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
        
        // Top bar with Cancel, Title, Done
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
                text = AppStrings.EDIT_CONTACT_TITLE,
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
}

private fun createImageUri(context: Context): Uri? {
    return try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

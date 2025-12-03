package com.erdemyesilcicek.contactapp.presentation.screens.addcontact

import android.Manifest
import android.net.Uri
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.presentation.components.BottomSheetTopBar
import com.erdemyesilcicek.contactapp.presentation.components.BottomSheetTopBarType
import com.erdemyesilcicek.contactapp.presentation.components.ContactAvatar
import com.erdemyesilcicek.contactapp.presentation.components.ContactTextField
import com.erdemyesilcicek.contactapp.presentation.components.PhotoPickerBottomSheet
import com.erdemyesilcicek.contactapp.util.AppDimens
import com.erdemyesilcicek.contactapp.util.ImageUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactBottomSheet(
    onDismiss: () -> Unit,
    onContactSaved: () -> Unit,
    viewModel: AddContactViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dimens = AppDimens.current
    val scope = rememberCoroutineScope()
    
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
    
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoUri?.let { uri ->
                viewModel.onPhotoSelected(uri)
            }
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.onPhotoSelected(it)
        }
    }
    
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = ImageUtils.createImageUri(context)
            tempPhotoUri = uri
            uri?.let { cameraLauncher.launch(it) }
        }
    }
    
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onContactSaved()
            viewModel.resetSavedState()
            viewModel.resetState()
        }
    }
    
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
        onDismissRequest = {
            viewModel.resetState()
            onDismiss()
        },
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
                    type = BottomSheetTopBarType.ADD_CONTACT,
                    onCancel = {
                        scope.launch {
                            isContentVisible = false
                            delay(150)
                            sheetState.hide()
                            viewModel.resetState()
                            onDismiss()
                        }
                    },
                    onDone = { viewModel.saveContact() },
                    isDoneEnabled = state.isValid && !state.isSaving
                )
            }
                
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(AppColors.Background)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(dimens.paddingLarge))
                
                AnimatedVisibility(
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
                                text = if (state.photoUri != null) AppStrings.CHANGE_PHOTO else AppStrings.ADD_PHOTO,
                                fontSize = dimens.fontSizeMedium,
                                fontWeight = FontWeight.Normal,
                                color = AppColors.TextLink
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(dimens.paddingLarge))
                
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(animationSpec = tween(400, delayMillis = 200)) +
                           slideInVertically(
                               initialOffsetY = { it / 2 },
                               animationSpec = spring(
                                   dampingRatio = Spring.DampingRatioLowBouncy,
                                   stiffness = Spring.StiffnessLow
                               )
                           ),
                    exit = fadeOut()
                ) {
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
                }
                
                Spacer(modifier = Modifier.height(dimens.paddingXXLarge))
            }
        }
    }
}

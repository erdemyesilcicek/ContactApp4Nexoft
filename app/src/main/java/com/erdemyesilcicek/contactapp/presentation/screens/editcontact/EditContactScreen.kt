package com.erdemyesilcicek.contactapp.presentation.screens.editcontact

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.presentation.components.ContactAvatar
import com.erdemyesilcicek.contactapp.presentation.components.ContactTextField
import com.erdemyesilcicek.contactapp.presentation.components.PhotoPickerBottomSheet
import com.erdemyesilcicek.contactapp.util.AppDimens
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactScreen(
    onNavigateBack: () -> Unit,
    onContactUpdated: () -> Unit,
    viewModel: EditContactViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dimens = AppDimens.current
    
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.EDIT_CONTACT_TITLE,
                        fontSize = dimens.fontSizeLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text(
                            text = AppStrings.CANCEL,
                            fontSize = dimens.fontSizeLarge,
                            fontWeight = FontWeight.Normal,
                            color = AppColors.CancelText
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.updateContact() },
                        enabled = state.isValid && !state.isSaving
                    ) {
                        Text(
                            text = AppStrings.DONE,
                            fontSize = dimens.fontSizeLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (state.isValid) AppColors.DoneTextEnabled else AppColors.DoneTextDisabled
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Surface
                )
            )
        },
        containerColor = AppColors.Background
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColors.Primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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

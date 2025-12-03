package com.erdemyesilcicek.contactapp.presentation.screens.contactlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.erdemyesilcicek.contactapp.constants.AppColors
import com.erdemyesilcicek.contactapp.constants.AppStrings
import com.erdemyesilcicek.contactapp.presentation.components.AddContactBottomSheet
import com.erdemyesilcicek.contactapp.presentation.components.ContactDetailBottomSheet
import com.erdemyesilcicek.contactapp.presentation.components.ContactListItem
import com.erdemyesilcicek.contactapp.presentation.components.ContactSectionHeader
import com.erdemyesilcicek.contactapp.presentation.components.EditContactBottomSheet
import com.erdemyesilcicek.contactapp.presentation.components.EmptyContactsState
import com.erdemyesilcicek.contactapp.presentation.components.SearchBar
import com.erdemyesilcicek.contactapp.util.AppDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    onContactSaved: () -> Unit,
    showUpdateSuccess: Boolean = false,
    onUpdateSuccessShown: () -> Unit = {},
    viewModel: ContactListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val dimens = AppDimens.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // State for showing add contact bottom sheet
    var showAddContactSheet by remember { mutableStateOf(false) }
    
    // State for showing contact detail bottom sheet
    var showContactDetailSheet by remember { mutableStateOf(false) }
    var selectedContactId by remember { mutableStateOf<String?>(null) }
    
    // State for showing edit contact bottom sheet
    var showEditContactSheet by remember { mutableStateOf(false) }
    var editContactId by remember { mutableStateOf<String?>(null) }
    
    // Local state for showing update success from edit
    var showLocalUpdateSuccess by remember { mutableStateOf(false) }
    
    // Show update success snackbar
    LaunchedEffect(showUpdateSuccess, showLocalUpdateSuccess) {
        if (showUpdateSuccess || showLocalUpdateSuccess) {
            snackbarHostState.showSnackbar(AppStrings.USER_UPDATED)
            if (showUpdateSuccess) onUpdateSuccessShown()
            if (showLocalUpdateSuccess) showLocalUpdateSuccess = false
        }
    }
    
    // Add Contact Bottom Sheet
    if (showAddContactSheet) {
        AddContactBottomSheet(
            onDismiss = { showAddContactSheet = false },
            onContactSaved = {
                showAddContactSheet = false
                onContactSaved()
            }
        )
    }
    
    // Contact Detail Bottom Sheet
    if (showContactDetailSheet && selectedContactId != null) {
        ContactDetailBottomSheet(
            contactId = selectedContactId!!,
            onDismiss = { 
                showContactDetailSheet = false
                selectedContactId = null
            },
            onNavigateToEdit = { contactId ->
                showContactDetailSheet = false
                selectedContactId = null
                editContactId = contactId
                showEditContactSheet = true
            },
            onDeleted = {
                showContactDetailSheet = false
                selectedContactId = null
            }
        )
    }
    
    // Edit Contact Bottom Sheet
    if (showEditContactSheet && editContactId != null) {
        EditContactBottomSheet(
            contactId = editContactId!!,
            onDismiss = {
                showEditContactSheet = false
                editContactId = null
            },
            onContactUpdated = {
                showEditContactSheet = false
                editContactId = null
                showLocalUpdateSuccess = true
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.CONTACTS_TITLE,
                        fontSize = dimens.fontSizeTitle,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showAddContactSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircle,
                            contentDescription = "Add contact",
                            tint = AppColors.Primary,
                            modifier = Modifier.size(dimens.topBarAddButtonSize)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Surface
                )
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(dimens.paddingMedium),
                    shape = RoundedCornerShape(dimens.cornerRadiusMedium),
                    containerColor = AppColors.Surface,
                    contentColor = AppColors.TextPrimary
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(dimens.iconSizeMedium)
                                .clip(CircleShape)
                                .background(AppColors.Success),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AppColors.Surface,
                                modifier = Modifier.size(dimens.iconSizeSmall)
                            )
                        }
                        Spacer(modifier = Modifier.width(dimens.paddingSmall))
                        Text(
                            text = data.visuals.message,
                            color = AppColors.Success,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        containerColor = AppColors.Surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppColors.Surface)
        ) {
            // Search Bar
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.screenHorizontalPadding)
            )
            
            Spacer(modifier = Modifier.height(dimens.paddingMedium))
            
            if (state.contacts.isEmpty() && state.searchQuery.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyContactsState(
                        onCreateContactClick = { showAddContactSheet = true }
                    )
                }
            } else {
                // Contact List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = dimens.paddingLarge)
                ) {
                    state.groupedContacts.forEach { (letter, contactsInGroup) ->
                        item(key = "header_$letter") {
                            ContactSectionHeader(letter = letter)
                        }
                        
                        items(
                            items = contactsInGroup,
                            key = { it.id }
                        ) { contact ->
                            ContactListItem(
                                contact = contact,
                                onClick = { 
                                    selectedContactId = contact.id
                                    showContactDetailSheet = true
                                },
                                showDivider = contactsInGroup.last() != contact
                            )
                        }
                    }
                }
            }
        }
    }
}

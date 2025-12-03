package com.erdemyesilcicek.contactapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.erdemyesilcicek.contactapp.presentation.screens.contactlist.ContactListScreen
import com.erdemyesilcicek.contactapp.presentation.screens.success.SuccessScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    var showUpdateSuccess by remember { mutableStateOf(false) }
    var showAddSuccess by remember { mutableStateOf(false) }
    
    NavHost(
        navController = navController,
        startDestination = Routes.ContactList.route
    ) {
        composable(route = Routes.ContactList.route) {
            // If we need to show success screen after adding contact
            if (showAddSuccess) {
                SuccessScreen(
                    onNavigateToContactList = {
                        showAddSuccess = false
                    }
                )
            } else {
                ContactListScreen(
                    onContactSaved = {
                        showAddSuccess = true
                    },
                    showUpdateSuccess = showUpdateSuccess,
                    onUpdateSuccessShown = { showUpdateSuccess = false }
                )
            }
        }
        
        composable(route = Routes.Success.route) {
            SuccessScreen(
                onNavigateToContactList = {
                    navController.navigate(Routes.ContactList.route) {
                        popUpTo(Routes.ContactList.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

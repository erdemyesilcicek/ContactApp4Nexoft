package com.erdemyesilcicek.contactapp.navigation

sealed class Routes(val route: String) {
    data object ContactList : Routes("contact_list")
    data object Success : Routes("success")
}

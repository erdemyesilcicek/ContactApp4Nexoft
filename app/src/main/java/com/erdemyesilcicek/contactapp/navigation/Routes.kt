package com.erdemyesilcicek.contactapp.navigation

sealed class Routes(val route: String) {
    data object ContactList : Routes("contact_list")
    data object AddContact : Routes("add_contact")
    data object Success : Routes("success")
    data object ContactDetail : Routes("contact_detail/{contactId}") {
        fun createRoute(contactId: String) = "contact_detail/$contactId"
    }
    data object EditContact : Routes("edit_contact/{contactId}") {
        fun createRoute(contactId: String) = "edit_contact/$contactId"
    }
}

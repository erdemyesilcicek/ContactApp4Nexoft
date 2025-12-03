package com.erdemyesilcicek.contactapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * User data transfer object from API
 */
data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?
)

/**
 * Request body for creating/updating user
 */
data class UserRequest(
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?
)

/**
 * Response data for GetAll endpoint
 */
data class UserListData(
    @SerializedName("users")
    val users: List<UserDto>
)

/**
 * Response data for image upload
 */
data class ImageUploadData(
    @SerializedName("imageUrl")
    val imageUrl: String
)

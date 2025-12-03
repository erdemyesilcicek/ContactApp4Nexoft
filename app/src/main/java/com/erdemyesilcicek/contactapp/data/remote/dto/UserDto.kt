package com.erdemyesilcicek.contactapp.data.remote.dto

import com.google.gson.annotations.SerializedName

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

data class UserListData(
    @SerializedName("users")
    val users: List<UserDto>
)

data class ImageUploadData(
    @SerializedName("imageUrl")
    val imageUrl: String
)

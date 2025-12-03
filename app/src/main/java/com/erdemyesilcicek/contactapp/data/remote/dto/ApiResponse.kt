package com.erdemyesilcicek.contactapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("messages")
    val messages: List<String>,
    @SerializedName("data")
    val data: T?,
    @SerializedName("status")
    val status: Int
)

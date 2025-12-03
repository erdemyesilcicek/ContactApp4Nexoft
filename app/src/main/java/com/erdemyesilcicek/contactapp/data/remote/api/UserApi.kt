package com.erdemyesilcicek.contactapp.data.remote.api

import com.erdemyesilcicek.contactapp.data.remote.dto.ApiResponse
import com.erdemyesilcicek.contactapp.data.remote.dto.ImageUploadData
import com.erdemyesilcicek.contactapp.data.remote.dto.UserDto
import com.erdemyesilcicek.contactapp.data.remote.dto.UserListData
import com.erdemyesilcicek.contactapp.data.remote.dto.UserRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApi {
    
    @POST("api/User")
    suspend fun createUser(
        @Body request: UserRequest
    ): Response<ApiResponse<UserDto>>
    
    @GET("api/User/{id}")
    suspend fun getUserById(
        @Path("id") id: String
    ): Response<ApiResponse<UserDto>>
    
    @PUT("api/User/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UserRequest
    ): Response<ApiResponse<UserDto>>
    
    @DELETE("api/User/{id}")
    suspend fun deleteUser(
        @Path("id") id: String
    ): Response<ApiResponse<Any>>
    
    @GET("api/User/GetAll")
    suspend fun getAllUsers(): Response<ApiResponse<UserListData>>
    
    @Multipart
    @POST("api/User/UploadImage")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<ApiResponse<ImageUploadData>>
}

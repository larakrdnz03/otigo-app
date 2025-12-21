package com.example.otigoapp.data.api

import com.example.otigoapp.data.model.AuthResponse
import com.example.otigoapp.data.model.LoginRequest
import com.example.otigoapp.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OtigoApiService {

    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("/api/v1/auth/authenticate")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}
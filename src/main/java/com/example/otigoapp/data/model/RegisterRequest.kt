package com.example.otigoapp.data.model

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val role: String // "VELI" veya "UZMAN"
)
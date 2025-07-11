package com.example.secretstash.dto

data class AuthResponse(
    val token: String,
    val tokenType: String = "Bearer"
)
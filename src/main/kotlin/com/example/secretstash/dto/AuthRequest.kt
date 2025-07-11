package com.example.secretstash.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class AuthRequest(
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val password: String
)

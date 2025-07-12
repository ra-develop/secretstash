package com.example.secretstash.dto

import jakarta.validation.constraints.NotBlank

data class NoteDto(
    val id: Long? = null,

    @field:NotBlank
    val title: String,

    @field:NotBlank
    val content: String,

    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val expiresAt: Long? = null
)
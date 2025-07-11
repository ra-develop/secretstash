package com.example.secretstash.controller

import com.example.secretstash.dto.AuthRequest
import com.example.secretstash.dto.AuthResponse
import com.example.secretstash.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody authRequest: AuthRequest): AuthResponse {
        return authService.register(authRequest)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody authRequest: AuthRequest): AuthResponse {
        return authService.login(authRequest)
    }
}
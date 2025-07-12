package com.example.secretstash.service

import com.example.secretstash.dto.AuthRequest
import com.example.secretstash.dto.AuthResponse
import com.example.secretstash.exception.BadRequestException
import com.example.secretstash.security.UserPrincipal
import com.example.secretstash.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService
) {
    fun register(authRequest: AuthRequest): AuthResponse {
        if (userService.existsByEmail(authRequest.email)) {
            throw BadRequestException("Email already in use")
        }

        userService.createUser(authRequest.email, authRequest.password)
        return login(authRequest)
    }

    fun login(authRequest: AuthRequest): AuthResponse {

        try {
            val authenticationToken = UsernamePasswordAuthenticationToken(
                authRequest.email,
                authRequest.password
            )
            val authentication = authenticationManager.authenticate(
                authenticationToken
            )

            val principal = authentication.principal as UserPrincipal
            val token = jwtTokenProvider.generateToken(authentication)

            return AuthResponse(token)
        } catch (e: AuthenticationException) {
            throw BadRequestException("Invalid email/password")
        }
    }
}
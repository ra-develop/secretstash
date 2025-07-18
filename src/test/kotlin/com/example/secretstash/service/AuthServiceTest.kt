package com.example.secretstash.service

import com.example.secretstash.dto.AuthRequest
import com.example.secretstash.dto.AuthResponse
import com.example.secretstash.exception.BadRequestException
import com.example.secretstash.model.User
import com.example.secretstash.repository.UserRepository
import com.example.secretstash.security.JwtTokenProvider
import com.example.secretstash.security.UserPrincipal
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    @Mock
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        authService = AuthService(authenticationManager, jwtTokenProvider, UserService(userRepository, passwordEncoder))
    }

    @Test
    fun `register should return token when new user`() {
        // Given
        val authRequest = AuthRequest("test@example.com", "password123")
        val user = User(1L, "test@example.com", "encodedPassword")
        val userPrincipal = UserPrincipal(1L, "test@example.com", "encodedPassword", emptyList())
        val authentication = mock(Authentication::class.java).apply {
            `when`(principal).thenReturn(userPrincipal)
        }

        // Setup mocks
        `when`(userRepository.existsByEmail("test@example.com")).thenReturn(false)
        `when`(passwordEncoder.encode("password123")).thenReturn("encodedPassword")
        `when`(userRepository.save(any(User::class.java))).thenReturn(user)

        // For login part (if register also logs in)
        `when`(authenticationManager.authenticate(any())).thenReturn(authentication)
        `when`(authentication.principal).thenReturn(userPrincipal)

        // Use argument matcher that will match any authentication with the same principal
        `when`(jwtTokenProvider.generateToken(authentication)).thenReturn("test-token")

        // When
        val result = authService.register(authRequest)

        // Then
        assertEquals("test-token", result.token)
        verify(userRepository).existsByEmail("test@example.com")
        verify(userRepository).save(argThat {
            it.email == "test@example.com" && it.password == "encodedPassword"
        })
    }

    @Test
    fun `register should throw when email exists`() {
        // Given
        val authRequest = AuthRequest("test@example.com", "password123")
        `when`(userRepository.existsByEmail(anyString())).thenReturn(true)

        // When / Then
        assertThrows<BadRequestException> {
            authService.register(authRequest)
        }
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun `login should return token when valid credentials`() {
        // Given
        val authRequest = AuthRequest("test@example.com", "password123")
        val user = User(1L, "test@example.com", "encodedPassword")
        val userPrincipal = UserPrincipal(1L, "test@example.com", "encodedPassword", emptyList())
        val authentication = mock(Authentication::class.java).apply {
            `when`(principal).thenReturn(userPrincipal)
        }

        // For login part (if register also logs in)
        `when`(authenticationManager.authenticate(any())).thenReturn(authentication)
        `when`(authentication.principal).thenReturn(userPrincipal)

        // Use argument matcher that will match any authentication with the same principal
        `when`(jwtTokenProvider.generateToken(authentication)).thenReturn("test-token")

        // When
        val result = authService.login(authRequest)

        // Then
        assertEquals("test-token", result.token)
        verify(authenticationManager).authenticate(
            UsernamePasswordAuthenticationToken("test@example.com", "password123")
        )
    }

    @Test
    fun `login should propagate authentication exception`() {
        // Given
        val authRequest = AuthRequest("test@example.com", "wrong-password")
        `when`(authenticationManager.authenticate(any())).thenThrow(RuntimeException("Bad credentials"))

        // When / Then
        assertThrows<RuntimeException> {
            authService.login(authRequest)
        }
    }

}
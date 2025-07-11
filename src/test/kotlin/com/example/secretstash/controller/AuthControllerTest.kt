package com.example.secretstash.controller

import com.example.secretstash.dto.AuthRequest
import com.example.secretstash.dto.AuthResponse
import com.example.secretstash.service.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(AuthController::class)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var authService: AuthService

    private val objectMapper = ObjectMapper()

    @Test
    fun `register should return token when valid request`() {
        val authRequest = AuthRequest("test@example.com", "password123")
        val authResponse = AuthResponse("test-token")

        `when`(authService.register(authRequest)).thenReturn(authResponse)

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value("test-token"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
    }

    @Test
    fun `register should return 400 when invalid email`() {
        val invalidRequest = AuthRequest("invalid-email", "password123")

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `register should return 400 when empty password`() {
        val invalidRequest = AuthRequest("test@example.com", "")

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `login should return token when valid credentials`() {
        val authRequest = AuthRequest("test@example.com", "password123")
        val authResponse = AuthResponse("test-token")

        `when`(authService.login(authRequest)).thenReturn(authResponse)

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value("test-token"))
    }

    @Test
    fun `login should return 400 when invalid email format`() {
        val invalidRequest = AuthRequest("invalid-email", "password123")

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `login should return 400 when empty password`() {
        val invalidRequest = AuthRequest("test@example.com", "")

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
    }
}
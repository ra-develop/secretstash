package com.example.secretstash.controller

import com.example.secretstash.dto.NoteDto
import com.example.secretstash.service.NoteService
import com.example.secretstash.service.RateLimitingService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.Instant

@WebMvcTest(NoteController::class)
class NoteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var noteService: NoteService

    @MockitoBean
    private lateinit var rateLimitingService: RateLimitingService

    private val objectMapper = ObjectMapper()

    @Test
    @WithMockUser
    fun `createNote should return created note`() {
        val noteDto = NoteDto(null, "Test", "Content", null, null, null)
        val createdNote = NoteDto(1, "Test", "Content", Instant.now().epochSecond, Instant.now().epochSecond, null)

        `when`(noteService.createNote(any(), any())).thenReturn(createdNote)

        mockMvc.perform(
            post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    @WithMockUser
    fun `getNotes should return notes`() {
        val noteDto = NoteDto(1, "Test", "Content", Instant.now().epochSecond, Instant.now().epochSecond, null)
        val page = PageImpl(listOf(noteDto))

        `when`(noteService.getNotes(1)).thenReturn(page)

        mockMvc.perform(get("/api/notes"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].title").value("Test"))
    }
}
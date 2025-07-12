package com.example.secretstash.controller

import com.example.secretstash.dto.NoteDto
import com.example.secretstash.exception.TooManyRequestsException
import com.example.secretstash.security.UserPrincipal
import com.example.secretstash.service.NoteService
import com.example.secretstash.service.RateLimitingService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
@RestController
@RequestMapping("/api/notes")
class NoteController(
    private val noteService: NoteService,
    private val rateLimitingService: RateLimitingService
) {

    @GetMapping
    fun getNotes(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @RequestParam() page: Int?,
        @RequestParam() size: Int?
//        @RequestParam(defaultValue = "0") page: Int,
//        @RequestParam(defaultValue = "10") size: Int
    ): Page<NoteDto> {
        if (page == null || size == null) {
            return noteService.getNotes(userPrincipal.id)
        } else {
            return noteService.getNotesPageable(
                userPrincipal.id,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
            )
        }
    }

    @GetMapping("/latest")
    fun getLatestNotes(@AuthenticationPrincipal userPrincipal: UserPrincipal): List<NoteDto> {
        if (!rateLimitingService.checkRateLimit(userPrincipal.id.toString())) {
            throw TooManyRequestsException("Rate limit exceeded") as Throwable
        }
        return noteService.getLatestNotes(userPrincipal.id)
    }

    @PostMapping
    fun createNote(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody noteDto: NoteDto
    ): NoteDto {
        return noteService.createNote(userPrincipal.id, noteDto)
    }

    @PutMapping("/{id}")
    fun updateNote(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: Long,
        @Valid @RequestBody noteDto: NoteDto
    ): NoteDto {
        return noteService.updateNote(userPrincipal.id, id, noteDto)
    }

    @DeleteMapping("/{id}")
    fun deleteNote(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        noteService.deleteNote(userPrincipal.id, id)
        return ResponseEntity.noContent().build()
    }
}
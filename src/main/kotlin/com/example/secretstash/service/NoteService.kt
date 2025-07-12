package com.example.secretstash.service

import com.example.secretstash.dto.NoteDto
import com.example.secretstash.exception.NotFoundException
import com.example.secretstash.model.Note
import com.example.secretstash.repository.NoteRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val userService: UserService
) {

    fun createNote(userId: Long, noteDto: NoteDto): NoteDto {
        val user = userService.getUserById(userId)
        val note = Note(
            title = noteDto.title,
            content = noteDto.content,
            user = user,
            expiresAt = noteDto.expiresAt?.let { Instant.ofEpochSecond(it) }
        )
        val savedNote = noteRepository.save(note)
        return savedNote.toDto()
    }

    fun getNotes(userId: Long): Page<NoteDto> {

        val allNotes = noteRepository.findFirst1000ByUserIdOrderByCreatedAtDesc(userId)

        val pageNumber = 0 // Second page (0-indexed)
        val pageSize = allNotes.size
        val pageable: Pageable = PageRequest.of(pageNumber, pageSize)

        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(allNotes.size)
        val subList = if (start >= end) emptyList() else allNotes.subList(start, end)

        val itemPage: Page<Note> = PageImpl(subList, pageable, allNotes.size.toLong())

        return itemPage.map { it.toDto() }
    }

    fun getNotesPageable(userId: Long, pageable: Pageable): Page<NoteDto> {
        val currentTime = Instant.ofEpochSecond(0) // .now()
        return noteRepository
            .findByUserIdAndExpiresAtAfterOrExpiresAtIsNull(
                userId,
                currentTime,
                pageable
            )
            .map { it.toDto() }
    }

    fun getLatestNotes(userId: Long): List<NoteDto> {
        return noteRepository
            .findFirst1000ByUserIdOrderByCreatedAtDesc(userId)
            .filter { it.expiresAt != null || it.expiresAt?.isAfter(Instant.now()) == true }
            .map { it.toDto() }
    }

    fun updateNote(userId: Long, noteId: Long, noteDto: NoteDto): NoteDto {
        val note = noteRepository.findByIdAndUserId(noteId, userId)
            .orElseThrow { NotFoundException("Note not found") }

        note.title = noteDto.title
        note.content = noteDto.content
        note.expiresAt = noteDto.expiresAt?.let { Instant.ofEpochSecond(it) }
        note.updatedAt = Instant.now()

        return noteRepository.save(note).toDto()
    }

    fun deleteNote(userId: Long, noteId: Long) {
        if (!noteRepository.existsByIdAndUserId(noteId, userId)) {
            throw NotFoundException("Note not found")
        }
        noteRepository.deleteById(noteId)
    }

    private fun Note.toDto(): NoteDto {
        return NoteDto(
            id = id,
            title = title,
            content = content,
            createdAt = createdAt.epochSecond,
            updatedAt = updatedAt.epochSecond,
            expiresAt = expiresAt?.epochSecond
        )
    }
}
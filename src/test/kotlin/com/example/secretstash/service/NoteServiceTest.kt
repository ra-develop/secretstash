package com.example.secretstash.service

import com.example.secretstash.dto.NoteDto
import com.example.secretstash.exception.NotFoundException
import com.example.secretstash.model.Note
import com.example.secretstash.model.User
import com.example.secretstash.repository.NoteRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class NoteServiceTest {

    @Mock
    private lateinit var noteRepository: NoteRepository

    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var noteService: NoteService

    private val testUser = User(1L, "user@example.com", "encodedPassword")

    @Test
    fun `createNote should save and return note`() {
        // Given
        val noteDto = NoteDto(
            title = "Test Note",
            content = "Test Content",
            expiresAt = Instant.now().plusSeconds(3600).epochSecond
        )
        val savedNote = Note(
            id = 1L,
            title = "Test Note",
            content = "Test Content",
            user = testUser,
            expiresAt = Instant.ofEpochSecond(noteDto.expiresAt!!)
        )

        `when`(userService.getUserById(1L)).thenReturn(testUser)
        `when`(noteRepository.save(any(Note::class.java))).thenReturn(savedNote)

        // When
        val result = noteService.createNote(1L, noteDto)

        // Then
        assertEquals(1L, result.id)
        assertEquals("Test Note", result.title)
        assertEquals("Test Content", result.content)
        assertNotNull(result.expiresAt)
        verify(noteRepository).save(any(Note::class.java))
    }

    @Test
    fun `getNotes should return active notes`() {
        // Given
        val activeNote = Note(
            id = 1L,
            title = "Active Note",
            content = "Content",
            user = testUser,
            expiresAt = Instant.now().plusSeconds(3600)
        )
        val expiredNote = Note(
            id = 2L,
            title = "Expired Note",
            content = "Content",
            user = testUser,
            expiresAt = Instant.now().minusSeconds(3600)
        )
        val pageable: Pageable = PageRequest.of(0, 10)
        val activeNotes = listOf(activeNote)

        `when`(noteRepository.findByUserIdAndExpiresAtAfterOrExpiresAtIsNull(
            eq(1L),
            any(Instant::class.java),
            eq(pageable)
        )).thenReturn(PageImpl(activeNotes, pageable, 1))

        // When
        val result = noteService.getNotes(1L)

        // Then
        assertEquals(1, result.totalElements)
        assertEquals("Active Note", result.content[0].title)
        verify(noteRepository).findByUserIdAndExpiresAtAfterOrExpiresAtIsNull(
            eq(1L),
            any(Instant::class.java),
            eq(pageable)
        )
    }

    @Test
    fun `getLatestNotes should return max 1000 notes`() {
        // Given
        val notes = (1..1000).map {
            Note(
                id = it.toLong(),
                title = "Note $it",
                content = "Content $it",
                user = testUser,
                expiresAt = if (it % 2 == 0) Instant.now().plusSeconds(3600) else null
            )
        }

        `when`(noteRepository.findFirst1000ByUserIdOrderByCreatedAtDesc(1L)).thenReturn(notes)

        // When
        val result = noteService.getLatestNotes(1L)

        // Then
        assertEquals(1000, result.size)
        assertTrue(result.all { it.expiresAt == null || it.expiresAt!! > Instant.now().epochSecond })
        verify(noteRepository).findFirst1000ByUserIdOrderByCreatedAtDesc(1L)
    }

    @Test
    fun `updateNote should update existing note`() {
        // Given
        val existingNote = Note(
            id = 1L,
            title = "Old Title",
            content = "Old Content",
            user = testUser
        )
        val updatedDto = NoteDto(
            title = "New Title",
            content = "New Content",
            expiresAt = Instant.now().plusSeconds(7200).epochSecond
        )

        `when`(noteRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingNote))
        `when`(noteRepository.save(any(Note::class.java))).thenAnswer { it.arguments[0] as Note }

        // When
        val result = noteService.updateNote(1L, 1L, updatedDto)

        // Then
        assertEquals(1L, result.id)
        assertEquals("New Title", result.title)
        assertEquals("New Content", result.content)
        assertNotNull(result.expiresAt)
        verify(noteRepository).findByIdAndUserId(1L, 1L)
        verify(noteRepository).save(existingNote)
    }

    @Test
    fun `updateNote should throw when note not found`() {
        // Given
        val updatedDto = NoteDto(title = "Title", content = "Content")
        `when`(noteRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty())

        // When / Then
        assertThrows<NotFoundException> {
            noteService.updateNote(1L, 1L, updatedDto)
        }
        verify(noteRepository, never()).save(any(Note::class.java))
    }

    @Test
    fun `deleteNote should delete existing note`() {
        // Given
        `when`(noteRepository.existsByIdAndUserId(1L, 1L)).thenReturn(true)

        // When
        noteService.deleteNote(1L, 1L)

        // Then
        verify(noteRepository).deleteById(1L)
    }

    @Test
    fun `deleteNote should throw when note not found`() {
        // Given
        `when`(noteRepository.existsByIdAndUserId(1L, 1L)).thenReturn(false)

        // When / Then
        assertThrows<NotFoundException> {
            noteService.deleteNote(1L, 1L)
        }
        verify(noteRepository, never()).deleteById(anyLong())
    }

    @Test
    fun `should filter out expired notes in getLatestNotes`() {
        // Given
        val activeNote = Note(
            id = 1L,
            title = "Active",
            content = "Content",
            user = testUser,
            expiresAt = Instant.now().plusSeconds(3600)
        )
        val expiredNote = Note(
            id = 2L,
            title = "Expired",
            content = "Content",
            user = testUser,
            expiresAt = Instant.now().minusSeconds(3600)
        )

        `when`(noteRepository.findFirst1000ByUserIdOrderByCreatedAtDesc(1L))
            .thenReturn(listOf(activeNote, expiredNote))

        // When
        val result = noteService.getLatestNotes(1L)

        // Then
        assertEquals(1, result.size)
        assertEquals("Active", result[0].title)
    }
}
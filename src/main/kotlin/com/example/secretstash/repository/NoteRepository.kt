package com.example.secretstash.repository

import com.example.secretstash.model.Note
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.*

interface NoteRepository : JpaRepository<Note, Long> {
    fun findByUserIdAndExpiresAtAfterOrExpiresAtIsNull(
        userId: Long,
        currentTime: Instant,
        pageable: Pageable
    ): Page<Note>

    fun findFirst1000ByUserIdOrderByCreatedAtDesc(userId: Long): List<Note>
    fun findByIdAndUserId(id: Long, userId: Long): Optional<Note>
    fun existsByIdAndUserId(id: Long, userId: Long): Boolean
}
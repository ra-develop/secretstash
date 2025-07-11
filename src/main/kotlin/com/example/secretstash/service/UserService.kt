package com.example.secretstash.service

import com.example.secretstash.exception.ResourceNotFoundException
import com.example.secretstash.model.User
import com.example.secretstash.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun createUser(email: String, password: String): User {
        val user = User(
            email = email,
            password = passwordEncoder.encode(password)
        )
        return userRepository.save(user)
    }

    fun getUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found") }
    }

    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow { ResourceNotFoundException("User not found") }
    }

    fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }
}

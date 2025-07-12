package com.example.secretstash.service

import com.example.secretstash.model.User
import com.example.secretstash.repository.UserRepository
import com.example.secretstash.security.UserPrincipal
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service



@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)

        return UserPrincipal(
            user.get().id,
            user.get().email,
            emptyList(),
             // Add authorities/roles if needed
        )
    }
}
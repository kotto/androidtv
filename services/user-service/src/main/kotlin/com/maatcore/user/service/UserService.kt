package com.maatcore.user.service

import com.maatcore.user.model.User
import com.maatcore.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun createUser(email: String, password: String, displayName: String, roles: Set<String>): User {
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already in use")
        }

        val user = User(
            email = email,
            passwordHash = passwordEncoder.encode(password),
            displayName = displayName,
            roles = roles.ifEmpty { setOf("ROLE_END_USER") }
        )

        return userRepository.save(user)
    }

    fun getUserById(id: UUID): Optional<User> {
        return userRepository.findById(id)
    }

    fun getUserByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    fun updateUser(id: UUID, displayName: String?, roles: Set<String>?): User {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found") }

        val updatedUser = user.copy(
            displayName = displayName ?: user.displayName,
            roles = roles ?: user.roles,
            updatedAt = LocalDateTime.now()
        )

        return userRepository.save(updatedUser)
    }

    fun updateLastLogin(id: UUID): User {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found") }

        val updatedUser = user.copy(
            lastLoginAt = LocalDateTime.now()
        )

        return userRepository.save(updatedUser)
    }

    fun deactivateUser(id: UUID): User {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found") }

        val updatedUser = user.copy(
            isActive = false,
            updatedAt = LocalDateTime.now()
        )

        return userRepository.save(updatedUser)
    }

    fun activateUser(id: UUID): User {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found") }

        val updatedUser = user.copy(
            isActive = true,
            updatedAt = LocalDateTime.now()
        )

        return userRepository.save(updatedUser)
    }
}

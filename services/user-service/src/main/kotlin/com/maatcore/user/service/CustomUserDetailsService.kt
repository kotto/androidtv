package com.maatcore.user.service

import com.maatcore.user.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            .orElseThrow { UsernameNotFoundException("User not found with email: $username") }

        if (!user.isActive) {
            throw UsernameNotFoundException("User is deactivated: $username")
        }

        val authorities = user.roles.map { SimpleGrantedAuthority(it) }

        return User(
            user.email,
            user.passwordHash,
            user.isActive,
            true,
            true,
            true,
            authorities
        )
    }
}

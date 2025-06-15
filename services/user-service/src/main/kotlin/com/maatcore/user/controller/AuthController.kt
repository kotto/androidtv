package com.maatcore.user.controller

import com.maatcore.user.model.User
import com.maatcore.user.security.JwtTokenProvider
import com.maatcore.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<JwtAuthResponse> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
        )

        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtTokenProvider.generateToken(authentication)
        
        // Mettre à jour la date de dernière connexion
        val user = userService.getUserByEmail(loginRequest.email).orElseThrow()
        userService.updateLastLogin(user.id)

        return ResponseEntity.ok(JwtAuthResponse(jwt))
    }

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<User> {
        val user = userService.createUser(
            registerRequest.email,
            registerRequest.password,
            registerRequest.displayName,
            registerRequest.roles ?: setOf("ROLE_END_USER")
        )

        return ResponseEntity.ok(user)
    }
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
    val roles: Set<String>? = null
)

data class JwtAuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer"
)

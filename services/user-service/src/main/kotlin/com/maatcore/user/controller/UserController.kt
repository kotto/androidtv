package com.maatcore.user.controller

import com.maatcore.user.model.User
import com.maatcore.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_PLATFORM_ADMIN') or #id == authentication.principal.id")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<User> {
        val user = userService.getUserById(id)
            .orElseThrow { IllegalArgumentException("User not found") }
        return ResponseEntity.ok(user)
    }

    @GetMapping("/me")
    fun getCurrentUser(@RequestAttribute("userId") userId: UUID): ResponseEntity<User> {
        val user = userService.getUserById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }
        return ResponseEntity.ok(user)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_PLATFORM_ADMIN') or #id == authentication.principal.id")
    fun updateUser(
        @PathVariable id: UUID,
        @RequestBody updateRequest: UpdateUserRequest
    ): ResponseEntity<User> {
        val updatedUser = userService.updateUser(
            id,
            updateRequest.displayName,
            updateRequest.roles
        )
        return ResponseEntity.ok(updatedUser)
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ROLE_PLATFORM_ADMIN')")
    fun deactivateUser(@PathVariable id: UUID): ResponseEntity<User> {
        val user = userService.deactivateUser(id)
        return ResponseEntity.ok(user)
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ROLE_PLATFORM_ADMIN')")
    fun activateUser(@PathVariable id: UUID): ResponseEntity<User> {
        val user = userService.activateUser(id)
        return ResponseEntity.ok(user)
    }
}

data class UpdateUserRequest(
    val displayName: String? = null,
    val roles: Set<String>? = null
)

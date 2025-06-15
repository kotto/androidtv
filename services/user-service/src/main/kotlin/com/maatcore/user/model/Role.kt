package com.maatcore.user.model

/**
 * Énumération des rôles disponibles dans le système MaâtCore
 * Conforme au modèle RBAC défini dans l'architecture
 */
enum class Role(val roleName: String) {
    END_USER("ROLE_END_USER"),
    MAATTUBE_CREATOR("ROLE_MAATTUBE_CREATOR"),
    MAATCARE_DOCTOR("ROLE_MAATCARE_DOCTOR"),
    BACKOFFICE_MODERATOR("ROLE_BACKOFFICE_MODERATOR"),
    PLATFORM_ADMIN("ROLE_PLATFORM_ADMIN")
}

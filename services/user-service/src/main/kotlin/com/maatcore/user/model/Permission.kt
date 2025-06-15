package com.maatcore.user.model

/**
 * Énumération des permissions disponibles dans le système MaâtCore
 * Conforme au modèle RBAC défini dans l'architecture
 */
enum class Permission(val permissionName: String) {
    // Permissions MaâtTV
    TV_CONTENT_VIEW("maattv:content:view"),
    TV_CONTENT_MANAGE("maattv:content:manage"),
    
    // Permissions MaâtTube
    TUBE_VIDEO_VIEW("maattube:video:view"),
    TUBE_VIDEO_UPLOAD("maattube:video:upload"),
    TUBE_VIDEO_MANAGE("maattube:video:manage"),
    
    // Permissions MaâtCare
    CARE_CONSULTATION_REQUEST("maatcare:consultation:request"),
    CARE_CONSULTATION_ACCESS_ASSIGNED("maatcare:consultation:access_assigned"),
    CARE_CONSULTATION_MANAGE("maatcare:consultation:manage"),
    
    // Permissions MaâtClass
    CLASS_COURSE_VIEW("maatclass:course:view"),
    CLASS_COURSE_MANAGE("maatclass:course:manage"),
    
    // Permissions Backoffice
    BACKOFFICE_ACCESS("backoffice:access"),
    BACKOFFICE_CONTENT_MODERATE("backoffice:content:moderate"),
    
    // Permissions Admin
    ADMIN_USER_MANAGE("admin:user:manage"),
    ADMIN_SYSTEM_MANAGE("admin:system:manage")
}

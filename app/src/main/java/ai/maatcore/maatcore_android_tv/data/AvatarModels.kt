package ai.maatcore.maatcore_android_tv.data

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

/**
 * Modèles de données pour le système d'avatars IA selon les spécifications UI/UX
 */

/**
 * États possibles d'un avatar IA
 */
enum class AvatarState {
    IDLE,           // État de repos
    LISTENING,      // En écoute (recherche vocale)
    SPEAKING,       // En train de parler
    THINKING,       // En réflexion/traitement
    FOCUSED,        // Focalisé (sélectionné)
    DISABLED        // Désactivé
}

/**
 * Types d'avatars correspondant aux services MaâtCore
 */
enum class AvatarType {
    MAAT_TV,
    MAAT_CARE,
    MAAT_CLASS,
    MAAT_FOOT,
    MAAT_TUBE,
    MAAT_FLIX
}

/**
 * Modèle de données pour un avatar IA
 */
data class AIAvatar(
    val id: String,
    val name: String,
    val type: AvatarType,
    val description: String,
    val welcomeMessage: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val route: String,
    val isEnabled: Boolean = true,
    val currentState: AvatarState = AvatarState.IDLE
)

/**
 * Données des avatars selon les spécifications
 */
object AvatarData {
    
    val maatTvAvatar = AIAvatar(
        id = "avatar_tv",
        name = "Aya",
        type = AvatarType.MAAT_TV,
        description = "Votre guide pour l'actualité et le divertissement",
        welcomeMessage = "Bonjour ! Je suis Aya, votre présentatrice IA. Découvrons ensemble l'actualité africaine et mondiale.",
        icon = Icons.Default.Tv,
        primaryColor = Color(0xFF1976D2), // Bleu TV
        secondaryColor = Color(0xFF42A5F5),
        route = "maattv"
    )
    
    val maatCareAvatar = AIAvatar(
        id = "avatar_care",
        name = "Santé",
        type = AvatarType.MAAT_CARE,
        description = "Votre assistant santé et bien-être",
        welcomeMessage = "Salut ! Je suis votre assistant santé. Comment puis-je vous aider aujourd'hui ?",
        icon = Icons.Default.Favorite,
        primaryColor = Color(0xFF388E3C), // Vert santé
        secondaryColor = Color(0xFF66BB6A),
        route = "maatcare"
    )
    
    val maatClassAvatar = AIAvatar(
        id = "avatar_class",
        name = "Prof",
        type = AvatarType.MAAT_CLASS,
        description = "Votre professeur IA personnalisé",
        welcomeMessage = "Bienvenue ! Je suis votre professeur IA. Prêt à apprendre quelque chose de nouveau ?",
        icon = Icons.Default.School,
        primaryColor = Color(0xFFFF9800), // Orange éducation
        secondaryColor = Color(0xFFFFB74D),
        route = "maatclass"
    )
    
    val maatFootAvatar = AIAvatar(
        id = "avatar_foot",
        name = "Coach",
        type = AvatarType.MAAT_FOOT,
        description = "Votre expert football et sport",
        welcomeMessage = "Salut champion ! Prêt pour les dernières nouvelles du football africain ?",
        icon = Icons.Default.SportsSoccer,
        primaryColor = Color(0xFF4CAF50), // Vert terrain
        secondaryColor = Color(0xFF81C784),
        route = "maatfoot"
    )
    
    val maatTubeAvatar = AIAvatar(
        id = "avatar_tube",
        name = "Créa",
        type = AvatarType.MAAT_TUBE,
        description = "Votre guide créatif et divertissement",
        welcomeMessage = "Hey ! Je suis là pour vous aider à créer et découvrir du contenu incroyable.",
        icon = Icons.Default.Movie,
        primaryColor = Color(0xFFE91E63), // Rose créatif
        secondaryColor = Color(0xFFF06292),
        route = "maattube"
    )
    
    val maatFlixAvatar = AIAvatar(
        id = "avatar_flix",
        name = "Flix",
        type = AvatarType.MAAT_FLIX,
        description = "Votre cinéma personnel - Films, séries et documentaires",
        welcomeMessage = "Bienvenue dans votre cinéma ! Découvrez des films, séries et documentaires exceptionnels.",
        icon = Icons.Default.Theaters,
        primaryColor = Color(0xFFE50914), // Rouge Netflix
        secondaryColor = Color(0xFFFF6B6B),
        route = "maatflix"
    )
    
    /**
     * Liste de tous les avatars disponibles
     */
    val allAvatars = listOf(
        maatTvAvatar,
        maatCareAvatar,
        maatClassAvatar,
        maatFootAvatar,
        maatTubeAvatar,
        maatFlixAvatar
    )
    
    /**
     * Obtenir un avatar par son type
     */
    fun getAvatarByType(type: AvatarType): AIAvatar? {
        return allAvatars.find { it.type == type }
    }
    
    /**
     * Obtenir un avatar par son ID
     */
    fun getAvatarById(id: String): AIAvatar? {
        return allAvatars.find { it.id == id }
    }
    

}

/**
 * Modèle pour les interactions avec l'avatar
 */
data class AvatarInteraction(
    val avatarId: String,
    val interactionType: InteractionType,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Types d'interactions possibles
 */
enum class InteractionType {
    VOICE_COMMAND,
    SELECTION,
    FOCUS,
    NAVIGATION,
    GREETING
}

/**
 * Gestionnaire d'état des avatars
 */
class AvatarManager {
    private val _activeAvatarId = mutableStateOf<String?>(null)
    val activeAvatarId: State<String?> = _activeAvatarId
    
    private val _avatarStates = mutableStateOf<Map<String, AvatarState>>(emptyMap())
    val avatarStates: State<Map<String, AvatarState>> = _avatarStates
    
    private val _isVoiceSearchActive = mutableStateOf(false)
    val isVoiceSearchActive: State<Boolean> = _isVoiceSearchActive
    
    private val _lastInteraction = mutableStateOf<AvatarInteraction?>(null)
    val lastInteraction: State<AvatarInteraction?> = _lastInteraction
    
    /**
     * Mettre à jour l'état d'un avatar
     */
    fun updateAvatarState(avatarId: String, newState: AvatarState) {
        _avatarStates.value = _avatarStates.value.toMutableMap().apply {
            put(avatarId, newState)
        }
    }
    
    /**
     * Activer un avatar
     */
    fun activateAvatar(avatarId: String) {
        _activeAvatarId.value = avatarId
        _avatarStates.value = _avatarStates.value.toMutableMap().apply {
            // Désactiver tous les autres avatars
            keys.forEach { id ->
                if (id != avatarId) {
                    put(id, AvatarState.IDLE)
                }
            }
            // Activer l'avatar sélectionné
            put(avatarId, AvatarState.FOCUSED)
        }
    }
    
    /**
     * Démarrer la recherche vocale
     */
    fun startVoiceSearch(avatarId: String) {
        _isVoiceSearchActive.value = true
        _avatarStates.value = _avatarStates.value.toMutableMap().apply {
            put(avatarId, AvatarState.LISTENING)
        }
    }
    
    /**
     * Arrêter la recherche vocale
     */
    fun stopVoiceSearch() {
        _isVoiceSearchActive.value = false
        _avatarStates.value = _avatarStates.value.mapValues { 
            if (it.value == AvatarState.LISTENING) AvatarState.IDLE else it.value 
        }
    }
    
    /**
     * Définir l'état d'un avatar
     */
    fun setAvatarState(avatarId: String, state: AvatarState) {
        _avatarStates.value = _avatarStates.value.toMutableMap().apply {
            put(avatarId, state)
        }
    }
    
}
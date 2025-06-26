@file:OptIn(ExperimentalTvMaterial3Api::class)

package ai.maatcore.maatcore_android_tv.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon as TvIcon
import ai.maatcore.maatcore_android_tv.data.*
import ai.maatcore.maatcore_android_tv.ui.components.*
import ai.maatcore.maatcore_android_tv.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Écran de navigation principal avec avatars IA
 * Implémente les spécifications UI/UX pour l'interface Netflix-like avec touche africaine
 */
@Composable
fun MainNavigationScreen(
    onNavigateToService: (String) -> Unit,
    onVoiceSearchRequested: () -> Unit = {},
    onSettingsRequested: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedAvatarId by remember { mutableStateOf<String?>(null) }
    var focusedAvatarId by remember { mutableStateOf<String?>(null) }
    var isVoiceListening by remember { mutableStateOf(false) }
    var showWelcomeMessage by remember { mutableStateOf(true) }
    
    val avatarManager = remember { AvatarManager() }
    val avatarStates by avatarManager.avatarStates
    val scope = rememberCoroutineScope()
    
    // Animation d'entrée
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
        delay(3000)
        showWelcomeMessage = false
    }
    
    // Gestion des touches globales
    val globalFocusRequester = remember { FocusRequester() }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color.Black
                    )
                )
            )
            .focusRequester(globalFocusRequester)
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.Menu -> {
                            onSettingsRequested()
                            true
                        }
                        Key.Search -> {
                            scope.launch {
                                isVoiceListening = true
                                avatarManager.setAvatarState("maat_tv", AvatarState.LISTENING)
                                onVoiceSearchRequested()
                                delay(3000)
                                avatarManager.setAvatarState("maat_tv", AvatarState.IDLE)
                                isVoiceListening = false
                            }
                            true
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        // Arrière-plan animé avec motifs africains
        AfricanPatternBackground(
            modifier = Modifier.fillMaxSize()
        )
        
        // Contenu principal
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800)) + 
                   slideInVertically(animationSpec = tween(800)) { it / 2 },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // En-tête avec logo et message de bienvenue
                HeaderSection(
                    showWelcomeMessage = showWelcomeMessage,
                    isVoiceListening = isVoiceListening,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Grille des avatars IA
                AvatarGridSection(
                    avatars = AvatarData.allAvatars,
                    selectedAvatarId = selectedAvatarId,
                    focusedAvatarId = focusedAvatarId,
                    avatarStates = avatarStates,
                    onAvatarClick = { avatar ->
                        selectedAvatarId = avatar.id
                        scope.launch {
                            avatarManager.setAvatarState(avatar.id, AvatarState.FOCUSED)
                            delay(500)
                            avatarManager.setAvatarState(avatar.id, AvatarState.IDLE)
                            onNavigateToService(avatar.route)
                        }
                    },
                    onAvatarFocusChanged = { avatar, focused ->
                        focusedAvatarId = if (focused) avatar.id else null
                        if (focused) {
                            scope.launch {
                                avatarManager.setAvatarState(avatar.id, AvatarState.FOCUSED)
                            }
                        } else {
                            scope.launch {
                                avatarManager.setAvatarState(avatar.id, AvatarState.IDLE)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Barre d'actions en bas
                BottomActionBar(
                    onVoiceSearch = {
                        scope.launch {
                            isVoiceListening = true
                            avatarManager.setAvatarState("maat_tv", AvatarState.LISTENING)
                            onVoiceSearchRequested()
                            delay(3000)
                            avatarManager.setAvatarState("maat_tv", AvatarState.IDLE)
                            isVoiceListening = false
                        }
                    },
                    onSettings = onSettingsRequested,
                    isVoiceListening = isVoiceListening
                )
            }
        }
        
        // Overlay de recherche vocale
        if (isVoiceListening) {
            VoiceSearchOverlay(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    
    // Focus initial
    LaunchedEffect(Unit) {
        delay(500)
        globalFocusRequester.requestFocus()
    }
}

/**
 * Section d'en-tête avec logo et message de bienvenue
 */
@Composable
fun HeaderSection(
    showWelcomeMessage: Boolean,
    isVoiceListening: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo MaâtCore avec animation
        val logoScale by animateFloatAsState(
            targetValue = if (isVoiceListening) 1.1f else 1f,
            animationSpec = spring(dampingRatio = 0.7f),
            label = "logoScale"
        )
        
        Text(
            text = "MaâtCore",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaatGold,
            fontFamily = PoppinsFamily,
            modifier = Modifier.graphicsLayer {
                scaleX = logoScale
                scaleY = logoScale
            }
        )
        
        Text(
            text = "Intelligence Africaine • Sagesse Universelle",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f),
            fontFamily = PoppinsFamily,
            textAlign = TextAlign.Center
        )
        
        // Message de bienvenue animé
        AnimatedVisibility(
            visible = showWelcomeMessage,
            enter = fadeIn(animationSpec = tween(1000, delayMillis = 500)) + 
                   slideInVertically(animationSpec = tween(1000, delayMillis = 500)),
            exit = fadeOut(animationSpec = tween(500)) + 
                  slideOutVertically(animationSpec = tween(500))
        ) {
            Card(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .widthIn(max = 600.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    text = "Bienvenue dans l'écosystème MaâtCore.\nChoisissez votre assistant IA pour commencer votre expérience.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    fontFamily = PoppinsFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}

/**
 * Section de la grille d'avatars
 */
@Composable
fun AvatarGridSection(
    avatars: List<AIAvatar>,
    selectedAvatarId: String?,
    focusedAvatarId: String?,
    avatarStates: Map<String, AvatarState>,
    onAvatarClick: (AIAvatar) -> Unit,
    onAvatarFocusChanged: (AIAvatar, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        items(avatars) { avatar ->
            val isSelected = avatar.id == selectedAvatarId
            val isFocused = avatar.id == focusedAvatarId
            val currentState = avatarStates[avatar.id] ?: AvatarState.IDLE
            
            AvatarWithInfo(
                avatar = avatar,
                currentState = currentState,
                isSelected = isSelected,
                isFocused = isFocused,
                focusRequester = remember { FocusRequester() },
                onClick = { onAvatarClick(avatar) },
                onFocusChanged = { focused -> 
                    onAvatarFocusChanged(avatar, focused) 
                },
                modifier = Modifier// .animateItemPlacement() // Not available in current Compose version
            )
        }
    }
}

/**
 * Barre d'actions en bas de l'écran
 */
@Composable
fun BottomActionBar(
    onVoiceSearch: () -> Unit,
    onSettings: () -> Unit,
    isVoiceListening: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bouton recherche vocale
        ActionButton(
            icon = Icons.Default.Mic,
            label = "Recherche Vocale",
            isActive = isVoiceListening,
            onClick = onVoiceSearch,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isVoiceListening) Color.Red else MaatGold,
                contentColor = Color.White
            )
        )
        
        // Bouton paramètres
        ActionButton(
            icon = Icons.Default.Settings,
            label = "Paramètres",
            onClick = onSettings
        )
    }
}

/**
 * Bouton d'action personnalisé
 */
@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaatGold,
        contentColor = Color.White
    ),
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "buttonScale"
    )
    
    Button(
        onClick = onClick,
        colors = colors,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TvIcon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = PoppinsFamily
            )
        }
    }
}

/**
 * Overlay pour la recherche vocale
 */
@Composable
fun VoiceSearchOverlay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animation de microphone
                val micScale by animateFloatAsState(
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = EaseInOut),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "micScale"
                )
                
                TvIcon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Microphone",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(64.dp)
                        .graphicsLayer {
                            scaleX = micScale
                            scaleY = micScale
                        }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Écoute en cours...",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = PoppinsFamily
                )
                
                Text(
                    text = "Dites votre recherche",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = PoppinsFamily
                )
            }
        }
    }
}

/**
 * Arrière-plan avec motifs africains
 */
@Composable
fun AfricanPatternBackground(
    modifier: Modifier = Modifier
) {
    // Implémentation simplifiée - peut être enrichie avec des motifs SVG
    Box(
        modifier = modifier
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaatGold.copy(alpha = 0.1f),
                        Color.Transparent,
                        MaatRed.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    radius = 800f
                )
            )
    )
}
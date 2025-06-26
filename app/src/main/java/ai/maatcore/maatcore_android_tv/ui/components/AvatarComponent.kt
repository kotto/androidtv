@file:OptIn(ExperimentalTvMaterial3Api::class)

package ai.maatcore.maatcore_android_tv.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon as TvIcon
import ai.maatcore.maatcore_android_tv.data.AIAvatar
import ai.maatcore.maatcore_android_tv.data.AvatarState
import ai.maatcore.maatcore_android_tv.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Composant Avatar IA animé selon les spécifications UI/UX
 */
@Composable
fun AvatarComponent(
    avatar: AIAvatar,
    currentState: AvatarState = AvatarState.IDLE,
    isSelected: Boolean = false,
    isFocused: Boolean = false,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onClick: () -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var localFocused by remember { mutableStateOf(false) }
    
    // Animations pour les différents états
    val scale by animateFloatAsState(
        targetValue = when {
            localFocused -> 1.15f
            isSelected -> 1.08f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 300f
        ),
        label = "avatarScale"
    )
    
    val elevation by animateFloatAsState(
        targetValue = when {
            localFocused -> 12f
            isSelected -> 8f
            else -> 4f
        },
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 400f
        ),
        label = "avatarElevation"
    )
    
    // Animation de pulsation pour l'état LISTENING
    val pulseScale by animateFloatAsState(
        targetValue = if (currentState == AvatarState.LISTENING) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    // Couleur de bordure selon l'état
    val borderColor by animateColorAsState(
        targetValue = when {
            currentState == AvatarState.LISTENING -> Color.Red
            currentState == AvatarState.SPEAKING -> Color.Green
            currentState == AvatarState.THINKING -> Color.Yellow
            localFocused -> avatar.primaryColor
            isSelected -> avatar.secondaryColor
            else -> Color.Transparent
        },
        animationSpec = tween(300),
        label = "borderColor"
    )
    
    // Animation de rotation pour l'état THINKING
    val rotation by animateFloatAsState(
        targetValue = if (currentState == AvatarState.THINKING) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Card(
        onClick = onClick,
        modifier = modifier
            .size(120.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                localFocused = focusState.isFocused
                onFocusChanged(focusState.isFocused)
            }
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionCenter, Key.Enter -> {
                            onClick()
                            true
                        }
                        else -> false
                    }
                } else false
            }
            .scale(scale)
            .graphicsLayer {
                shadowElevation = elevation
                rotationZ = if (currentState == AvatarState.THINKING) rotation else 0f
                scaleX = pulseScale
                scaleY = pulseScale
            },
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            avatar.primaryColor.copy(alpha = 0.8f),
                            avatar.secondaryColor.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.3f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 3.dp,
                    color = borderColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Icône de l'avatar
            TvIcon(
                imageVector = avatar.icon,
                contentDescription = avatar.name,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            
            // Indicateur d'état vocal
            if (currentState == AvatarState.LISTENING) {
                VoiceIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                )
            }
            
            // Indicateur de traitement
            if (currentState == AvatarState.THINKING) {
                ThinkingIndicator(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = 8.dp)
                )
            }
        }
    }
}

/**
 * Composant Avatar avec nom et description
 */
@Composable
fun AvatarWithInfo(
    avatar: AIAvatar,
    currentState: AvatarState = AvatarState.IDLE,
    isSelected: Boolean = false,
    isFocused: Boolean = false,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onClick: () -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarComponent(
            avatar = avatar,
            currentState = currentState,
            isSelected = isSelected,
            isFocused = isFocused,
            focusRequester = focusRequester,
            onClick = onClick,
            onFocusChanged = onFocusChanged
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Nom de l'avatar
        Text(
            text = avatar.name,
            color = if (isFocused || isSelected) avatar.primaryColor else Color.White,
            fontSize = 16.sp,
            fontWeight = if (isFocused || isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            fontFamily = PoppinsFamily
        )
        
        // Description avec animation
        AnimatedVisibility(
            visible = isFocused,
            enter = fadeIn(animationSpec = tween(300)) + 
                   expandVertically(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(200)) + 
                  shrinkVertically(animationSpec = tween(200))
        ) {
            Text(
                text = avatar.description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontFamily = PoppinsFamily,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .widthIn(max = 140.dp)
            )
        }
    }
}

/**
 * Indicateur visuel pour l'état d'écoute vocale
 */
@Composable
fun VoiceIndicator(
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voiceAlpha"
    )
    
    Box(
        modifier = modifier
            .size(24.dp)
            .background(
                color = Color.Red.copy(alpha = alpha),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        TvIcon(
            imageVector = androidx.compose.material.icons.Icons.Default.Mic,
            contentDescription = "Écoute vocale",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Indicateur visuel pour l'état de réflexion
 */
@Composable
fun ThinkingIndicator(
    modifier: Modifier = Modifier
) {
    val dots = remember { listOf(0, 1, 2) }
    
    Row(
        modifier = modifier
            .background(
                color = Color.Yellow.copy(alpha = 0.9f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        dots.forEach { index ->
            val alpha by animateFloatAsState(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = index * 200,
                        easing = EaseInOut
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "thinkingDot$index"
            )
            
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = Color.Black.copy(alpha = alpha),
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Grille d'avatars pour la navigation principale
 */
@Composable
fun AvatarGrid(
    avatars: List<AIAvatar>,
    selectedAvatarId: String? = null,
    avatarStates: Map<String, AvatarState> = emptyMap(),
    onAvatarClick: (AIAvatar) -> Unit = {},
    onAvatarFocusChanged: (AIAvatar, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(avatars.size) { index ->
            val avatar = avatars[index]
            val isSelected = avatar.id == selectedAvatarId
            val currentState = avatarStates[avatar.id] ?: AvatarState.IDLE
            
            AvatarWithInfo(
                avatar = avatar,
                currentState = currentState,
                isSelected = isSelected,
                focusRequester = remember { FocusRequester() },
                onClick = { onAvatarClick(avatar) },
                onFocusChanged = { focused -> 
                    onAvatarFocusChanged(avatar, focused) 
                }
            )
        }
    }
}
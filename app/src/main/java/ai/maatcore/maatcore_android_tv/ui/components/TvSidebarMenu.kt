@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package ai.maatcore.maatcore_android_tv.ui.components

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.zIndex
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Icon
import ai.maatcore.maatcore_android_tv.R as AppR
import ai.maatcore.maatcore_android_tv.ui.theme.*

// Simple data class representing an item
data class SidebarItem(
    val id: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String,
    val description: String = ""
)

@Composable
fun TvSidebarMenu(
    items: List<SidebarItem>,
    selectedIndex: Int,
    isExpanded: Boolean,
    onItemSelected: (Int) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
        val focusManager = LocalFocusManager.current

    val sidebarWidth by animateDpAsState(
        targetValue = if (isExpanded) 224.dp else 56.dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f), label = "sidebarWidth"
    )

    val focusRequesters = remember { List(items.size) { FocusRequester() } }

    // Ensure currently selected item has focus when sidebar expands
    LaunchedEffect(isExpanded, selectedIndex) {
        if (isExpanded) {
            focusRequesters[selectedIndex].requestFocus()
        }
    }

    Box(
        modifier = modifier
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                    onExpandedChange(true)
                    true
                } else if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) {
                    onExpandedChange(false)
                    // move focus to main content
                    focusManager.moveFocus(FocusDirection.Right)
                    true
                } else {
                    false
                }
            }
            .width(sidebarWidth)
            .fillMaxHeight()
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaatColorNoirProfond.copy(alpha = 0.98f),
                        MaatColorNoirProfond.copy(alpha = 0.85f),
                        Color.Transparent
                    ),
                    startX = 0f,
                    endX = 700f
                )
            )
            .zIndex(1f)
    ) {
        TvLazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 24.dp, start = 4.dp, end = 4.dp)
        ) {
            items(items.size) { index ->
                SidebarCard(
                    item = items[index],
                    isSelected = selectedIndex == index,
                    isExpanded = isExpanded,
                    focusRequester = focusRequesters[index],
                    onClick = { onItemSelected(index) },
                    onFocus = { focused -> if (focused) onExpandedChange(true) }
                )
            }
        }
    }
}

@Composable
private fun SidebarCard(
    item: SidebarItem,
    isSelected: Boolean,
    isExpanded: Boolean,
    focusRequester: FocusRequester,
    onClick: () -> Unit,
    onFocus: (Boolean) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val bgColor = when {
        isSelected -> MaatColorOrSable.copy(alpha = 0.3f)
        isFocused -> MaatColorOrangeSolaire.copy(alpha = 0.2f)
        else -> Color.Transparent
    }
    val iconTint = when {
        isSelected -> MaatColorOrSable
        isFocused -> MaatColorOrangeSolaire
        else -> Color.White.copy(alpha = 0.8f)
    }

    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused
                onFocus(it.isFocused)
            }
            .focusable(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandHorizontally() + fadeIn(),
                exit = shrinkHorizontally() + fadeOut()
            ) {
                Column(Modifier.padding(start = 12.dp)) {
                    Text(text = item.title, color = iconTint, fontSize = 16.sp)
                    if (item.description.isNotEmpty()) {
                        AnimatedVisibility(visible = isFocused) {
                            Text(
                                text = item.description,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

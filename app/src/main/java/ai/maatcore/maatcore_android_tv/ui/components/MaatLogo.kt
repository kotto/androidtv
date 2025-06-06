package ai.maatcore.maatcore_android_tv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text // Changed to Material 3
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrSable
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorOrangeSolaire
import ai.maatcore.maatcore_android_tv.ui.theme.MaatColorCuivreAncien
import kotlin.math.*

@Composable
fun MaatLogo(
    modifier: Modifier = Modifier,
    size: Float = 48f,
    showText: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Monogramme "M" avec motifs géométriques africains
        Canvas(
            modifier = Modifier.size(size.dp)
        ) {
            drawMaatMonogram(this, size.dp.toPx())
        }
        
        if (showText) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "MAÂTCORE",
                color = MaatColorOrSable,
                fontSize = (size * 0.30f).sp, // Réduit davantage à 0.30f
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp // Supprimé letterSpacing
            )
        }
    }
}

private fun drawMaatMonogram(drawScope: DrawScope, size: Float) {
    with(drawScope) {
        // val center = Offset(size / 2f, size / 2f) // Supprimé car inutilisé
        // val radius = size * 0.4f // Supprimé car inutilisé
        
        // Couleurs dégradées
        val goldGradient = Brush.linearGradient(
            colors = listOf(MaatColorOrSable, MaatColorOrangeSolaire),
            start = Offset(0f, 0f),
            end = Offset(size, size)
        )
        
        // val copperGradient = Brush.linearGradient( // Commenté car inutilisé
        //     colors = listOf(MaatColorCuivreAncien, MaatColorOrSable),
        //     start = Offset(0f, size),
        //     end = Offset(size, 0f)
        // )
        
        // Dessiner le "M" stylisé avec motifs géométriques
        val path = Path().apply {
            // Base du M
            moveTo(size * 0.15f, size * 0.85f)
            lineTo(size * 0.15f, size * 0.25f)
            
            // Première montée
            lineTo(size * 0.35f, size * 0.25f)
            lineTo(size * 0.5f, size * 0.55f)
            
            // Deuxième montée
            lineTo(size * 0.65f, size * 0.25f)
            lineTo(size * 0.85f, size * 0.25f)
            lineTo(size * 0.85f, size * 0.85f)
            
            // Retour côté droit
            lineTo(size * 0.7f, size * 0.85f)
            lineTo(size * 0.7f, size * 0.45f)
            lineTo(size * 0.5f, size * 0.75f)
            lineTo(size * 0.3f, size * 0.45f)
            lineTo(size * 0.3f, size * 0.85f)
            
            close()
        }
        
        // Dessiner le M principal
        drawPath(
            path = path,
            brush = goldGradient
        )
        
        // Ajouter des motifs géométriques africains
        // drawAfricanPatterns(this, size, copperGradient) // Supprimé pour un 'M' simple
        
        // Contour doré
        // drawPath( // Supprimé pour un 'M' simple
        //     path = path,
        //     color = MaatColorOrangeSolaire,
        //     style = Stroke(width = 2.dp.toPx())
        // )
    }
}

private fun drawAfricanPatterns(drawScope: DrawScope, size: Float, brush: Brush) {
    with(drawScope) {
        // Motifs triangulaires inspirés des arts africains
        val triangleSize = size * 0.08f
        
        // Triangles décoratifs autour du M
        for (i in 0..7) {
            val angle = i * PI / 4
            val x = size / 2f + cos(angle) * size * 0.35f
            val y = size / 2f + sin(angle) * size * 0.35f
            
            val trianglePath = Path().apply {
                moveTo(x.toFloat(), (y - triangleSize).toFloat())
                lineTo((x - triangleSize).toFloat(), (y + triangleSize).toFloat())
                lineTo((x + triangleSize).toFloat(), (y + triangleSize).toFloat())
                close()
            }
            
            drawPath(
                path = trianglePath,
                brush = brush,
                alpha = 0.6f
            )
        }
        
        // Lignes géométriques subtiles
        for (i in 0..3) {
            val startX = size * 0.1f
            val endX = size * 0.9f
            val y = size * 0.2f + i * size * 0.2f
            
            drawLine(
                brush = brush,
                start = Offset(startX, y),
                end = Offset(endX, y),
                strokeWidth = 1.dp.toPx(),
                alpha = 0.3f
            )
        }
    }
}

@Composable
fun MaatBrandHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        MaatLogo(size = 64f)
        // Spacer et Text "A Journey of Tradition and Culture" ont été supprimés.
    }
}

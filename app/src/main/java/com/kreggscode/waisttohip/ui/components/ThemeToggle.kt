package com.kreggscode.waisttohip.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.kreggscode.waisttohip.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedThemeToggle(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = isDarkMode, label = "theme_toggle")
    
    val offset by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "offset"
    ) { dark ->
        if (dark) 1f else 0f
    }
    
    val rotation by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "rotation"
    ) { dark ->
        if (dark) 180f else 0f
    }
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isDarkMode) Color(0xFF1E1E2E) else Color(0xFFFFF8E7),
        animationSpec = tween(300),
        label = "background"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isDarkMode) Color(0xFFFDB813) else Color(0xFFFF6B35),
        animationSpec = tween(300),
        label = "icon_color"
    )
    
    Box(
        modifier = modifier
            .width(80.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color = backgroundColor)
            .clickable(onClick = onToggle)
            .padding(4.dp)
    ) {
        // Animated stars for dark mode
        if (isDarkMode) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stars = listOf(
                    Offset(size.width * 0.2f, size.height * 0.3f),
                    Offset(size.width * 0.7f, size.height * 0.2f),
                    Offset(size.width * 0.5f, size.height * 0.7f)
                )
                
                stars.forEach { pos ->
                    drawCircle(
                        color = Color.White.copy(alpha = 0.6f),
                        radius = 1.5f,
                        center = pos
                    )
                }
            }
        }
        
        // Animated clouds for light mode
        if (!isDarkMode) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val clouds = listOf(
                    Offset(size.width * 0.3f, size.height * 0.4f),
                    Offset(size.width * 0.6f, size.height * 0.5f)
                )
                
                clouds.forEach { pos ->
                    drawCircle(
                        color = Color.White.copy(alpha = 0.4f),
                        radius = 4f,
                        center = pos
                    )
                }
            }
        }
        
        // Sliding toggle button
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .offset(x = (36.dp * offset))
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (isDarkMode) {
                            listOf(Color(0xFFFDB813), Color(0xFFFF9500))
                        } else {
                            listOf(Color(0xFFFFD700), Color(0xFFFF6B35))
                        }
                    )
                )
                .rotate(rotation),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                contentDescription = if (isDarkMode) "Dark Mode" else "Light Mode",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun PremiumThemeToggle(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "theme_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Box(
        modifier = modifier
            .size(56.dp)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center
    ) {
        // Animated gradient ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            rotate(rotation) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = if (isDarkMode) {
                            listOf(
                                NeonPurple.copy(alpha = pulseAlpha),
                                NeonCyan.copy(alpha = pulseAlpha),
                                NeonPink.copy(alpha = pulseAlpha),
                                NeonPurple.copy(alpha = pulseAlpha)
                            )
                        } else {
                            listOf(
                                Coral.copy(alpha = pulseAlpha),
                                Lavender.copy(alpha = pulseAlpha),
                                Mint.copy(alpha = pulseAlpha),
                                Coral.copy(alpha = pulseAlpha)
                            )
                        }
                    ),
                    radius = size.width / 2,
                    center = center
                )
            }
        }
        
        // Inner circle with icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                contentDescription = if (isDarkMode) "Dark Mode" else "Light Mode",
                tint = if (isDarkMode) NeonPurple else Coral,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

package com.kreggscode.waisttohip.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.waisttohip.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.*

// Premium Glass Card with blur effect
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    glassColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f),
    borderGradient: List<Color> = listOf(
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    ),
    shadowElevation: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(cornerRadius),
                clip = false,
                ambientColor = GlassShadow,
                spotColor = GlassShadow
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassColor,
                        glassColor.copy(alpha = glassColor.alpha * 0.6f)
                    )
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(colors = borderGradient),
                shape = RoundedCornerShape(cornerRadius)
            ),
        content = content
    )
}

// Ice Cream Scoop Progress Ring - The star of the show!
@Composable
fun IceCreamProgressRing(
    waistProgress: Float,
    hipProgress: Float,
    whrValue: Float,
    whrStatus: String,
    modifier: Modifier = Modifier,
    animationDuration: Int = 900,
    size: Dp = 280.dp
) {
    val animatedWaist by animateFloatAsState(
        targetValue = waistProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "waist"
    )
    
    val animatedHip by animateFloatAsState(
        targetValue = hipProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "hip"
    )
    
    val animatedWhr by animateFloatAsState(
        targetValue = whrValue,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "whr"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "ice_cream_animation")
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
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = 24.dp.toPx()
            val center = Offset(this.size.width / 2, this.size.height / 2)
            
            // Outer ring - Waist (Strawberry)
            drawIceCreamRing(
                center = center,
                radius = this.size.width / 2 - strokeWidth,
                progress = animatedWaist,
                strokeWidth = strokeWidth,
                colors = listOf(Strawberry, Coral, CoralLight),
                rotation = rotation * 0.5f
            )
            
            // Middle ring - Hip (Pistachio)
            drawIceCreamRing(
                center = center,
                radius = this.size.width / 2 - strokeWidth * 2.5f,
                progress = animatedHip,
                strokeWidth = strokeWidth,
                colors = listOf(Pistachio, Mint, MintLight),
                rotation = -rotation * 0.7f
            )
            
            // Inner ring - WHR (Blueberry)
            drawIceCreamRing(
                center = center,
                radius = this.size.width / 2 - strokeWidth * 4f,
                progress = animatedWhr / 2f, // Normalize WHR to 0-1 range
                strokeWidth = strokeWidth,
                colors = listOf(Blueberry, Lavender, LavenderLight),
                rotation = rotation
            )
        }
        
        // Center content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format("%.2f", animatedWhr),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "WHR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (whrStatus) {
                    "Healthy" -> HealthyGreen.copy(alpha = 0.2f)
                    "Moderate" -> ModerateYellow.copy(alpha = 0.2f)
                    else -> HighRisk.copy(alpha = 0.2f)
                }
            ) {
                Text(
                    text = whrStatus,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = when (whrStatus) {
                        "Healthy" -> HealthyGreen
                        "Moderate" -> ModerateYellow
                        else -> HighRisk
                    }
                )
            }
        }
    }
}

// Helper function to draw ice cream scoop-style rings
private fun DrawScope.drawIceCreamRing(
    center: Offset,
    radius: Float,
    progress: Float,
    strokeWidth: Float,
    colors: List<Color>,
    rotation: Float
) {
    val sweepAngle = 360f * progress
    
    // Draw background ring
    drawCircle(
        color = colors[0].copy(alpha = 0.1f),
        radius = radius,
        center = center,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
    )
    
    // Draw progress arc with gradient
    rotate(rotation, center) {
        drawArc(
            brush = Brush.sweepGradient(
                colors = colors,
                center = center
            ),
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )
        
        // Draw ice cream drip effect at the end
        if (progress > 0.1f) {
            val endAngle = Math.toRadians((-90f + sweepAngle).toDouble())
            val dripX = center.x + radius * cos(endAngle).toFloat()
            val dripY = center.y + radius * sin(endAngle).toFloat()
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colors[1], colors[2]),
                    center = Offset(dripX, dripY),
                    radius = strokeWidth * 0.7f
                ),
                radius = strokeWidth * 0.7f,
                center = Offset(dripX, dripY)
            )
        }
    }
}

// Animated Gradient Button
@Composable
fun AnimatedGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = GradientNeon,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    val infiniteTransition = rememberInfiniteTransition(label = "button_gradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )
    
    Surface(
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                clip = false
            )
            .clip(RoundedCornerShape(28.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = Color.White),
                enabled = enabled,
                onClick = onClick
            ),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors,
                        start = Offset(0f, 0f),
                        end = Offset(1000f * gradientOffset, 1000f * gradientOffset)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Floating Glass Bottom Navigation
@Composable
fun FloatingBottomNav(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf("Home", "Measure", "Scan", "History")
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(navBarPadding)
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(64.dp),
            cornerRadius = 32.dp,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    NavItem(
                        label = item,
                        selected = selectedIndex == index,
                        onClick = { onItemSelected(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) Coral else TextMuted,
        animationSpec = tween(300),
        label = "nav_color"
    )
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = when (label) {
                "Home" -> Icons.Rounded.Home
                "Measure" -> Icons.Rounded.Analytics
                "Scan" -> Icons.Rounded.CameraAlt
                "History" -> Icons.Rounded.History
                else -> Icons.Rounded.Home
            },
            contentDescription = label,
            tint = animatedColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = animatedColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

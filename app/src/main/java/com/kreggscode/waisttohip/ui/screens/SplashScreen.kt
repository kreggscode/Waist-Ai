package com.kreggscode.waisttohip.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.kreggscode.waisttohip.ui.components.GlassCard
import com.kreggscode.waisttohip.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    var scoopsVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
        delay(300)
        scoopsVisible = true
        delay(500)
        textVisible = true
        delay(2500)
        onSplashComplete()
    }
    
    // Animated gradient background
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animation")
    
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
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
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Lavender.copy(alpha = 0.3f),
                        Coral.copy(alpha = 0.2f),
                        Mint.copy(alpha = 0.3f),
                        Vanilla
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY * gradientOffset
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating ice cream scoops background
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (scoopsVisible) 1f else 0f)
        ) {
            drawFloatingIceCreamScoops(rotation, gradientOffset)
        }
        
        // Glass morphism card with content
        GlassCard(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .scale(animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "card_scale"
                ).value)
                .alpha(animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0f,
                    animationSpec = tween(500),
                    label = "card_alpha"
                ).value),
            cornerRadius = 32.dp,
            shadowElevation = 24.dp,
            glassColor = GlassWhite.copy(alpha = 0.9f)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated ice cream icon
                IceCreamLogo(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(animateFloatAsState(
                            targetValue = if (scoopsVisible) 1f else 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "logo_scale"
                        ).value)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // App name with gradient
                Text(
                    text = "WaistAI",
                    style = MaterialTheme.typography.displayLarge.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(Coral, Lavender, Mint)
                        )
                    ),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(animateFloatAsState(
                            targetValue = if (textVisible) 1f else 0f,
                            animationSpec = tween(800),
                            label = "title_alpha"
                        ).value)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tagline
                Text(
                    text = "Your AI-Powered Health Coach",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(animateFloatAsState(
                            targetValue = if (textVisible) 1f else 0f,
                            animationSpec = tween(800, delayMillis = 200),
                            label = "subtitle_alpha"
                        ).value)
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Three-step intro with animations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Measure", "Track", "Expert AI").forEachIndexed { index, step ->
                        StepIndicator(
                            text = step,
                            delay = 1000L + (index * 200L),
                            visible = textVisible
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Pulsing dots loader
                PulsingDotsLoader(
                    modifier = Modifier
                        .alpha(animateFloatAsState(
                            targetValue = if (textVisible) 1f else 0f,
                            animationSpec = tween(800, delayMillis = 400),
                            label = "loader_alpha"
                        ).value)
                )
            }
        }
    }
}

@Composable
fun IceCreamLogo(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_animation")
    
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )
    
    Canvas(
        modifier = modifier.offset(y = bounce.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val scoopRadius = size.width / 4
        
        // Bottom scoop - Mint
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(MintLight, Mint, MintDark),
                center = Offset(centerX, centerY + scoopRadius)
            ),
            radius = scoopRadius,
            center = Offset(centerX, centerY + scoopRadius)
        )
        
        // Middle scoop - Strawberry
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(CoralLight, Coral, CoralDark),
                center = Offset(centerX, centerY)
            ),
            radius = scoopRadius,
            center = Offset(centerX, centerY)
        )
        
        // Top scoop - Lavender
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(LavenderLight, Lavender, LavenderDark),
                center = Offset(centerX, centerY - scoopRadius)
            ),
            radius = scoopRadius,
            center = Offset(centerX, centerY - scoopRadius)
        )
        
        // Sparkle effect
        drawCircle(
            color = Color.White.copy(alpha = 0.6f),
            radius = scoopRadius / 4,
            center = Offset(centerX - scoopRadius / 2, centerY - scoopRadius * 1.2f)
        )
    }
}

@Composable
fun StepIndicator(
    text: String,
    delay: Long,
    visible: Boolean
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(visible) {
        if (visible) {
            kotlinx.coroutines.delay(delay)
            isVisible = true
        }
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(animateFloatAsState(
                targetValue = if (isVisible) 1f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "step_scale"
            ).value)
            .alpha(animateFloatAsState(
                targetValue = if (isVisible) 1f else 0f,
                animationSpec = tween(500),
                label = "step_alpha"
            ).value)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = when (text) {
                            "Measure" -> listOf(Coral, CoralLight)
                            "Track" -> listOf(Mint, MintLight)
                            else -> listOf(Lavender, LavenderLight)
                        }
                    ),
                    shape = androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (text) {
                    "Measure" -> "📏"
                    "Track" -> "📊"
                    else -> "🤖"
                },
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PulsingDotsLoader(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots_animation")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 150),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_alpha_$index"
            )
            
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(alpha)
                    .background(
                        color = when (index) {
                            0 -> Coral
                            1 -> Mint
                            else -> Lavender
                        },
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}

private fun DrawScope.drawFloatingIceCreamScoops(rotation: Float, offset: Float) {
    val scoopPositions = listOf(
        Offset(size.width * 0.2f, size.height * 0.3f),
        Offset(size.width * 0.8f, size.height * 0.2f),
        Offset(size.width * 0.15f, size.height * 0.7f),
        Offset(size.width * 0.85f, size.height * 0.6f),
        Offset(size.width * 0.5f, size.height * 0.1f),
        Offset(size.width * 0.5f, size.height * 0.9f)
    )
    
    scoopPositions.forEachIndexed { index, position ->
        val animatedY = position.y + sin(rotation * 0.01f + index) * 20f
        val animatedX = position.x + cos(rotation * 0.01f + index) * 10f
        
        rotate(rotation * (if (index % 2 == 0) 1 else -1), Offset(animatedX, animatedY)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = when (index % 3) {
                        0 -> listOf(Strawberry.copy(alpha = 0.3f), Coral.copy(alpha = 0.1f))
                        1 -> listOf(Pistachio.copy(alpha = 0.3f), Mint.copy(alpha = 0.1f))
                        else -> listOf(Blueberry.copy(alpha = 0.3f), Lavender.copy(alpha = 0.1f))
                    },
                    center = Offset(animatedX, animatedY),
                    radius = 60f
                ),
                radius = 60f,
                center = Offset(animatedX, animatedY)
            )
        }
    }
}

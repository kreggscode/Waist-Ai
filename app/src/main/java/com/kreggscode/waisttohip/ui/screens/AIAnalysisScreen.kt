package com.kreggscode.waisttohip.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.waisttohip.ui.components.*
import com.kreggscode.waisttohip.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AIAnalysisScreen(
    whrValue: Float = 0.84f,
    onBackClick: () -> Unit,
    onChatClick: () -> Unit
) {
    var analysisLoading by remember { mutableStateOf(true) }
    var analysisText by remember { mutableStateOf("") }
    var recommendations by remember { mutableStateOf(listOf<Recommendation>()) }
    
    LaunchedEffect(Unit) {
        delay(1500) // Simulate AI processing
        analysisLoading = false
        analysisText = generateAIAnalysis(whrValue)
        recommendations = generateRecommendations(whrValue)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NeonPurple.copy(alpha = 0.05f),
                        Vanilla,
                        Color.White
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = "AI Analysis",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Rounded.Bookmark,
                            contentDescription = "Save",
                            tint = TextPrimary
                        )
                    }
                }
            }
            
            // AI Avatar and Title
            item {
                AIHeaderSection()
            }
            
            // Analysis Content
            item {
                AnimatedVisibility(
                    visible = !analysisLoading,
                    enter = fadeIn() + expandVertically()
                ) {
                    AnalysisCard(
                        analysisText = analysisText,
                        whrValue = whrValue
                    )
                }
            }
            
            // Loading Animation
            if (analysisLoading) {
                item {
                    AILoadingAnimation()
                }
            }
            
            // Recommendations
            if (!analysisLoading) {
                item {
                    Text(
                        text = "Personalized Recommendations",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                }
                
                recommendations.forEach { recommendation ->
                    item {
                        RecommendationCard(recommendation)
                    }
                }
                
                // Diet Plan CTA
                item {
                    DietPlanCTA(onChatClick)
                }
            }
        }
        
        // Floating Chat Button
        if (!analysisLoading) {
            FloatingChatButton(
                onClick = onChatClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 100.dp)
            )
        }
    }
}

@Composable
fun AIHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated AI Avatar
        val infiniteTransition = rememberInfiniteTransition(label = "ai_avatar")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "avatar_scale"
        )
        
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Gradient circle background
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonPurple.copy(alpha = 0.3f),
                            NeonCyan.copy(alpha = 0.2f),
                            NeonPink.copy(alpha = 0.1f)
                        ),
                        center = center,
                        radius = size.width / 2
                    )
                )
            }
            
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(colors = GradientNeon)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🤖",
                    fontSize = 40.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "AI Expert",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Text(
            text = "Your Personal Health Coach",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AnalysisCard(
    analysisText: String,
    whrValue: Float
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        cornerRadius = 24.dp,
        borderGradient = GradientNeon.map { it.copy(alpha = 0.2f) },
        glassColor = GlassWhite.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Risk Assessment Badge
            val riskLevel = when {
                whrValue < 0.85f -> "Low Risk" to HealthyGreen
                whrValue < 0.95f -> "Moderate Risk" to ModerateYellow
                else -> "High Risk" to HighRisk
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = riskLevel.second.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(riskLevel.second)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = riskLevel.first,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = riskLevel.second
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Analysis Text with typing animation
            var displayedText by remember { mutableStateOf("") }
            LaunchedEffect(analysisText) {
                analysisText.forEachIndexed { index, _ ->
                    delay(10)
                    displayedText = analysisText.substring(0, index + 1)
                }
            }
            
            Text(
                text = displayedText,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Key Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricChip(
                    label = "Calorie Target",
                    value = "2000",
                    unit = "kcal",
                    icon = Icons.Rounded.LocalFireDepartment,
                    color = Coral
                )
                MetricChip(
                    label = "Protein Goal",
                    value = "120",
                    unit = "g",
                    icon = Icons.Rounded.FitnessCenter,
                    color = Mint
                )
            }
        }
    }
}

@Composable
fun MetricChip(
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text(
                        text = " $unit",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun AILoadingAnimation() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated brain/processing visual
        val infiniteTransition = rememberInfiniteTransition(label = "loading")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )
        
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotation)
            ) {
                val strokeWidth = 4.dp.toPx()
                
                // Draw multiple rotating arcs
                listOf(
                    NeonPurple to 0f,
                    NeonCyan to 120f,
                    NeonPink to 240f
                ).forEach { (color, startAngle) ->
                    drawArc(
                        color = color.copy(alpha = 0.6f),
                        startAngle = startAngle + rotation,
                        sweepAngle = 90f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }
            
            Text(
                text = "🧠",
                fontSize = 40.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Analyzing your data...",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Pulsing dots
        Row(
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
                    label = "dot_$index"
                )
                
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(alpha)
                        .background(NeonPurple, CircleShape)
                )
            }
        }
    }
}

@Composable
fun RecommendationCard(recommendation: Recommendation) {
    var expanded by remember { mutableStateOf(false) }
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { expanded = !expanded },
        cornerRadius = 20.dp,
        glassColor = recommendation.color.copy(alpha = 0.05f),
        borderGradient = listOf(
            recommendation.color.copy(alpha = 0.3f),
            recommendation.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(recommendation.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = recommendation.icon,
                        fontSize = 20.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = recommendation.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = recommendation.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = recommendation.color
                )
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = recommendation.details,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                    
                    if (recommendation.actionItems.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        recommendation.actionItems.forEach { item ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "•",
                                    color = recommendation.color,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DietPlanCTA(onChatClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clickable { onChatClick() },
        cornerRadius = 24.dp,
        borderGradient = GradientNeon,
        glassColor = NeonPurple.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🍽️",
                fontSize = 48.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Get Your Personalized Diet Plan",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Chat with AI Expert to create a custom meal plan tailored to your goals and preferences",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            AnimatedGradientButton(
                text = "Start Chat",
                onClick = onChatClick,
                modifier = Modifier.fillMaxWidth(),
                gradientColors = GradientNeon
            )
        }
    }
}

@Composable
fun FloatingChatButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chat_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chat_scale"
    )
    
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.scale(scale),
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    brush = Brush.linearGradient(colors = GradientNeon),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Chat,
                contentDescription = "Chat",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

data class Recommendation(
    val title: String,
    val subtitle: String,
    val details: String,
    val icon: String,
    val color: Color,
    val actionItems: List<String> = emptyList()
)

private fun generateAIAnalysis(whrValue: Float): String {
    return when {
        whrValue < 0.85f -> """
            Excellent news! Your waist-to-hip ratio of ${String.format("%.2f", whrValue)} is within the healthy range. 
            This indicates a lower risk of cardiovascular disease and metabolic disorders. 
            
            Your body composition suggests good fat distribution, which is associated with better insulin sensitivity 
            and overall metabolic health. Keep up the great work with your current lifestyle habits!
            
            To maintain this healthy ratio, I recommend continuing with regular physical activity and a balanced diet 
            rich in whole foods. Your current trajectory is positive, and small consistent efforts will help you 
            maintain these excellent results.
        """.trimIndent()
        
        whrValue < 0.95f -> """
            Your waist-to-hip ratio of ${String.format("%.2f", whrValue)} falls in the moderate range. 
            While not immediately concerning, there's room for improvement to optimize your health outcomes.
            
            This ratio suggests some central adiposity, which can be addressed through targeted lifestyle modifications. 
            The good news is that even small changes can lead to significant improvements in your WHR and overall health.
            
            I recommend focusing on increasing your daily activity levels and making strategic dietary adjustments. 
            With consistent effort, you can move into the healthy range within 2-3 months.
        """.trimIndent()
        
        else -> """
            Your waist-to-hip ratio of ${String.format("%.2f", whrValue)} indicates an elevated health risk that deserves attention. 
            This suggests higher central adiposity, which is associated with increased risk of metabolic conditions.
            
            However, this is completely reversible with the right approach! Many people have successfully improved their WHR 
            through sustainable lifestyle changes. You're taking the first step by measuring and being aware.
            
            I strongly recommend implementing a structured plan combining dietary modifications, regular physical activity, 
            and stress management. Small, consistent changes will compound over time to create significant improvements.
        """.trimIndent()
    }
}

private fun generateRecommendations(whrValue: Float): List<Recommendation> {
    val baseRecommendations = listOf(
        Recommendation(
            title = "Nutrition Optimization",
            subtitle = "Personalized meal planning",
            details = "Focus on whole foods, lean proteins, and fiber-rich vegetables. Aim for a moderate caloric deficit if needed, prioritizing nutrient density over restriction.",
            icon = "🥗",
            color = Mint,
            actionItems = listOf(
                "Increase protein to 0.8-1g per lb body weight",
                "Add 2-3 servings of vegetables per meal",
                "Stay hydrated with 8-10 glasses of water daily"
            )
        ),
        Recommendation(
            title = "Exercise Protocol",
            subtitle = "Targeted fitness routine",
            details = "Combine strength training with cardiovascular exercise for optimal results. Focus on compound movements and progressive overload.",
            icon = "💪",
            color = Coral,
            actionItems = listOf(
                "3-4 strength training sessions per week",
                "150 minutes of moderate cardio weekly",
                "Include core strengthening exercises"
            )
        ),
        Recommendation(
            title = "Sleep & Recovery",
            subtitle = "Optimize rest for results",
            details = "Quality sleep is crucial for hormonal balance and recovery. Aim for 7-9 hours of consistent sleep nightly.",
            icon = "😴",
            color = Lavender,
            actionItems = listOf(
                "Maintain consistent sleep schedule",
                "Create a relaxing bedtime routine",
                "Limit screen time before bed"
            )
        )
    )
    
    return if (whrValue >= 0.95f) {
        baseRecommendations + Recommendation(
            title = "Medical Consultation",
            subtitle = "Professional health assessment",
            details = "Consider scheduling a check-up with your healthcare provider to assess metabolic health markers and create a comprehensive health plan.",
            icon = "⚕️",
            color = NeonPurple,
            actionItems = listOf(
                "Schedule annual physical exam",
                "Discuss metabolic health screening",
                "Review current medications if applicable"
            )
        )
    } else {
        baseRecommendations
    }
}

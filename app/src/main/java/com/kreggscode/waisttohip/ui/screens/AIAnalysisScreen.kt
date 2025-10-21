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
            Great! Your waist-to-hip ratio of ${String.format("%.2f", whrValue)} is ${String.format("%.2f", whrValue)}. 
            
            Based on general fitness guidelines, this measurement falls within commonly referenced ranges. 
            Keep tracking your measurements to monitor your progress over time!
            
            Consider maintaining your current activity levels and nutrition habits. Regular physical activity 
            and a balanced diet can help you stay on track with your fitness goals.
            
            *Disclaimer: This app provides informational content only and is not medical advice. 
            Consult a healthcare professional for personalized health guidance.*
        """.trimIndent()
        
        whrValue < 0.95f -> """
            Your waist-to-hip ratio measurement is ${String.format("%.2f", whrValue)}. 
            
            You might consider focusing on your fitness routine and nutrition tracking. Many people find 
            that consistent exercise and mindful eating help them achieve their body composition goals.
            
            Try increasing your daily activity levels and tracking your meals to see what works best for you. 
            Small, sustainable changes often lead to the best long-term results.
            
            *Disclaimer: This app provides informational content only and is not medical advice. 
            Consult a healthcare professional for personalized health guidance.*
        """.trimIndent()
        
        else -> """
            Your current waist-to-hip ratio measurement is ${String.format("%.2f", whrValue)}. 
            
            This is a good starting point for tracking your fitness journey! Many people successfully improve 
            their measurements through consistent lifestyle changes. You're taking a positive step by measuring and monitoring.
            
            Consider creating a structured fitness and nutrition plan. Combining regular exercise, balanced meals, 
            and adequate rest can help you work toward your goals over time.
            
            *Disclaimer: This app provides informational content only and is not medical advice. 
            Consult a healthcare professional for personalized health guidance.*
        """.trimIndent()
    }
}

private fun generateRecommendations(whrValue: Float): List<Recommendation> {
    return listOf(
        Recommendation(
            title = "Nutrition Tracking",
            subtitle = "Monitor your meals",
            details = "Track your daily food intake to understand your eating patterns. Focus on whole foods, lean proteins, and fiber-rich vegetables for balanced nutrition.",
            icon = "🥗",
            color = Mint,
            actionItems = listOf(
                "Log your meals consistently",
                "Include variety in your diet",
                "Stay hydrated throughout the day"
            )
        ),
        Recommendation(
            title = "Fitness Routine",
            subtitle = "Stay active",
            details = "Regular physical activity is an important part of a healthy lifestyle. Consider combining different types of exercise for variety and enjoyment.",
            icon = "💪",
            color = Coral,
            actionItems = listOf(
                "Find activities you enjoy",
                "Set realistic fitness goals",
                "Track your workout progress"
            )
        ),
        Recommendation(
            title = "Rest & Recovery",
            subtitle = "Prioritize sleep",
            details = "Quality rest is an essential part of any fitness routine. Aim for consistent sleep patterns to support your overall wellness goals.",
            icon = "😴",
            color = Lavender,
            actionItems = listOf(
                "Maintain a regular sleep schedule",
                "Create a relaxing evening routine",
                "Limit screen time before bed"
            )
        ),
        Recommendation(
            title = "Progress Tracking",
            subtitle = "Monitor your journey",
            details = "Keep track of your measurements over time to see how your lifestyle changes are working for you. Remember, progress takes time and consistency.",
            icon = "📊",
            color = NeonPurple,
            actionItems = listOf(
                "Take regular measurements",
                "Log your progress in the app",
                "Celebrate small wins along the way"
            )
        )
    )
}

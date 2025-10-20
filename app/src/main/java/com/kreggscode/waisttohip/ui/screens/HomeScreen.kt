package com.kreggscode.waisttohip.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.waisttohip.ui.components.*
import com.kreggscode.waisttohip.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMeasureClick: () -> Unit,
    onScanClick: () -> Unit,
    onQuickAddClick: () -> Unit,
    onChatClick: () -> Unit,
    themeManager: com.kreggscode.waisttohip.data.ThemeManager,
    isDarkMode: Boolean,
    onManualAddClick: () -> Unit = {},
    viewModel: com.kreggscode.waisttohip.ui.viewmodels.HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val recentMeals by viewModel.recentMeals.collectAsState()
    val recentMeasurements by viewModel.recentMeasurements.collectAsState()
    var greeting by remember { mutableStateOf(getGreeting()) }
    val scope = rememberCoroutineScope()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Premium Header with Greeting and Theme Toggle
            item {
                PremiumHeader(
                    greeting = greeting,
                    isDarkMode = isDarkMode,
                    onThemeToggle = {
                        scope.launch {
                            themeManager.toggleTheme()
                        }
                    }
                )
            }
            
            // Stats Cards Row
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AnimatedStatCard(
                            title = "Waist",
                            value = "32",
                            unit = "inches",
                            icon = Icons.Rounded.Straighten,
                            gradient = listOf(Coral, CoralLight),
                            trend = "+0.5",
                            isPositive = false
                        )
                    }
                    item {
                        AnimatedStatCard(
                            title = "Hip",
                            value = "38",
                            unit = "inches",
                            icon = Icons.Rounded.Height,
                            gradient = listOf(Mint, MintLight),
                            trend = "-0.2",
                            isPositive = true
                        )
                    }
                    item {
                        AnimatedStatCard(
                            title = "WHR",
                            value = "0.84",
                            unit = "ratio",
                            icon = Icons.Rounded.Analytics,
                            gradient = listOf(Lavender, LavenderLight),
                            trend = "Healthy",
                            isPositive = true
                        )
                    }
                }
            }
            
            // Calorie Summary Strip
            item {
                CalorieSummaryStrip(
                    consumed = 1450,
                    target = 2000,
                    protein = 65,
                    carbs = 180,
                    fat = 45
                )
            }
            
            // Quick Actions
            item {
                QuickActionsSection(
                    onScanClick = onScanClick,
                    onQuickAddClick = onQuickAddClick,
                    onManualAddClick = onManualAddClick
                )
            }
            
            // Recent Meals
            item {
                RecentMealsSection(meals = recentMeals)
            }
            
            // AI Insights Card
            item {
                AIInsightsCard(onChatClick = onChatClick)
            }
        }
        
        // Floating Measure FAB
        FloatingMeasureFAB(
            onClick = onMeasureClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 140.dp)
        )
    }
}

@Composable
fun PremiumHeader(
    greeting: String,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Let's track your progress",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Theme Toggle
            PremiumThemeToggle(
                isDarkMode = isDarkMode,
                onToggle = onThemeToggle
            )
        }
    }
}

@Composable
fun AnimatedStatCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    gradient: List<Color>,
    trend: String,
    isPositive: Boolean
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "stat_scale"
    )
    
    GlassCard(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp)
            .scale(scale),
        cornerRadius = 24.dp,
        glassColor = GlassWhite.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(colors = gradient)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                if (trend.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPositive) HealthyGreen.copy(alpha = 0.1f) 
                               else HighRisk.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = trend,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isPositive) HealthyGreen else HighRisk,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CalorieSummaryStrip(
    consumed: Int,
    target: Int,
    protein: Int,
    carbs: Int,
    fat: Int
) {
    val progress = (consumed.toFloat() / target).coerceIn(0f, 1f)
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        cornerRadius = 20.dp,
        glassColor = GlassWhite.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Today's Calories",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = consumed.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = " / $target kcal",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                
                // Circular progress indicator
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = when {
                            progress < 0.8f -> HealthyGreen
                            progress < 1f -> ModerateYellow
                            else -> HighRisk
                        },
                        strokeWidth = 6.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Macros row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroIndicator("Protein", protein, Coral)
                MacroIndicator("Carbs", carbs, Mint)
                MacroIndicator("Fat", fat, Lavender)
            }
        }
    }
}

@Composable
fun MacroIndicator(
    label: String,
    value: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${value}g",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun QuickActionsSection(
    onScanClick: () -> Unit,
    onQuickAddClick: () -> Unit,
    onManualAddClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                icon = Icons.Rounded.CameraAlt,
                label = "Scan Food",
                gradient = listOf(Coral, CoralLight),
                onClick = onScanClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                icon = Icons.Rounded.PhotoLibrary,
                label = "From Gallery",
                gradient = listOf(Mint, MintLight),
                onClick = onQuickAddClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                icon = Icons.Rounded.Restaurant,
                label = "Manual Add",
                gradient = listOf(Lavender, LavenderLight),
                onClick = onManualAddClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradient.map { it.copy(alpha = 0.1f) }
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(colors = gradient.map { it.copy(alpha = 0.3f) }),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = gradient.first(),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun RecentMealsSection(meals: List<com.kreggscode.waisttohip.data.local.MealEntity> = emptyList()) {
    val hasMeals = meals.isNotEmpty()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Meals",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (hasMeals) {
                TextButton(onClick = { }) {
                    Text("See All", color = Coral)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (hasMeals) {
            // Show actual meal items from database
            meals.take(3).forEach { meal ->
                val timeFormatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                val time = timeFormatter.format(java.util.Date(meal.timestamp))
                
                MealItem(
                    name = "Meal",
                    calories = meal.totalCalories,
                    time = time,
                    emoji = "🍽️"
                )
                
                if (meal != meals.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            // Empty state
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                glassColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                borderGradient = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                )
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
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No meals tracked yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Start scanning or adding meals to track your nutrition",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MealItem(
    name: String,
    calories: Int,
    time: String,
    emoji: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = emoji,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Vanilla
            ) {
                Text(
                    text = "$calories kcal",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Coral
                )
            }
        }
    }
}

@Composable
fun AIInsightsCard(onChatClick: () -> Unit = {}) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        cornerRadius = 24.dp,
        borderGradient = GradientNeon.map { it.copy(alpha = pulseAlpha) },
        glassColor = NeonPurple.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(colors = GradientNeon)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🤖",
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AI Expert",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Your personalized insights",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Great progress! Your WHR is in the healthy range. Consider adding 20g more protein to support your fitness goals. Tap to chat with me for a personalized meal plan.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onChatClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = NeonPurple
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Chat,
                        contentDescription = "Chat",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ask Follow-up")
                }
            }
        }
    }
}

@Composable
fun FloatingMeasureFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fab_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fab_scale"
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
                    brush = Brush.linearGradient(
                        colors = listOf(Coral, Lavender, Mint)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Straighten,
                contentDescription = "Measure",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private fun getGreeting(): String {
    val hour = LocalDateTime.now().hour
    return when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else -> "Good Night"
    }
}

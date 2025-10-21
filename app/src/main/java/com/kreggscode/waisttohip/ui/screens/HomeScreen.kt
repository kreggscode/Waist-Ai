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
    onSeeAllMealsClick: () -> Unit = {},
    viewModel: com.kreggscode.waisttohip.ui.viewmodels.HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val recentMeals by viewModel.recentMeals.collectAsState()
    val recentMeasurements by viewModel.recentMeasurements.collectAsState()
    val calorieGoal by viewModel.calorieGoal.collectAsState()
    val caloriePeriod by viewModel.caloriePeriod.collectAsState()
    var greeting by remember { mutableStateOf(getGreeting()) }
    val scope = rememberCoroutineScope()
    var showManualMealDialog by remember { mutableStateOf(false) }
    var showCalorieGoalDialog by remember { mutableStateOf(false) }
    
    // Calculate today's total calories from meals
    val todayCalories = remember(recentMeals) {
        val todayStart = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        recentMeals.filter { it.timestamp >= todayStart }
            .sumOf { it.totalCalories }
    }
    
    val todayProtein = remember(recentMeals) {
        val todayStart = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        recentMeals.filter { it.timestamp >= todayStart }
            .sumOf { it.totalProtein }
    }
    
    val todayCarbs = remember(recentMeals) {
        val todayStart = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        recentMeals.filter { it.timestamp >= todayStart }
            .sumOf { it.totalCarbs }
    }
    
    val todayFat = remember(recentMeals) {
        val todayStart = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        recentMeals.filter { it.timestamp >= todayStart }
            .sumOf { it.totalFat }
    }
    
    // Get latest measurement data
    val latestMeasurement = recentMeasurements.firstOrNull()
    val waistValue = latestMeasurement?.waistInches ?: 32f
    val hipValue = latestMeasurement?.hipInches ?: 38f
    val whrValue = latestMeasurement?.whrValue ?: 0.84f
    
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
                            value = String.format("%.1f", waistValue),
                            unit = "inches",
                            icon = "📏",
                            gradient = listOf(Coral, CoralLight),
                            trend = if (recentMeasurements.size > 1) {
                                val change = waistValue - recentMeasurements[1].waistInches
                                String.format("%+.1f", change)
                            } else "+0.0",
                            isPositive = if (recentMeasurements.size > 1) {
                                waistValue < recentMeasurements[1].waistInches
                            } else true
                        )
                    }
                    item {
                        AnimatedStatCard(
                            title = "Hip",
                            value = String.format("%.1f", hipValue),
                            unit = "inches",
                            icon = "📐",
                            gradient = listOf(Mint, MintLight),
                            trend = if (recentMeasurements.size > 1) {
                                val change = hipValue - recentMeasurements[1].hipInches
                                String.format("%+.1f", change)
                            } else "+0.0",
                            isPositive = if (recentMeasurements.size > 1) {
                                hipValue < recentMeasurements[1].hipInches
                            } else true
                        )
                    }
                    item {
                        AnimatedStatCard(
                            title = "WHR",
                            value = String.format("%.2f", whrValue),
                            unit = "ratio",
                            icon = "🎯",
                            gradient = listOf(Lavender, LavenderLight),
                            trend = when {
                                whrValue < 0.85f -> "Healthy"
                                whrValue < 0.95f -> "Moderate"
                                else -> "High Risk"
                            },
                            isPositive = whrValue < 0.85f
                        )
                    }
                }
            }
            
            // Calorie Summary Strip
            item {
                CalorieSummaryStrip(
                    consumed = todayCalories,
                    target = calorieGoal,
                    protein = todayProtein,
                    carbs = todayCarbs,
                    fat = todayFat,
                    period = caloriePeriod,
                    onEditClick = { showCalorieGoalDialog = true }
                )
            }
            
            // Quick Actions
            item {
                QuickActionsSection(
                    onScanClick = onScanClick,
                    onQuickAddClick = onQuickAddClick,
                    onManualAddClick = { showManualMealDialog = true }
                )
            }
            
            // Recent Meals
            item {
                RecentMealsSection(
                    meals = recentMeals,
                    onSeeAllClick = onSeeAllMealsClick
                )
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
    
    // Manual Meal Entry Dialog
    if (showManualMealDialog) {
        ManualMealDialog(
            onDismiss = { showManualMealDialog = false },
            onSave = { mealName, calories, protein, carbs, fat, mealType ->
                scope.launch {
                    viewModel.addManualMeal(mealName, calories, protein, carbs, fat, mealType)
                    showManualMealDialog = false
                }
            }
        )
    }
    
    // Calorie Goal Edit Dialog
    if (showCalorieGoalDialog) {
        CalorieGoalDialog(
            currentGoal = calorieGoal,
            currentPeriod = caloriePeriod,
            onDismiss = { showCalorieGoalDialog = false },
            onSave = { goal, period ->
                viewModel.updateCalorieGoal(goal)
                viewModel.updateCaloriePeriod(period)
                showCalorieGoalDialog = false
            }
        )
    }
}

@Composable
fun PremiumHeader(
    greeting: String,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    
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
            
            // Menu Button
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("⭐ Rate App") },
                        onClick = {
                            showMenu = false
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.kreggscode.waisttohip")
                            )
                            context.startActivity(intent)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("📱 More Apps") },
                        onClick = {
                            showMenu = false
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse("https://play.google.com/store/apps/dev?id=4822923174061161987")
                            )
                            context.startActivity(intent)
                        }
                    )
                }
            }
            
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
    icon: String,
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
        glassColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f)
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
                    Text(
                        text = icon,
                        fontSize = 24.sp
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
    fat: Int,
    period: String = "daily",
    onEditClick: () -> Unit = {}
) {
    val progress = (consumed.toFloat() / target).coerceIn(0f, 1f)
    val periodLabel = when(period) {
        "daily" -> "Today's"
        "monthly" -> "This Month's"
        "yearly" -> "This Year's"
        else -> "Today's"
    }
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clickable(onClick = onEditClick),
        cornerRadius = 20.dp,
        glassColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$periodLabel Calories",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit goal",
                            tint = Mint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
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
                        colors = gradient.map { it.copy(alpha = 0.15f) }
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(colors = gradient.map { it.copy(alpha = 0.5f) }),
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
fun RecentMealsSection(
    meals: List<com.kreggscode.waisttohip.data.local.MealEntity> = emptyList(),
    onSeeAllClick: () -> Unit = {}
) {
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
                TextButton(onClick = onSeeAllClick) {
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
                    name = meal.mealType,
                    calories = meal.totalCalories,
                    time = time,
                    emoji = when(meal.mealType) {
                        "Breakfast" -> "🍳"
                        "Lunch" -> "🍱"
                        "Dinner" -> "🍽️"
                        "Snack" -> "🍿"
                        else -> "🍽️"
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualMealDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int, Int, String) -> Unit
) {
    var mealName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack", "Other")
    val defaultMealType = remember {
        val hourOfDay = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hourOfDay) {
            in 5..10 -> "Breakfast"
            in 11..14 -> "Lunch"
            in 15..17 -> "Snack"
            in 18..22 -> "Dinner"
            else -> "Snack"
        }
    }
    var selectedMealType by remember { mutableStateOf(defaultMealType) }
    var expandedMealType by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Meal Manually",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = mealName,
                        onValueChange = { mealName = it },
                        label = { Text("Meal Name") },
                        placeholder = { Text("e.g., Chicken Salad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Rounded.Restaurant, contentDescription = null)
                        }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { if (it.all { char -> char.isDigit() }) calories = it },
                        label = { Text("Calories (kcal)") },
                        placeholder = { Text("e.g., 350") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Text("🔥", fontSize = 20.sp)
                        }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { if (it.all { char -> char.isDigit() }) protein = it },
                        label = { Text("Protein (g)") },
                        placeholder = { Text("e.g., 25") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Text("💪", fontSize = 20.sp)
                        }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { if (it.all { char -> char.isDigit() }) carbs = it },
                        label = { Text("Carbs (g)") },
                        placeholder = { Text("e.g., 30") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Text("🌾", fontSize = 20.sp)
                        }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { if (it.all { char -> char.isDigit() }) fat = it },
                        label = { Text("Fat (g)") },
                        placeholder = { Text("e.g., 15") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Text("🥑", fontSize = 20.sp)
                        }
                    )
                }
                
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedMealType,
                        onExpandedChange = { expandedMealType = it }
                    ) {
                        OutlinedTextField(
                            value = selectedMealType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Meal Type") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMealType)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            leadingIcon = {
                                Text(
                                    when (selectedMealType) {
                                        "Breakfast" -> "🍳"
                                        "Lunch" -> "🍱"
                                        "Dinner" -> "🍽️"
                                        "Snack" -> "🍿"
                                        else -> "🍴"
                                    },
                                    fontSize = 20.sp
                                )
                            }
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expandedMealType,
                            onDismissRequest = { expandedMealType = false }
                        ) {
                            mealTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                when (type) {
                                                    "Breakfast" -> "🍳"
                                                    "Lunch" -> "🍱"
                                                    "Dinner" -> "🍽️"
                                                    "Snack" -> "🍿"
                                                    else -> "🍴"
                                                },
                                                fontSize = 20.sp
                                            )
                                            Text(type)
                                        }
                                    },
                                    onClick = {
                                        selectedMealType = type
                                        expandedMealType = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (mealName.isNotBlank() && calories.isNotBlank()) {
                        onSave(
                            mealName,
                            calories.toIntOrNull() ?: 0,
                            protein.toIntOrNull() ?: 0,
                            carbs.toIntOrNull() ?: 0,
                            fat.toIntOrNull() ?: 0,
                            selectedMealType
                        )
                    }
                },
                enabled = mealName.isNotBlank() && calories.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save Meal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieGoalDialog(
    currentGoal: Int,
    currentPeriod: String,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit
) {
    var goalText by remember { mutableStateOf(currentGoal.toString()) }
    var selectedPeriod by remember { mutableStateOf(currentPeriod) }
    val periods = listOf("daily", "monthly", "yearly")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Set Calorie Goal",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) goalText = it },
                    label = { Text("Calorie Goal") },
                    placeholder = { Text("e.g., 2000") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Text("🎯", fontSize = 20.sp)
                    },
                    suffix = { Text("kcal") }
                )
                
                Text(
                    text = "Period",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    periods.forEach { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { selectedPeriod = period },
                            label = {
                                Text(
                                    text = period.capitalize(),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Text(
                    text = "Tip: Set realistic goals based on your activity level",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goal = goalText.toIntOrNull() ?: currentGoal
                    onSave(goal, selectedPeriod)
                },
                enabled = goalText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Mint
                )
            ) {
                Text("Save Goal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

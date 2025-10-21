package com.kreggscode.waisttohip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kreggscode.waisttohip.data.local.MealEntity
import com.kreggscode.waisttohip.ui.theme.*
import com.kreggscode.waisttohip.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AllMealsScreen(
    onBackClick: () -> Unit,
    onScanClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val recentMeals by viewModel.recentMeals.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Timeline", "By Date", "Graphs")
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "All Meals",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
            
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onSurface,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Mint,
                            height = 3.dp
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        selectedContentColor = Mint,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Mint else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
            
            // Tab Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 120.dp)
            ) {
            
            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Timeline View - All meals chronologically
                    items(recentMeals) { meal ->
                        AllMealItem(meal = meal)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                1 -> {
                    // Date-wise View - Grouped by date
                    val groupedMeals = recentMeals.groupBy { meal ->
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(meal.timestamp))
                    }
                    groupedMeals.forEach { (date, meals) ->
                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Mint,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(meals) { meal ->
                            AllMealItem(meal = meal)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
                2 -> {
                    // Graphs View - Nutrition trends
                    item {
                        NutritionGraphsView(meals = recentMeals)
                    }
                }
            }
            
            // Empty state
            if (recentMeals.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🍽️",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No meals tracked yet",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Start scanning or adding meals to track your nutrition",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            }
        }
        
        // Floating Action Button
        FloatingActionButton(
            onClick = onScanClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 80.dp),
            containerColor = Mint,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add Meal",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun AllMealItem(meal: MealEntity) {
    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val time = timeFormatter.format(Date(meal.timestamp))
    val date = dateFormatter.format(Date(meal.timestamp))
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = when(meal.mealType) {
                            "Breakfast" -> "🍳"
                            "Lunch" -> "🍱"
                            "Dinner" -> "🍽️"
                            "Snack" -> "🍿"
                            else -> "🍽️"
                        },
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = meal.mealType,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$date • $time",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Coral.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${meal.totalCalories}\nkcal",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Coral,
                        lineHeight = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Macro Chips Row - Responsive
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroChip("${meal.totalProtein}g", "P", Mint, Modifier.weight(1f))
                MacroChip("${meal.totalCarbs}g", "C", Lavender, Modifier.weight(1f))
                MacroChip("${meal.totalFat}g", "F", Coral, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MacroChip(
    value: String, 
    label: String, 
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun NutritionGraphsView(meals: List<MealEntity>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        // Daily Calories Chart
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "📊 Daily Calories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Group by date and calculate totals
                val dailyCalories = meals.groupBy { meal ->
                    SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(meal.timestamp))
                }.mapValues { entry ->
                    entry.value.sumOf { it.totalCalories }
                }.toList().takeLast(7)
                
                if (dailyCalories.isNotEmpty()) {
                    val maxCalories = dailyCalories.maxOf { it.second }
                    dailyCalories.forEach { (date, calories) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.width(60.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Coral.copy(alpha = 0.1f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(calories.toFloat() / maxCalories)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Coral, Coral.copy(alpha = 0.7f))
                                            )
                                        )
                                )
                            }
                            Text(
                                text = "$calories",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(50.dp),
                                color = Coral
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Macro Distribution
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "🥗 Macro Distribution",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                val totalProtein = meals.sumOf { it.totalProtein }
                val totalCarbs = meals.sumOf { it.totalCarbs }
                val totalFat = meals.sumOf { it.totalFat }
                val total = totalProtein + totalCarbs + totalFat
                
                if (total > 0) {
                    MacroBar("Protein", totalProtein, total, Mint)
                    Spacer(modifier = Modifier.height(12.dp))
                    MacroBar("Carbs", totalCarbs, total, Lavender)
                    Spacer(modifier = Modifier.height(12.dp))
                    MacroBar("Fat", totalFat, total, Coral)
                } else {
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MacroBar(label: String, value: Int, total: Int, color: androidx.compose.ui.graphics.Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${value}g (${(value * 100 / total)}%)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value.toFloat() / total)
                    .background(
                        Brush.horizontalGradient(
                            listOf(color, color.copy(alpha = 0.7f))
                        )
                    )
            )
        }
    }
}

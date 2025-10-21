package com.kreggscode.waisttohip.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.waisttohip.ui.components.*
import com.kreggscode.waisttohip.ui.theme.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.kreggscode.waisttohip.ui.viewmodels.ScannerViewModel
import androidx.compose.ui.platform.LocalContext

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScanResultsScreen(
    items: List<FoodItem>,
    onBackClick: () -> Unit,
    onConfirm: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var editedItems by remember { mutableStateOf(items) }
    val totalCalories = editedItems.sumOf { it.calories * it.quantity.toInt() }
    val totalProtein = editedItems.sumOf { (it.protein * it.quantity).toInt() }
    val totalCarbs = editedItems.sumOf { (it.carbs * it.quantity).toInt() }
    val totalFat = editedItems.sumOf { (it.fat * it.quantity).toInt() }
    
    // Meal type selection
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
    var showMealTypeDropdown by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    
    var showSuccessAnimation by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300)
        showSuccessAnimation = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        // Single scrollable column containing everything
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()),
            contentPadding = PaddingValues(bottom = 100.dp), // Space for bottom nav bar
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Premium Header
            item {
                ScanResultsHeader(
                    onBackClick = onBackClick,
                    itemCount = editedItems.size,
                    onShare = {
                        val shareText = buildString {
                            appendLine("🍽️ My Meal Summary")
                            appendLine("━━━━━━━━━━━━━━━━")
                            appendLine("📊 Total Nutrition:")
                            appendLine("🔥 Calories: $totalCalories kcal")
                            appendLine("💪 Protein: ${totalProtein}g")
                            appendLine("🌾 Carbs: ${totalCarbs}g")
                            appendLine("🥑 Fat: ${totalFat}g")
                            appendLine()
                            appendLine("📝 Items:")
                            editedItems.forEach { item ->
                                appendLine("• ${item.name} (${item.quantity}x) - ${item.calories * item.quantity.toInt()} kcal")
                            }
                            appendLine()
                            appendLine("Tracked with WaistHip 📱")
                        }
                        
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, "Share meal via")
                        context.startActivity(shareIntent)
                    }
                )
            }
            
            // Animated Success Indicator
            item {
                AnimatedVisibility(
                    visible = showSuccessAnimation,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn()
                ) {
                    SuccessBanner()
                }
            }
            
            // Meal Type Selector
            item {
                MealTypeSelector(
                    selectedType = selectedMealType,
                    mealTypes = mealTypes,
                    onTypeSelected = { selectedMealType = it }
                )
            }
            
            // Premium Summary Card with Macros Visualization
            item {
                NutritionSummaryCard(
                    totalCalories = totalCalories,
                    totalProtein = totalProtein,
                    totalCarbs = totalCarbs,
                    totalFat = totalFat,
                    itemCount = editedItems.size
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            // Section Header
            item {
                Text(
                    text = "Scanned Items",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Food Items List with Premium Cards
            items(editedItems) { item ->
                PremiumFoodItemCard(
                    item = item,
                    onQuantityChange = { newQuantity ->
                        editedItems = editedItems.map {
                            if (it.id == item.id) it.copy(quantity = newQuantity) else it
                        }
                    },
                    onDelete = {
                        editedItems = editedItems.filter { it.id != item.id }
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
            
            // Add More Item Card
            item {
                AddMoreItemCard(
                    onClick = { showAddItemDialog = true },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
            
            // Bottom Action Bar - now scrollable
            item {
                Spacer(modifier = Modifier.height(20.dp))
                BottomActionBar(
                    onAddMore = { showAddItemDialog = true },
                    onAddMeal = {
                        scope.launch {
                            try {
                                viewModel.saveMealToDatabase(editedItems, selectedMealType)
                                android.widget.Toast.makeText(
                                    context,
                                    "$selectedMealType added successfully!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                onConfirm()
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(
                                    context,
                                    "Failed to add meal: ${e.message}",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    totalCalories = totalCalories
                )
            }
        }
    }
    
    // Add Item Dialog
    if (showAddItemDialog) {
        AddFoodItemDialog(
            onDismiss = { showAddItemDialog = false },
            onAdd = { name, calories, protein, carbs, fat ->
                val newItem = FoodItem(
                    id = java.util.UUID.randomUUID().toString(),
                    name = name,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    quantity = 1f
                )
                editedItems = editedItems + newItem
                showAddItemDialog = false
            }
        )
    }
}

@Composable
fun ScanResultsHeader(
    onBackClick: () -> Unit,
    itemCount: Int,
    onShare: () -> Unit = {}
) {
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
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Scan Results",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$itemCount items found",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(onClick = onShare) {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SuccessBanner() {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        cornerRadius = 16.dp,
        borderGradient = listOf(HealthyGreen.copy(alpha = 0.4f), HealthyGreen.copy(alpha = 0.1f)),
        glassColor = HealthyGreen.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(HealthyGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Success",
                    tint = HealthyGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Scan Successful!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = HealthyGreen
                )
                Text(
                    text = "Nutritional data extracted and ready",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NutritionSummaryCard(
    totalCalories: Int,
    totalProtein: Int,
    totalCarbs: Int,
    totalFat: Int,
    itemCount: Int
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        cornerRadius = 24.dp,
        borderGradient = GradientNeon.map { it.copy(alpha = 0.3f) },
        glassColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Total Calories Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Nutrition",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = totalCalories.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = Coral
                        )
                        Text(
                            text = " kcal",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                
                // Animated Calorie Icon
                val infiniteTransition = rememberInfiniteTransition(label = "calorie_pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )
                
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Coral.copy(alpha = 0.2f), Coral.copy(alpha = 0.05f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 28.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Macros Breakdown with Progress Bars
            MacroProgressBar(
                label = "Protein",
                value = totalProtein,
                unit = "g",
                color = Mint,
                icon = "💪",
                progress = (totalProtein / 150f).coerceIn(0f, 1f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            MacroProgressBar(
                label = "Carbs",
                value = totalCarbs,
                unit = "g",
                color = Lavender,
                icon = "🌾",
                progress = (totalCarbs / 250f).coerceIn(0f, 1f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            MacroProgressBar(
                label = "Fat",
                value = totalFat,
                unit = "g",
                color = Coral,
                icon = "🥑",
                progress = (totalFat / 70f).coerceIn(0f, 1f)
            )
        }
    }
}

@Composable
fun MacroProgressBar(
    label: String,
    value: Int,
    unit: String,
    color: Color,
    icon: String,
    progress: Float
) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(progress) {
        animate(
            initialValue = 0f,
            targetValue = progress,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) { value, _ ->
            animatedProgress = value
        }
    }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Text(
                text = "$value$unit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(color, color.copy(alpha = 0.7f))
                        )
                    )
            )
        }
    }
}

@Composable
fun PremiumFoodItemCard(
    item: FoodItem,
    onQuantityChange: (Float) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    GlassCard(
        modifier = modifier,
        cornerRadius = 20.dp,
        glassColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f),
        borderGradient = listOf(
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        ),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Coral.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "${(item.calories * item.quantity).toInt()} kcal",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Coral
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${(item.protein * item.quantity).toInt()}g protein",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Row {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown,
                            contentDescription = "Expand",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Delete",
                            tint = HighRisk,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Detailed Macros
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MacroDetail(
                            label = "Carbs",
                            value = (item.carbs * item.quantity).toInt(),
                            unit = "g",
                            color = Lavender
                        )
                        MacroDetail(
                            label = "Fat",
                            value = (item.fat * item.quantity).toInt(),
                            unit = "g",
                            color = Coral
                        )
                        MacroDetail(
                            label = "Fiber",
                            value = 3,
                            unit = "g",
                            color = Mint
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Quantity Selector with Premium Design
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Serving Size",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { if (item.quantity > 0.5f) onQuantityChange(item.quantity - 0.5f) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Coral.copy(alpha = 0.2f), CoralLight.copy(alpha = 0.1f))
                                )
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Remove,
                            contentDescription = "Decrease",
                            tint = Coral,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.width(60.dp)
                    ) {
                        Text(
                            text = String.format("%.1f", item.quantity),
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    
                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 0.5f) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Coral, CoralLight)
                                )
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Increase",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MacroDetail(
    label: String,
    value: Int,
    unit: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value$unit",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun AddMoreItemCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(Mint.copy(alpha = 0.3f), MintLight.copy(alpha = 0.1f))
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add",
                tint = Mint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add Another Item",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Mint
            )
        }
    }
}

@Composable
fun BottomActionBar(
    onAddMore: () -> Unit,
    onAddMeal: () -> Unit,
    totalCalories: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        shadowElevation = 16.dp,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Quick Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickStat(icon = "🔥", value = "$totalCalories", label = "kcal")
                QuickStat(icon = "⏱️", value = "2", label = "mins")
                QuickStat(icon = "✓", value = "Ready", label = "to add")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onAddMore,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add More",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add More")
                }
                
                AnimatedGradientButton(
                    text = "Add to Meal Log",
                    onClick = onAddMeal,
                    modifier = Modifier.weight(1.5f),
                    gradientColors = listOf(Coral, Lavender, Mint)
                )
            }
        }
    }
}

@Composable
fun MealTypeSelector(
    selectedType: String,
    mealTypes: List<String>,
    onTypeSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Meal Type",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mealTypes) { type ->
                val isSelected = type == selectedType
                
                Surface(
                    onClick = { onTypeSelected(type) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    border = if (isSelected) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else null
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = when(type) {
                                "Breakfast" -> "🍳"
                                "Lunch" -> "🍱"
                                "Dinner" -> "🍽️"
                                "Snack" -> "🍿"
                                "Other" -> "🍴"
                                else -> "🍽️"
                            },
                            fontSize = 18.sp
                        )
                        Text(
                            text = type,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickStat(
    icon: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun MacroStat(
    icon: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodItemDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Int, Int, Int, Int) -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Food Item",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food Name") },
                        placeholder = { Text("e.g., Grilled Chicken") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Text("🍽️", fontSize = 20.sp)
                        }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { if (it.all { char -> char.isDigit() }) calories = it },
                        label = { Text("Calories (kcal)") },
                        placeholder = { Text("e.g., 250") },
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
                        placeholder = { Text("e.g., 30") },
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
                        placeholder = { Text("e.g., 20") },
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
                        placeholder = { Text("e.g., 10") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Text("🥑", fontSize = 20.sp)
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (foodName.isNotBlank() && calories.isNotBlank()) {
                        onAdd(
                            foodName,
                            calories.toIntOrNull() ?: 0,
                            protein.toIntOrNull() ?: 0,
                            carbs.toIntOrNull() ?: 0,
                            fat.toIntOrNull() ?: 0
                        )
                    }
                },
                enabled = foodName.isNotBlank() && calories.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Mint
                )
            ) {
                Text("Add Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

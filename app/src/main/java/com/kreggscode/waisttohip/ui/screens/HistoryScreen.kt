package com.kreggscode.waisttohip.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreggscode.waisttohip.ui.components.*
import com.kreggscode.waisttohip.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    onBackClick: () -> Unit,
    viewModel: com.kreggscode.waisttohip.ui.viewmodels.HistoryViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val measurements by viewModel.measurements.collectAsState()
    val hasHistory = measurements.isNotEmpty()
    var selectedFilter by remember { mutableStateOf("All") }
    var showFilterDialog by remember { mutableStateOf(false) }
    val filters = listOf("All", "Week", "Month", "Year")
    
    // Convert measurements to history items
    val historyItems = measurements.map { measurement ->
        val whrStatus = when {
            measurement.whrValue < 0.85f -> "Healthy"
            measurement.whrValue < 0.95f -> "Moderate"
            else -> "High Risk"
        }
        
        HistoryItem(
            date = java.time.Instant.ofEpochMilli(measurement.timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime(),
            waist = measurement.waistInches,
            hip = measurement.hipInches,
            whr = measurement.whrValue,
            status = whrStatus
        )
    }
    
    // Filter items based on selected filter
    val itemsToShow = remember(historyItems, selectedFilter) {
        val now = java.time.LocalDateTime.now()
        when (selectedFilter) {
            "Week" -> historyItems.filter { it.date.isAfter(now.minusWeeks(1)) }
            "Month" -> historyItems.filter { it.date.isAfter(now.minusMonths(1)) }
            "Year" -> historyItems.filter { it.date.isAfter(now.minusYears(1)) }
            else -> historyItems
        }
    }
    
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
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Measurement History",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = "Filter",
                            tint = if (selectedFilter != "All") Mint else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            // Show content or empty state
            if (itemsToShow.isEmpty()) {
                item {
                    EmptyHistoryState()
                }
            } else {
                // Stats Summary
                item {
                    StatsSummaryCard(historyItems)
                }
                
                // Progress Chart
                item {
                    ProgressChartCard(historyItems)
                }
                
                // History List Header
                item {
                    Text(
                        text = "Recent Measurements",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                }
                
                // History Items
                items(itemsToShow) { item ->
                    HistoryItemCard(
                        item = item,
                        onDelete = { /* TODO: Implement delete via ViewModel */ }
                    )
                }
            }
        }
    }
    
    // Filter Dialog
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = {
                Text(
                    text = "Filter Measurements",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = {
                                selectedFilter = filter
                                showFilterDialog = false
                            },
                            label = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when(filter) {
                                            "All" -> "📅 All Time"
                                            "Week" -> "📆 Last Week"
                                            "Month" -> "🗓️ Last Month"
                                            "Year" -> "📊 Last Year"
                                            else -> filter
                                        },
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    if (selectedFilter == filter) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = Mint,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun StatsSummaryCard(items: List<HistoryItem>) {
    if (items.isEmpty()) return
    
    val latest = items.first()
    val previous = items.getOrNull(1)
    
    val waistChange = previous?.let { latest.waist - it.waist } ?: 0f
    val hipChange = previous?.let { latest.hip - it.hip } ?: 0f
    val whrChange = previous?.let { latest.whr - it.whr } ?: 0f
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        cornerRadius = 24.dp,
        glassColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Current Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatChangeItem(
                    label = "Waist",
                    value = String.format("%.1f", latest.waist),
                    change = waistChange,
                    unit = "in",
                    color = Coral
                )
                StatChangeItem(
                    label = "Hip",
                    value = String.format("%.1f", latest.hip),
                    change = hipChange,
                    unit = "in",
                    color = Mint
                )
                StatChangeItem(
                    label = "WHR",
                    value = String.format("%.2f", latest.whr),
                    change = whrChange,
                    unit = "",
                    color = Lavender
                )
            }
        }
    }
}

@Composable
fun StatChangeItem(
    label: String,
    value: String,
    change: Float,
    unit: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
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
                color = color
            )
            if (unit.isNotEmpty()) {
                Text(
                    text = " $unit",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
        
        if (change != 0f) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (change < 0) Icons.Rounded.TrendingDown else Icons.Rounded.TrendingUp,
                    contentDescription = null,
                    tint = if (change < 0) HealthyGreen else HighRisk,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = String.format("%.1f", kotlin.math.abs(change)),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (change < 0) HealthyGreen else HighRisk
                )
            }
        }
    }
}

@Composable
fun ProgressChartCard(items: List<HistoryItem>) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        cornerRadius = 24.dp,
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
                Text(
                    text = "Progress Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (items.firstOrNull()?.status) {
                        "Healthy" -> HealthyGreen.copy(alpha = 0.1f)
                        "Moderate" -> ModerateYellow.copy(alpha = 0.1f)
                        else -> HighRisk.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        text = items.firstOrNull()?.status ?: "Unknown",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = when (items.firstOrNull()?.status) {
                            "Healthy" -> HealthyGreen
                            "Moderate" -> ModerateYellow
                            else -> HighRisk
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Animated line chart
            if (items.isNotEmpty()) {
                WHRLineChart(
                    items = items.take(10).reversed(), // Show last 10 measurements, oldest to newest
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📊",
                        fontSize = 48.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Track your progress over time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun HistoryItemCard(
    item: HistoryItem,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        cornerRadius = 20.dp,
        glassColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = when (item.status) {
                                    "Healthy" -> listOf(Mint, MintLight)
                                    "Moderate" -> listOf(Coral, CoralLight)
                                    else -> listOf(Lavender, LavenderLight)
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📏",
                        fontSize = 24.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = item.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "WHR: ${String.format("%.2f", item.whr)} • ${item.status}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = HighRisk
                )
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Measurement?") },
            text = { Text("Are you sure you want to delete this measurement? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = HighRisk)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmptyHistoryState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📝",
            fontSize = 80.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No Measurements Yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Start tracking your measurements to see your progress over time",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

data class HistoryItem(
    val date: LocalDateTime,
    val waist: Float,
    val hip: Float,
    val whr: Float,
    val status: String
)

@Composable
fun WHRLineChart(
    items: List<HistoryItem>,
    modifier: Modifier = Modifier
) {
    var animationProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(items) {
        animationProgress = 0f
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        ) { value, _ ->
            animationProgress = value
        }
    }
    
    Column(modifier = modifier) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(vertical = 8.dp)) {
        if (items.isEmpty()) return@Canvas
        
        val whrValues = items.map { it.whr }
        val minWhr = (whrValues.minOrNull() ?: 0.7f) - 0.05f
        val maxWhr = (whrValues.maxOrNull() ?: 1.0f) + 0.05f
        val whrRange = maxWhr - minWhr
        
        val chartWidth = size.width - 40.dp.toPx()
        val chartHeight = size.height - 40.dp.toPx()
        val startX = 20.dp.toPx()
        val startY = 20.dp.toPx()
        
        // Calculate points
        val points = items.mapIndexed { index, item ->
            val x = startX + (index.toFloat() / (items.size - 1).coerceAtLeast(1)) * chartWidth
            val normalizedWhr = (item.whr - minWhr) / whrRange
            val y = startY + chartHeight - (normalizedWhr * chartHeight)
            androidx.compose.ui.geometry.Offset(x, y)
        }
        
        // Draw gradient fill under the line
        if (points.size > 1) {
            val animatedPoints = points.take((points.size * animationProgress).toInt().coerceAtLeast(2))
            
            val fillPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(animatedPoints.first().x, startY + chartHeight)
                animatedPoints.forEach { point ->
                    lineTo(point.x, point.y)
                }
                lineTo(animatedPoints.last().x, startY + chartHeight)
                close()
            }
            
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF14B8A6).copy(alpha = 0.3f),
                        Color(0xFF14B8A6).copy(alpha = 0.05f)
                    ),
                    startY = startY,
                    endY = startY + chartHeight
                )
            )
        }
        
        // Draw the line
        if (points.size > 1) {
            val animatedPoints = points.take((points.size * animationProgress).toInt().coerceAtLeast(2))
            
            val linePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(animatedPoints.first().x, animatedPoints.first().y)
                for (i in 1 until animatedPoints.size) {
                    val prevPoint = animatedPoints[i - 1]
                    val currentPoint = animatedPoints[i]
                    
                    // Smooth curve using quadratic bezier
                    val controlX = (prevPoint.x + currentPoint.x) / 2
                    quadraticBezierTo(
                        controlX, prevPoint.y,
                        controlX, (prevPoint.y + currentPoint.y) / 2
                    )
                    quadraticBezierTo(
                        controlX, currentPoint.y,
                        currentPoint.x, currentPoint.y
                    )
                }
            }
            
            drawPath(
                path = linePath,
                color = Color(0xFF14B8A6),
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
        
        // Draw points
        val animatedPointCount = (points.size * animationProgress).toInt()
        points.take(animatedPointCount).forEachIndexed { index, point ->
            // Outer circle (glow effect)
            drawCircle(
                color = Color(0xFF14B8A6).copy(alpha = 0.2f),
                radius = 8.dp.toPx(),
                center = point
            )
            // Inner circle
            drawCircle(
                color = Color(0xFF14B8A6),
                radius = 4.dp.toPx(),
                center = point
            )
            // Center dot
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = point
            )
        }
        
        // Draw reference lines for healthy zones
        val healthyThreshold = 0.85f
        if (healthyThreshold in minWhr..maxWhr) {
            val normalizedHealthy = (healthyThreshold - minWhr) / whrRange
            val healthyY = startY + chartHeight - (normalizedHealthy * chartHeight)
            
            drawLine(
                color = Color(0xFF10B981).copy(alpha = 0.3f),
                start = androidx.compose.ui.geometry.Offset(startX, healthyY),
                end = androidx.compose.ui.geometry.Offset(startX + chartWidth, healthyY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(10f, 10f)
                )
            )
        }
    }
        
        // Date labels
        if (items.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Show first, middle, and last dates
                val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")
                
                Text(
                    text = items.first().date.format(dateFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (items.size > 2) {
                    Text(
                        text = items[items.size / 2].date.format(dateFormatter),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (items.size > 1) {
                    Text(
                        text = items.last().date.format(dateFormatter),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

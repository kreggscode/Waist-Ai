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
import androidx.compose.ui.graphics.*
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
    
    var itemsToShow by remember { mutableStateOf(historyItems) }
    
    // Update items when measurements change
    LaunchedEffect(measurements) {
        itemsToShow = historyItems
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
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Rounded.FilterList,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.onSurface
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
                
                // History Items with delete functionality
                items(itemsToShow) { item ->
                    HistoryItemCard(
                        item = item,
                        onDelete = {
                            itemsToShow = itemsToShow.filter { it != item }
                        }
                    )
                }
            }
        }
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
        glassColor = GlassWhite.copy(alpha = 0.95f)
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
            
            // Simple progress visualization
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

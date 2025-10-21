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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.kreggscode.waisttohip.ui.components.*
import com.kreggscode.waisttohip.ui.theme.*
import com.kreggscode.waisttohip.ui.viewmodels.ScannerViewModel
import com.kreggscode.waisttohip.data.repository.AIRepository
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.util.UUID

@Composable
fun ScannerScreen(onBackClick: () -> Unit) {
    var currentMode by remember { mutableStateOf(ScanMode.CAMERA) }
    var scannedItems by remember { mutableStateOf(listOf<FoodItem>()) }
    var isScanning by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        selectedImageUri = uri
        if (uri != null) {
            isScanning = true
            // Will be processed in the main screen
        }
    }
    
    AnimatedContent(
        targetState = showResults,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { if (targetState) it else -it },
                animationSpec = tween(400)
            ) + fadeIn() togetherWith
            slideOutHorizontally(
                targetOffsetX = { if (targetState) -it else it },
                animationSpec = tween(400)
            ) + fadeOut()
        },
        label = "scanner_transition"
    ) { showingResults ->
        if (showingResults) {
            ScanResultsScreen(
                items = scannedItems,
                onBackClick = { showResults = false },
                onConfirm = onBackClick
            )
        } else {
            ScannerMainScreen(
                currentMode = currentMode,
                onModeChange = { currentMode = it },
                isScanning = isScanning,
                onScan = {
                    isScanning = true
                    scannedItems = generateMockFoodItems()
                    showResults = true
                    isScanning = false
                },
                onGalleryClick = {
                    galleryLauncher.launch("image/*")
                },
                selectedImageUri = selectedImageUri,
                onImageProcessed = { items ->
                    scannedItems = items
                    showResults = true
                    isScanning = false
                    selectedImageUri = null
                },
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
fun ScannerMainScreen(
    currentMode: ScanMode,
    onModeChange: (ScanMode) -> Unit,
    isScanning: Boolean,
    onScan: () -> Unit,
    onGalleryClick: () -> Unit,
    selectedImageUri: android.net.Uri?,
    onImageProcessed: (List<FoodItem>) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val aiRepository: AIRepository = hiltViewModel<ScannerViewModel>().aiRepository
    val scope = rememberCoroutineScope()
    
    // Process selected image
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    
                    // Convert to base64
                    val base64Image = bitmapToBase64(bitmap)
                    
                    // Analyze with AI
                    android.util.Log.d("ScannerScreen", "Starting AI food analysis...")
                    val result = aiRepository.analyzeFoodImage(base64Image)
                    result.onSuccess { jsonResponse ->
                        android.util.Log.d("ScannerScreen", "AI Response: $jsonResponse")
                        val items = parseFoodItemsFromJson(jsonResponse)
                        if (items.isNotEmpty()) {
                            android.util.Log.d("ScannerScreen", "Successfully parsed ${items.size} food items")
                            onImageProcessed(items)
                        } else {
                            android.util.Log.e("ScannerScreen", "No items parsed from AI response")
                            android.widget.Toast.makeText(
                                context,
                                "Could not identify food items. Please try a clearer image.",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }.onFailure { error ->
                        android.util.Log.e("ScannerScreen", "AI analysis failed: ${error.message}", error)
                        android.widget.Toast.makeText(
                            context,
                            "AI analysis failed: ${error.message}. Please try again.",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ScannerScreen", "Image processing failed: ${e.message}", e)
                    android.widget.Toast.makeText(
                        context,
                        "Image processing failed: ${e.message}",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (currentMode) {
            ScanMode.CAMERA -> CameraScannerView(isScanning = isScanning, onScan = onScan)
            ScanMode.RECEIPT -> ReceiptScannerView()
        }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.systemBars.asPaddingValues())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(GlassWhite.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                GlassCard(
                    cornerRadius = 20.dp,
                    glassColor = GlassWhite.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = "Food Scanner",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(GlassWhite.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            ScanModeSelector(currentMode = currentMode, onModeChange = onModeChange)
        }
        
        if (currentMode == ScanMode.CAMERA) {
            // Gallery Button - positioned above bottom nav with proper spacing
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 40.dp, bottom = 140.dp) // Increased from 100dp to 140dp
            ) {
                IconButton(
                    onClick = onGalleryClick,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Lavender, LavenderLight)
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PhotoLibrary,
                        contentDescription = "Gallery",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            // Scan Button - positioned above bottom nav with proper spacing
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 140.dp) // Increased from 100dp to 140dp
            ) {
                ScanButton(isScanning = isScanning, onClick = onScan)
            }
        }
    }
}

@Composable
fun CameraScannerView(isScanning: Boolean, onScan: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        CameraPreview(modifier = Modifier.fillMaxSize())
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val scanAreaSize = size.width * 0.7f
            
            drawRect(color = Color.Black.copy(alpha = 0.5f), size = size)
            
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(centerX - scanAreaSize / 2, centerY - scanAreaSize / 2),
                size = androidx.compose.ui.geometry.Size(scanAreaSize, scanAreaSize),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                blendMode = BlendMode.Clear
            )
            
            val cornerLength = 40.dp.toPx()
            val strokeWidth = 4.dp.toPx()
            val corners = listOf(
                Offset(centerX - scanAreaSize / 2, centerY - scanAreaSize / 2),
                Offset(centerX + scanAreaSize / 2, centerY - scanAreaSize / 2),
                Offset(centerX - scanAreaSize / 2, centerY + scanAreaSize / 2),
                Offset(centerX + scanAreaSize / 2, centerY + scanAreaSize / 2)
            )
            
            corners.forEachIndexed { index, corner ->
                val xMultiplier = if (index % 2 == 0) 1 else -1
                val yMultiplier = if (index < 2) 1 else -1
                
                drawLine(
                    brush = Brush.linearGradient(colors = listOf(Coral, CoralLight)),
                    start = corner,
                    end = corner.copy(x = corner.x + cornerLength * xMultiplier),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                
                drawLine(
                    brush = Brush.linearGradient(colors = listOf(Coral, CoralLight)),
                    start = corner,
                    end = corner.copy(y = corner.y + cornerLength * yMultiplier),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 200.dp)
        ) {
            GlassCard(
                cornerRadius = 20.dp,
                glassColor = GlassBlack.copy(alpha = 0.7f)
            ) {
                Text(
                    text = "Point at food to scan",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun EmailScannerView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.surface
            )))
            .padding(top = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Lavender.copy(alpha = 0.2f), LavenderLight.copy(alpha = 0.1f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "📧", fontSize = 60.sp)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Forward Meal Emails",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Forward your food delivery or restaurant\nemails to scan nutritional information",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            cornerRadius = 20.dp,
            borderGradient = listOf(Lavender.copy(alpha = 0.3f), LavenderLight.copy(alpha = 0.1f)),
            glassColor = Lavender.copy(alpha = 0.05f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Forward emails to:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "scan@curvefuel.ai",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Lavender
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(onClick = { }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Copy",
                            tint = Lavender,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Privacy",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your emails are processed securely and deleted after scanning",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ReceiptScannerView() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Handle selected image URI
        uri?.let {
            // TODO: Process the selected image
        }
    }
    
    Column(
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
            .padding(top = 140.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Mint.copy(alpha = 0.2f), MintLight.copy(alpha = 0.1f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🧾", fontSize = 60.sp)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Scan Receipts",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Upload grocery or restaurant receipts\nto track your meals automatically",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UploadOptionCard(
                icon = Icons.Rounded.PhotoCamera,
                title = "Take Photo",
                description = "Capture receipt with camera",
                gradient = listOf(Coral, CoralLight),
                onClick = { }
            )
            
            UploadOptionCard(
                icon = Icons.Rounded.PhotoLibrary,
                title = "Choose from Gallery",
                description = "Select existing photo",
                gradient = listOf(Mint, MintLight),
                onClick = { galleryLauncher.launch("image/*") }
            )
        }
    }
}

@Composable
fun UploadOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 20.dp,
        borderGradient = gradient.map { it.copy(alpha = 0.3f) },
        glassColor = gradient[0].copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush = Brush.linearGradient(colors = gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null,
                tint = gradient[0],
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ScanModeSelector(currentMode: ScanMode, onModeChange: (ScanMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ScanMode.values().forEach { mode ->
            ModeChip(mode = mode, isSelected = currentMode == mode, onClick = { onModeChange(mode) })
        }
    }
}

@Composable
fun ModeChip(mode: ScanMode, isSelected: Boolean, onClick: () -> Unit) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) Coral else Color.Transparent,
        animationSpec = tween(300),
        label = "mode_color"
    )
    
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = animatedColor.copy(alpha = if (isSelected) 0.2f else 0f),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) Coral else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (mode) {
                    ScanMode.CAMERA -> Icons.Rounded.PhotoCamera
                    ScanMode.RECEIPT -> Icons.Rounded.Add
                },
                contentDescription = mode.name,
                tint = if (isSelected) Coral else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = mode.displayName,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) Coral else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ScanButton(isScanning: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan_button")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isScanning) 1.2f else 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_scale"
    )
    
    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = if (isScanning) {
                        listOf(HighRisk, HighRisk.copy(alpha = 0.7f))
                    } else {
                        listOf(Coral, CoralLight)
                    }
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isScanning) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.PhotoCamera,
                contentDescription = "Scan",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

enum class ScanMode(val displayName: String) {
    CAMERA("Camera"),
    RECEIPT("Receipt")
}

data class FoodItem(
    val id: String,
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val quantity: Float = 1f
)

private fun generateMockFoodItems(): List<FoodItem> {
    return listOf(
        FoodItem("1", "Grilled Chicken Breast", 165, 31, 0, 4),
        FoodItem("2", "Brown Rice", 216, 5, 45, 2),
        FoodItem("3", "Mixed Vegetables", 65, 3, 13, 0),
        FoodItem("4", "Olive Oil", 119, 0, 0, 14)
    )
}

private fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

private fun parseFoodItemsFromJson(jsonResponse: String): List<FoodItem> {
    return try {
        // Clean up the response - remove markdown code blocks, extra text, and whitespace
        var cleanJson = jsonResponse
            .replace("```json", "")
            .replace("```", "")
            .trim()
        
        // Find the JSON array in the response (in case there's extra text)
        val arrayStart = cleanJson.indexOf('[')
        val arrayEnd = cleanJson.lastIndexOf(']')
        
        if (arrayStart != -1 && arrayEnd != -1 && arrayEnd > arrayStart) {
            cleanJson = cleanJson.substring(arrayStart, arrayEnd + 1)
        }
        
        val jsonArray = JSONArray(cleanJson)
        val items = mutableListOf<FoodItem>()
        
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            
            // Validate that we have a proper food name (not placeholder text)
            val name = jsonObject.optString("name", "Unknown Food")
            if (name.isNotEmpty() && !name.contains("placeholder", ignoreCase = true)) {
                items.add(
                    FoodItem(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        calories = jsonObject.optInt("calories", 0),
                        protein = jsonObject.optInt("protein", 0),
                        carbs = jsonObject.optInt("carbs", 0),
                        fat = jsonObject.optInt("fat", 0),
                        quantity = jsonObject.optDouble("quantity", 1.0).toFloat()
                    )
                )
            }
        }
        
        items
    } catch (e: Exception) {
        // Log the error for debugging
        android.util.Log.e("ScannerScreen", "Error parsing food items: ${e.message}", e)
        android.util.Log.e("ScannerScreen", "JSON Response: $jsonResponse")
        emptyList()
    }
}

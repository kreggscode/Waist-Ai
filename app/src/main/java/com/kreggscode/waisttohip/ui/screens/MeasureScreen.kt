package com.kreggscode.waisttohip.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.kreggscode.waisttohip.ui.components.*
import com.kreggscode.waisttohip.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MeasureScreen(
    onBackClick: () -> Unit,
    onResultsClick: () -> Unit,
    viewModel: com.kreggscode.waisttohip.ui.viewmodels.MeasureViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var currentStep by remember { mutableIntStateOf(0) }
    var waistValue by remember { mutableFloatStateOf(32f) }
    var hipValue by remember { mutableFloatStateOf(38f) }
    var useCamera by remember { mutableStateOf(false) }
    
    AnimatedContent(
        targetState = currentStep,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { if (targetState > initialState) it else -it },
                animationSpec = tween(400)
            ) + fadeIn() togetherWith
            slideOutHorizontally(
                targetOffsetX = { if (targetState > initialState) -it else it },
                animationSpec = tween(400)
            ) + fadeOut()
        },
        label = "step_transition"
    ) { step ->
        when (step) {
            0 -> MeasurementMethodScreen(
                onCameraClick = {
                    // Camera feature coming soon
                    useCamera = true
                    currentStep = 3
                },
                onManualClick = {
                    useCamera = false
                    currentStep = 1
                },
                onBackClick = onBackClick
            )
            1 -> if (useCamera) {
                CameraMeasureScreen(
                    onNext = { currentStep = 2 },
                    onBackClick = { currentStep = 0 }
                )
            } else {
                ManualInputScreen(
                    waistValue = waistValue,
                    hipValue = hipValue,
                    onWaistChange = { waistValue = it },
                    onHipChange = { hipValue = it },
                    onNext = { currentStep = 2 },
                    onBackClick = { currentStep = 0 }
                )
            }
            2 -> ResultScreen(
                waistValue = waistValue,
                hipValue = hipValue,
                onAnalysisClick = onResultsClick,
                onBackClick = { currentStep = 1 },
                viewModel = viewModel
            )
            3 -> ComingSoonScreen(
                onBackClick = { currentStep = 0 }
            )
        }
    }
}

@Composable
fun MeasurementMethodScreen(
    onCameraClick: () -> Unit,
    onManualClick: () -> Unit,
    onBackClick: () -> Unit
) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    text = "Measure",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Title
            Text(
                text = "How would you like\nto measure?",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Choose your preferred method",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Camera Option
            MeasureOptionCard(
                icon = "📸",
                title = "Camera Measure",
                description = "Use AR guidance for accurate measurements",
                gradient = listOf(Coral, CoralLight),
                onClick = onCameraClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Manual Option
            MeasureOptionCard(
                icon = "✏️",
                title = "Manual Input",
                description = "Enter your measurements directly",
                gradient = listOf(Mint, MintLight),
                onClick = onManualClick
            )
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun MeasureOptionCard(
    icon: String,
    title: String,
    description: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "option_scale"
    )
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        cornerRadius = 24.dp,
        borderGradient = gradient.map { it.copy(alpha = 0.3f) },
        glassColor = gradient[0].copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(colors = gradient)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = gradient[0],
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CameraMeasureScreen(
    onNext: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Camera Preview
        CameraPreview(
            modifier = Modifier.fillMaxSize()
        )
        
        // AR Overlay
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val guideRadius = size.width * 0.35f
            
            // Draw measurement guide circles
            drawCircle(
                color = Coral.copy(alpha = 0.3f),
                radius = guideRadius,
                center = Offset(centerX, centerY - 100),
                style = Stroke(width = 4.dp.toPx())
            )
            
            drawCircle(
                color = Mint.copy(alpha = 0.3f),
                radius = guideRadius * 1.2f,
                center = Offset(centerX, centerY + 100),
                style = Stroke(width = 4.dp.toPx())
            )
            
            // Draw guide lines
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(0f, centerY - 100),
                end = Offset(size.width, centerY - 100),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
            
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(0f, centerY + 100),
                end = Offset(size.width, centerY + 100),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        }
        
        // Top controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(GlassBlack.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            GlassCard(
                cornerRadius = 20.dp,
                glassColor = GlassBlack.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Align with guides",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        
        // Bottom capture button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
        ) {
            AnimatedGradientButton(
                text = "Capture",
                onClick = onNext,
                modifier = Modifier.width(200.dp),
                gradientColors = listOf(Coral, Lavender, Mint)
            )
        }
    }
}

@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }
    
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(android.Manifest.permission.CAMERA)
        }
    }
    
    if (!hasCameraPermission) {
        Box(
            modifier = modifier.background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "📷",
                    fontSize = 60.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Camera Permission Required",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please grant camera permission to use this feature",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { launcher.launch(android.Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Coral
                    )
                ) {
                    Text("Grant Permission")
                }
            }
        }
    } else {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build()
                            preview.setSurfaceProvider(surfaceProvider)
                            
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                }
            },
            modifier = modifier
        )
    }
}

@Composable
fun ManualInputScreen(
    waistValue: Float,
    hipValue: Float,
    onWaistChange: (Float) -> Unit,
    onHipChange: (Float) -> Unit,
    onNext: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(20.dp)
                .padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    text = "Enter Measurements",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Waist Input
            MeasurementInputCard(
                label = "Waist",
                value = waistValue,
                onChange = onWaistChange,
                gradient = listOf(Coral, CoralLight),
                icon = "📏",
                unit = "inches"
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Hip Input
            MeasurementInputCard(
                label = "Hip",
                value = hipValue,
                onChange = onHipChange,
                gradient = listOf(Mint, MintLight),
                icon = "📐",
                unit = "inches"
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Tips Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                glassColor = Lavender.copy(alpha = 0.05f),
                borderGradient = listOf(Lavender.copy(alpha = 0.3f), LavenderLight.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "💡",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Measurement Tips",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Measure waist at the narrowest point\n• Measure hips at the widest point\n• Keep tape parallel to the floor",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Continue Button
            AnimatedGradientButton(
                text = "Calculate WHR",
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                gradientColors = GradientNeon
            )
        }
    }
}

@Composable
fun MeasurementInputCard(
    label: String,
    value: Float,
    onChange: (Float) -> Unit,
    gradient: List<Color>,
    icon: String,
    unit: String
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        glassColor = gradient[0].copy(alpha = 0.05f),
        borderGradient = gradient.map { it.copy(alpha = 0.2f) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Decrease button
                IconButton(
                    onClick = { if (value > 10) onChange(value - 0.5f) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(gradient[0].copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = "Decrease",
                        tint = gradient[0]
                    )
                }
                
                // Value display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format("%.1f", value),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = gradient[0]
                    )
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Increase button
                IconButton(
                    onClick = { if (value < 100) onChange(value + 0.5f) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(gradient[0].copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Increase",
                        tint = gradient[0]
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Slider
            Slider(
                value = value,
                onValueChange = onChange,
                valueRange = 10f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = gradient[0],
                    activeTrackColor = gradient[0],
                    inactiveTrackColor = gradient[1].copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun ResultScreen(
    waistValue: Float,
    hipValue: Float,
    onAnalysisClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: com.kreggscode.waisttohip.ui.viewmodels.MeasureViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val whrValue = waistValue / hipValue
    val whrStatus = when {
        whrValue < 0.85f -> "Healthy"
        whrValue < 0.95f -> "Moderate"
        else -> "High Risk"
    }
    
    var showResult by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300)
        showResult = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        when (whrStatus) {
                            "Healthy" -> Pistachio.copy(alpha = 0.2f)
                            "Moderate" -> Strawberry.copy(alpha = 0.2f)
                            else -> Blueberry.copy(alpha = 0.2f)
                        },
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    text = "Your Results",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = "Share",
                        tint = TextPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Ice Cream Progress Ring - The star visualization!
            AnimatedVisibility(
                visible = showResult,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                IceCreamProgressRing(
                    waistProgress = (waistValue - 20) / 40, // Normalize to 0-1
                    hipProgress = (hipValue - 25) / 45, // Normalize to 0-1
                    whrValue = whrValue,
                    whrStatus = whrStatus,
                    modifier = Modifier.padding(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Detailed Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Waist",
                    value = String.format("%.1f", waistValue),
                    unit = "in",
                    color = Coral
                )
                StatItem(
                    label = "Hip",
                    value = String.format("%.1f", hipValue),
                    unit = "in",
                    color = Mint
                )
                StatItem(
                    label = "WHR",
                    value = String.format("%.2f", whrValue),
                    unit = "",
                    color = Lavender
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // AI Analysis Preview
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAnalysisClick() },
                cornerRadius = 20.dp,
                borderGradient = GradientNeon.map { it.copy(alpha = 0.3f) },
                glassColor = NeonPurple.copy(alpha = 0.05f)
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
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(colors = GradientNeon)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🤖",
                            fontSize = 24.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "AI Analysis Available",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Get personalized insights & recommendations",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = NeonPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Action Buttons
            AnimatedGradientButton(
                text = "View AI Analysis",
                onClick = onAnalysisClick,
                modifier = Modifier.fillMaxWidth(),
                gradientColors = GradientNeon
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = {
                    scope.launch {
                        try {
                            viewModel.saveMeasurement(waistValue, hipValue)
                            android.widget.Toast.makeText(
                                context,
                                "Measurement saved to history!",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(
                                context,
                                "Failed to save: ${e.message}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Text("Save to History")
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
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
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                )
            }
        }
    }
}

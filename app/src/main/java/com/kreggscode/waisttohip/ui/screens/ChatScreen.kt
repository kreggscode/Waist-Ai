package com.kreggscode.waisttohip.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kreggscode.waisttohip.ui.components.*
import com.kreggscode.waisttohip.ui.theme.*
import com.kreggscode.waisttohip.data.PollinationsAI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.speech.tts.TextToSpeech
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    
    // Use the exact same color as the header background
    val backgroundColor = MaterialTheme.colorScheme.background
    val statusBarColor = backgroundColor
    
    // Set status bar color to match background
    val isLight = backgroundColor.luminance() > 0.5f
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = isLight
        )
    }
    
    val tts = remember {
        var textToSpeech: TextToSpeech? = null
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set language to US English for realistic voice
                textToSpeech?.let {
                    val result = it.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Handle error
                    }
                }
            }
        }
        textToSpeech
    }
    
    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }
    
    var messages by remember { mutableStateOf(listOf(
        ChatMessage(
            text = "Hello! I'm your AI Expert health coach. I can help you with personalized diet plans, nutrition advice, and answer any questions about your health metrics. What would you like to know?",
            isUser = false,
            timestamp = LocalDateTime.now()
        )
    )) }
    
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var speakingMessageIndex by remember { mutableStateOf<Int?>(null) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            ChatHeader(
                onBackClick = onBackClick,
                isFavorite = isFavorite,
                onFavoriteClick = { isFavorite = !isFavorite }
            )
        },
        bottomBar = {
            ChatInputField(
                value = inputText,
                onValueChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        val messageText = inputText
                        val userMessage = ChatMessage(
                            text = messageText,
                            isUser = true,
                            timestamp = LocalDateTime.now()
                        )
                        messages = messages + userMessage
                        inputText = ""
                        keyboardController?.hide()
                        
                        // Use real Pollinations AI
                        coroutineScope.launch {
                            isTyping = true
                            listState.animateScrollToItem(messages.size)
                            val aiResponse = try {
                                PollinationsAI.generateText(messageText)
                            } catch (e: Exception) {
                                "I apologize, but I'm having trouble connecting right now. Please try again in a moment."
                            }
                            messages = messages + ChatMessage(
                                text = aiResponse,
                                isUser = false,
                                timestamp = LocalDateTime.now()
                            )
                            isTyping = false
                            listState.animateScrollToItem(messages.size)
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp),
            reverseLayout = false
        ) {
            items(messages.size) { index ->
                val message = messages[index]
                ChatMessageItem(
                    message = message,
                    isSpeaking = speakingMessageIndex == index,
                    onSpeakClick = if (!message.isUser) {
                        {
                            if (speakingMessageIndex == index) {
                                // Stop speaking
                                tts?.stop()
                                speakingMessageIndex = null
                            } else {
                                // Start speaking
                                tts?.stop() // Stop any previous speech
                                tts?.speak(message.text, TextToSpeech.QUEUE_FLUSH, null, null)
                                speakingMessageIndex = index
                            }
                        }
                    } else null
                )
            }
            
            if (isTyping) {
                item {
                    TypingIndicator()
                }
            }
            
            // Quick suggestions at the bottom
            if (messages.size == 1) {
                item {
                    QuickSuggestions(
                        onSuggestionClick = { suggestion ->
                            inputText = suggestion
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatHeader(
    onBackClick: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Subtle purple overlay gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NeonPurple.copy(alpha = 0.12f),
                            NeonPurple.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )
        // Decorative gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonPurple.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(100f, 100f),
                        radius = 300f
                    )
                )
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            // Enhanced AI Avatar with glow effect
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    NeonPurple.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(colors = GradientNeon),
                            shape = CircleShape
                        )
                        .background(
                            brush = Brush.linearGradient(
                                colors = GradientNeon,
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(100f, 100f)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Psychology,
                        contentDescription = "AI Expert",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "AI Expert",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Animated pulse dot
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse_alpha"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(HealthyGreen.copy(alpha = pulseAlpha))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Online • Ready to help",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Star/Favorite button
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFavorite) NeonPurple.copy(alpha = 0.1f)
                        else Color.Transparent
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) NeonPurple else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isSpeaking: Boolean = false,
    onSpeakClick: (() -> Unit)? = null
) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bubbleColors = if (message.isUser) {
        listOf(Coral, CoralLight)
    } else {
        listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surface
        )
    }
    val textColor = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurface
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(colors = GradientNeon.map { it.copy(alpha = 0.2f) })
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🤖",
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = alignment
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 16.dp
                ),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = if (message.isUser) {
                                Brush.linearGradient(colors = bubbleColors)
                            } else {
                                Brush.linearGradient(colors = bubbleColors)
                            }
                        )
                        .then(
                            if (!message.isUser) {
                                Modifier.border(
                                    width = 1.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = 4.dp,
                                        bottomEnd = 16.dp
                                    )
                                )
                            } else Modifier
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = message.text,
                            modifier = Modifier.weight(1f, fill = false),
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                        
                        // Tiny speaker button at the end of AI message text
                        if (!message.isUser && onSpeakClick != null) {
                            IconButton(
                                onClick = onSpeakClick,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSpeaking) {
                                            Brush.linearGradient(colors = listOf(Coral, CoralLight))
                                        } else {
                                            Brush.linearGradient(colors = GradientNeon.map { it.copy(alpha = 0.2f) })
                                        }
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Rounded.Stop else Icons.Rounded.VolumeUp,
                                    contentDescription = if (isSpeaking) "Stop speaking" else "Speak",
                                    tint = if (isSpeaking) Color.White else NeonPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(colors = GradientNeon.map { it.copy(alpha = 0.2f) })
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🤖",
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .height(40.dp)
                .widthIn(min = 60.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
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
}

@Composable
fun QuickSuggestions(onSuggestionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Quick Questions",
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        val suggestions = listOf(
            "Create a weekly meal plan for me" to "🍽️",
            "What foods should I avoid?" to "🚫",
            "How can I improve my WHR?" to "📊",
            "Suggest high-protein snacks" to "💪"
        )
        
        suggestions.forEach { (text, emoji) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSuggestionClick(text) },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = emoji,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .background(Color.Transparent)
    ) {
        // Floating input container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 12.dp,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = if (value.isNotBlank()) GradientNeon.map { it.copy(alpha = 0.5f) }
                            else listOf(
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f),
                    placeholder = {
                        Text(
                            text = "Ask me anything...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { onSend() }
                    ),
                    singleLine = false,
                    maxLines = 4
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Send button with animation
                val scale by animateFloatAsState(
                    targetValue = if (value.isNotBlank()) 1f else 0.9f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "send_scale"
                )
                
                IconButton(
                    onClick = onSend,
                    enabled = value.isNotBlank(),
                    modifier = Modifier
                        .size(44.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = if (value.isNotBlank()) GradientNeon
                                else listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Send,
                        contentDescription = "Send",
                        tint = if (value.isNotBlank()) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: LocalDateTime
)

private fun generateAIResponse(userMessage: String): String {
    return when {
        userMessage.contains("meal plan", ignoreCase = true) -> {
            """
            I'll create a personalized weekly meal plan for you! Based on your WHR and goals, here's what I recommend:
            
            **Monday-Wednesday Focus: High Protein**
            • Breakfast: Greek yogurt parfait with berries and granola (350 kcal, 25g protein)
            • Lunch: Grilled chicken salad with quinoa (450 kcal, 35g protein)
            • Dinner: Baked salmon with roasted vegetables (500 kcal, 40g protein)
            • Snack: Protein shake with almond butter (250 kcal, 20g protein)
            
            **Thursday-Sunday: Balanced Macros**
            • Breakfast: Overnight oats with chia seeds (400 kcal)
            • Lunch: Turkey wrap with hummus and veggies (420 kcal)
            • Dinner: Lean beef stir-fry with brown rice (480 kcal)
            
            Would you like me to customize this further based on your preferences?
            """.trimIndent()
        }
        
        userMessage.contains("avoid", ignoreCase = true) -> {
            """
            Based on your health goals, here are foods to limit or avoid:
            
            **Minimize These:**
            • Processed foods high in trans fats
            • Sugary beverages and sodas
            • Refined carbohydrates (white bread, pastries)
            • Excessive alcohol consumption
            • High-sodium packaged snacks
            
            **Better Alternatives:**
            • Choose whole grains over refined ones
            • Opt for natural sweeteners like honey in moderation
            • Select lean proteins and plant-based options
            • Drink water, herbal teas, or infused water
            
            Remember, it's about balance - occasional treats are fine!
            """.trimIndent()
        }
        
        userMessage.contains("WHR", ignoreCase = true) || userMessage.contains("improve", ignoreCase = true) -> {
            """
            Great question! Here are evidence-based strategies to improve your WHR:
            
            **1. Targeted Exercise:**
            • Core strengthening exercises (planks, Russian twists)
            • HIIT workouts 3x per week
            • Resistance training for overall muscle building
            
            **2. Nutrition Optimization:**
            • Increase fiber intake (25-35g daily)
            • Focus on anti-inflammatory foods
            • Maintain a moderate caloric deficit (300-500 kcal)
            
            **3. Lifestyle Factors:**
            • Manage stress through meditation or yoga
            • Ensure 7-9 hours of quality sleep
            • Stay hydrated (8-10 glasses daily)
            
            With consistency, you can see improvements in 4-8 weeks!
            """.trimIndent()
        }
        
        userMessage.contains("protein", ignoreCase = true) || userMessage.contains("snack", ignoreCase = true) -> {
            """
            Here are my top high-protein snack recommendations:
            
            **Quick & Easy (5 minutes):**
            • Greek yogurt with nuts (15g protein)
            • Hard-boiled eggs with everything seasoning (12g protein)
            • Cottage cheese with berries (14g protein)
            • Protein smoothie bowl (25g protein)
            
            **Prep-Ahead Options:**
            • Homemade protein balls (8g each)
            • Tuna salad on cucumber slices (20g protein)
            • Edamame with sea salt (17g protein)
            • Turkey roll-ups with cheese (15g protein)
            
            **On-the-Go:**
            • Mixed nuts and seeds (6g per oz)
            • Protein bars (choose ones with <5g sugar)
            • String cheese and apple slices (8g protein)
            
            Which type of snack fits best with your lifestyle?
            """.trimIndent()
        }
        
        else -> {
            """
            That's an interesting question! Let me help you with that.
            
            Based on what you're asking, I can provide personalized guidance on nutrition, exercise, and lifestyle modifications to help you reach your health goals.
            
            Could you tell me more about what specific aspect you'd like to focus on? I can help with:
            • Custom meal planning
            • Exercise recommendations
            • Understanding your health metrics
            • Creating sustainable habits
            
            What would be most helpful for you right now?
            """.trimIndent()
        }
    }
}

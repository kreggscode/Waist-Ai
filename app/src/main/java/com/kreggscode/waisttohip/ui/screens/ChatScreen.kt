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
import com.kreggscode.waisttohip.ui.components.*
import com.kreggscode.waisttohip.ui.theme.*
import com.kreggscode.waisttohip.data.PollinationsAI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit
) {
    var messages by remember { mutableStateOf(listOf(
        ChatMessage(
            text = "Hello! I'm your AI Expert health coach. I can help you with personalized diet plans, nutrition advice, and answer any questions about your health metrics. What would you like to know?",
            isUser = false,
            timestamp = LocalDateTime.now()
        )
    )) }
    
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
        ) {
            // Header
            ChatHeader(onBackClick = onBackClick)
            
            // Messages
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
                reverseLayout = false
            ) {
                items(messages) { message ->
                    ChatMessageItem(message = message)
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
            
            // Input field
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
        }
    }
}

@Composable
fun ChatHeader(onBackClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // AI Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(colors = GradientNeon)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Psychology,
                    contentDescription = "AI Expert",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "AI Expert",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(HealthyGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Online",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
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
                    Text(
                        text = message.text,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = {
                    Text(
                        text = "Type your message...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
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
            
            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (value.isNotBlank()) GradientNeon else listOf(Color.Gray, Color.Gray)
                        )
                    )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
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

package com.kreggscode.waisttohip.data.repository

import com.kreggscode.waisttohip.data.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepository @Inject constructor(
    private val aiService: PollinationAIService
) {
    // No API key needed - Pollinations.AI is open source!
    
    suspend fun analyzeWHR(
        waistInches: Float,
        hipInches: Float
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val whr = waistInches / hipInches
            val prompt = """
                Analyze this Waist-to-Hip Ratio (WHR) measurement:
                - Waist: ${String.format("%.1f", waistInches)} inches
                - Hip: ${String.format("%.1f", hipInches)} inches
                - WHR: ${String.format("%.2f", whr)}
                
                Provide a comprehensive health analysis including:
                1. Health risk assessment
                2. Personalized recommendations
                3. Nutrition targets
                4. Exercise suggestions
                
                Format the response in a clear, structured way.
            """.trimIndent()
            
            val messages = listOf(
                ChatMessage(role = "system", content = "You are a health and fitness expert specializing in body composition analysis. Provide clean, professional responses without hashtags, markdown formatting, or special characters. Use simple paragraphs and bullet points only."),
                ChatMessage(role = "user", content = prompt)
            )
            
            val request = ChatCompletionRequest(
                model = "openai",
                messages = messages,
                temperature = 1.0f
            )
            
            val response = aiService.chatCompletion(request)
            
            if (response.isSuccessful && response.body() != null) {
                val content = response.body()!!.choices.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content.toString())
                } else {
                    Result.failure(Exception("No content in response"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendChatMessage(
        message: String,
        conversationHistory: List<ChatMessage> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val messages = mutableListOf<ChatMessage>()
            messages.add(ChatMessage(
                role = "system",
                content = "You are a helpful AI health coach specializing in nutrition, fitness, and wellness. Provide clean, professional responses without hashtags, markdown formatting, or special characters. Use simple paragraphs and bullet points only. Be concise and friendly."
            ))
            messages.addAll(conversationHistory)
            messages.add(ChatMessage(role = "user", content = message))
            
            val request = ChatCompletionRequest(
                model = "openai",
                messages = messages,
                temperature = 1.0f
            )
            
            val response = aiService.chatCompletion(request)
            
            if (response.isSuccessful && response.body() != null) {
                val content = response.body()!!.choices.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content.toString())
                } else {
                    Result.failure(Exception("No content in response"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun analyzeFoodImage(
        base64Image: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val imageUrl = "data:image/jpeg;base64,$base64Image"
            
            val contentParts = listOf(
                ContentPart(type = "text", text = """
                    You are a professional nutritionist analyzing food images. Carefully examine this image and identify ALL food items visible.
                    
                    For each food item you identify, provide:
                    - The EXACT name of the food (e.g., "Margherita Pizza", "Grilled Chicken Breast", "Caesar Salad")
                    - Accurate nutritional values per typical serving size
                    
                    Return your analysis as a JSON array with this EXACT structure (no markdown, no code blocks, no extra text):
                    [
                      {
                        "name": "Specific Food Name",
                        "calories": 250,
                        "protein": 15,
                        "carbs": 30,
                        "fat": 8,
                        "quantity": 1.0
                      }
                    ]
                    
                    CRITICAL RULES:
                    1. Use specific, descriptive food names (not generic like "food" or "item")
                    2. Identify ALL visible food items separately
                    3. Use realistic nutritional values based on standard serving sizes
                    4. All nutritional values must be whole numbers (grams)
                    5. Set quantity to 1.0 for each item
                    6. Return ONLY the JSON array - no explanations, no markdown formatting
                    7. If you see pizza, identify the type (e.g., "Pepperoni Pizza", "Cheese Pizza")
                    8. If you see multiple items, list each one separately
                    
                    Example for a pizza image:
                    [{"name": "Pepperoni Pizza", "calories": 285, "protein": 12, "carbs": 36, "fat": 10, "quantity": 1.0}]
                """.trimIndent()),
                ContentPart(type = "image_url", imageUrl = ImageUrl(url = imageUrl))
            )
            
            val messages = listOf(
                ChatMessage(
                    role = "system",
                    content = "You are an expert nutritionist and food recognition AI. You analyze food images and return precise nutritional data in clean JSON format. Never use markdown formatting or code blocks. Always identify specific food names, never use placeholders."
                ),
                ChatMessage(
                    role = "user",
                    content = contentParts
                )
            )
            
            val request = ChatCompletionRequest(
                model = "openai",
                messages = messages,
                temperature = 1.0f  // Set to 1.0 for Pollinations AI vision capabilities
            )
            
            val response = aiService.chatCompletion(request)
            
            if (response.isSuccessful && response.body() != null) {
                val content = response.body()!!.choices.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content.toString())
                } else {
                    Result.failure(Exception("No content in response"))
                }
            } else {
                Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

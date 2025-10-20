package com.kreggscode.waisttohip.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import org.json.JSONObject

object PollinationsAI {
    private const val TEXT_API_URL = "https://text.pollinations.ai"
    private const val IMAGE_API_URL = "https://image.pollinations.ai/prompt"
    
    /**
     * Generate text response using Pollinations AI
     * @param prompt The user's message/prompt
     * @param systemPrompt Optional system prompt to guide the AI
     * @return AI generated response
     */
    suspend fun generateText(
        prompt: String,
        systemPrompt: String = "You are a helpful AI health and nutrition expert. Provide clear, actionable advice without using asterisks or markdown formatting. Use bullet points with • symbol."
    ): String = withContext(Dispatchers.IO) {
        try {
            val fullPrompt = "$systemPrompt\n\nUser: $prompt\n\nAssistant:"
            val encodedPrompt = URLEncoder.encode(fullPrompt, "UTF-8")
            val urlString = "$TEXT_API_URL/$encodedPrompt"
            
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 30000
            connection.readTimeout = 30000
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                response.trim()
            } else {
                "I'm having trouble connecting right now. Please try again in a moment."
            }
        } catch (e: Exception) {
            "I apologize, but I'm experiencing technical difficulties. Please try again."
        }
    }
    
    /**
     * Analyze food image using Pollinations AI vision
     * @param imageUrl URL or base64 encoded image
     * @param prompt Optional prompt for analysis
     * @return Analysis result
     */
    suspend fun analyzeImage(
        imageUrl: String,
        prompt: String = "Analyze this food image and provide: 1) Food items identified, 2) Estimated calories per item, 3) Protein, carbs, and fat content. Format as: Item Name | Calories | Protein | Carbs | Fat"
    ): String = withContext(Dispatchers.IO) {
        try {
            // For vision analysis, we'll use the text API with image context
            val fullPrompt = "$prompt\n\nImage URL: $imageUrl"
            generateText(fullPrompt, "You are a food recognition AI. Analyze food images and provide nutritional information.")
        } catch (e: Exception) {
            "Unable to analyze image. Please try again."
        }
    }
    
    /**
     * Generate meal plan based on user data
     */
    suspend fun generateMealPlan(
        whr: Float,
        targetCalories: Int,
        dietaryPreferences: String = ""
    ): String {
        val prompt = """
            Create a personalized daily meal plan for someone with:
            - Waist-to-Hip Ratio: $whr
            - Target Calories: $targetCalories kcal
            - Dietary Preferences: ${if (dietaryPreferences.isEmpty()) "None" else dietaryPreferences}
            
            Provide:
            • Breakfast with calories and macros
            • Lunch with calories and macros
            • Dinner with calories and macros
            • 2 snacks with calories
            
            Keep it simple and practical.
        """.trimIndent()
        
        return generateText(prompt)
    }
    
    /**
     * Get exercise recommendations
     */
    suspend fun getExerciseRecommendations(whr: Float, fitnessLevel: String = "beginner"): String {
        val prompt = """
            Provide exercise recommendations for someone with WHR of $whr and fitness level: $fitnessLevel.
            
            Include:
            • 3-4 specific exercises
            • Sets and reps
            • Weekly frequency
            • Expected results timeline
            
            Keep it practical and achievable.
        """.trimIndent()
        
        return generateText(prompt)
    }
}

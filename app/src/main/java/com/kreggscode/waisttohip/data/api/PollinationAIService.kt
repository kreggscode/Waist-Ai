package com.kreggscode.waisttohip.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PollinationAIService {
    
    @GET("{prompt}")
    suspend fun generateText(
        @Path("prompt", encoded = true) prompt: String,
        @Query("model") model: String = "openai",
        @Query("temperature") temperature: Float = 1.0f,
        @Query("seed") seed: Int? = null,
        @Query("system") system: String? = null
    ): Response<String>
    
    @POST("openai")
    suspend fun chatCompletion(
        @Body request: ChatCompletionRequest
    ): Response<ChatCompletionResponse>
}

// Request Models for OpenAI-compatible endpoint
data class ChatCompletionRequest(
    @SerializedName("model") val model: String = "openai",
    @SerializedName("messages") val messages: List<ChatMessage>,
    @SerializedName("temperature") val temperature: Float = 1.0f,
    @SerializedName("seed") val seed: Int? = null,
    @SerializedName("stream") val stream: Boolean = false,
    @SerializedName("private") val private: Boolean = false
)

data class ChatMessage(
    @SerializedName("role") val role: String, // "system", "user", "assistant"
    @SerializedName("content") val content: Any // Can be String or List<ContentPart> for vision
)

// Vision API support
data class ContentPart(
    @SerializedName("type") val type: String, // "text" or "image_url"
    @SerializedName("text") val text: String? = null,
    @SerializedName("image_url") val imageUrl: ImageUrl? = null
)

data class ImageUrl(
    @SerializedName("url") val url: String // base64 data URL or HTTP URL
)

// Response Models (OpenAI-compatible format)
data class ChatCompletionResponse(
    @SerializedName("id") val id: String,
    @SerializedName("object") val objectType: String,
    @SerializedName("created") val created: Long,
    @SerializedName("model") val model: String,
    @SerializedName("choices") val choices: List<Choice>,
    @SerializedName("usage") val usage: Usage? = null
)

data class Choice(
    @SerializedName("index") val index: Int,
    @SerializedName("message") val message: ChatMessage,
    @SerializedName("finish_reason") val finishReason: String
)

data class Usage(
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int
)

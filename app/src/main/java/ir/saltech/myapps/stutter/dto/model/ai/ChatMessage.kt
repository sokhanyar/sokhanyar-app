package ir.saltech.myapps.stutter.dto.model.ai

import androidx.compose.runtime.mutableStateListOf
import com.google.gson.annotations.SerializedName
import kotlinx.datetime.Clock

data class ChatHistory(
    val id: Int,
    val contents: MutableList<ChatMessage> = mutableStateListOf(),
    @SerializedName("created_at")
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)

data class ChatMessage(
    val id: Int,
    val role: String,
    val content: String, // TODO: It must support all of contents .. audio image and video later.
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)

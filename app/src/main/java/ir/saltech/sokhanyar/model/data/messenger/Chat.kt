package ir.saltech.sokhanyar.model.data.messenger

import androidx.compose.runtime.mutableStateListOf
import ir.saltech.sokhanyar.model.data.general.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chat(
	@SerialName("chat_id")
	val id: Int,
	val type: ChatType,
	val users: List<User>,
	val messages: MutableList<ChatMessage> = mutableStateListOf(),
)

@Serializable
data class Chats(
	val chats: MutableList<Chat> = mutableListOf()
)

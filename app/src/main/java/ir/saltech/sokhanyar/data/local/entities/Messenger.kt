package ir.saltech.sokhanyar.data.local.entities

import androidx.compose.runtime.mutableStateListOf
import androidx.room.Entity
import androidx.room.ForeignKey
import ir.saltech.sokhanyar.BaseApplication.ChatType
import ir.saltech.sokhanyar.BaseApplication.MessageStatus
import ir.saltech.sokhanyar.BaseApplication.MessageType
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Chat(
	@SerialName("chat_id")
	val id: Int,
	val type: ChatType,
	val users: List<User>,
	val messages: MutableList<ChatMessage> = mutableStateListOf(),
)

@Entity
@Serializable
data class MessageMedia(
	@SerialName("media_id")
	val id: String,
	@SerialName("url")
	val remotePath: String,
	@SerialName("mime_type")
	val mimeType: String,
	val checksum: String,
	val size: Long,
	@SerialName("path")
	val localPath: String, // Local File Path
	val duration: Long? = null,
)

@Serializable
data class MessageContent(
	val text: String? = null,
	val media: MessageMedia? = null,
)

@Serializable
data class MessageReaction(
	val reaction: String, // Unicode emoji string
	val reactor: User,
)

// TODO: Chat messages route endpoints is necessary to be developed
@Entity(
	foreignKeys = [ForeignKey(
		User::class,
		parentColumns = ["uid"],
		childColumns = ["senderId"]
	)]
)
@Serializable
data class ChatMessage(
	@SerialName("message_id")
	val id: String,
	val chat: Chat,
	val senderId: String,
	val type: MessageType,
	val status: MessageStatus,
	val content: MessageContent, // TODO: It must support all of contents .. audio image and video later.
	val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
	val repliedTo: ChatMessage,
	val forwardedFrom: ChatMessage,
	val reactions: MutableList<MessageReaction> = mutableListOf(),
	val isPinned: Boolean = false,
)

package ir.saltech.sokhanyar.model.data.messenger

import ir.saltech.sokhanyar.model.data.general.User
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
enum class MessageStatus {
    @SerialName("sent") Sent, @SerialName("edited") Edited, @SerialName("read") Read
}

@Serializable
enum class ChatType {
    @SerialName("group") Group, @SerialName("channel") Channel, @SerialName("private") Private
}

@Serializable
enum class MessageType {
    @SerialName("post") Post, @SerialName("message") Message, @SerialName("comment") Comment
}


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
    val media: MessageMedia? = null
)

@Serializable
data class MessageReaction(
    val reaction: String, // Unicode emoji string
    val reactor: User
)

// TODO: Chat messages route endpoints is necessary to be developed
@Serializable
data class ChatMessage(
    @SerialName("message_id")
    val id: String,
    val chat: Chat,
    val sender: User,
    val type: MessageType,
    val status: MessageStatus,
    val content: MessageContent, // TODO: It must support all of contents .. audio image and video later.
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val repliedTo: ChatMessage,
    val forwardedFrom: ChatMessage,
    val reactions: MutableList<MessageReaction> = mutableListOf(),
    val isPinned: Boolean = false,
)

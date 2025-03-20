package ir.saltech.sokhanyar.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.BaseApplication.MessageStatus
import ir.saltech.sokhanyar.BaseApplication.MessageType
import ir.saltech.sokhanyar.data.local.dbconfig.Converters
import ir.saltech.sokhanyar.data.local.entity.serializer.FileSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Entity(
	foreignKeys = [ForeignKey(
		User::class,
		parentColumns = ["id"],
		childColumns = ["uploaderId"],
		onUpdate = CASCADE,
		onDelete = CASCADE
	)], indices = [Index("uploaderId")]
)
@Serializable
@TypeConverters(Converters::class)
data class Media(
	@PrimaryKey @SerialName("media_id") val id: String,
	@SerialName("uploader_id") val uploaderId: String,
	@SerialName("mime_type") val mimeType: String,
	val checksum: String,
	@Embedded val uploadStatus: UploadStatus = UploadStatus(),  // this won't be sent to server
	val size: Long,
	@SerialName("file") @Serializable(FileSerializer::class) val attachedFile: File,
	val duration: Long? = null,
)

@Serializable
data class UploadStatus(
	var uploadError: String? = null,
	var uploadProgress: Float? = null
)

@Entity(
	foreignKeys = [ForeignKey(
		User::class,
		parentColumns = ["id"],
		childColumns = ["senderId"],
		onUpdate = CASCADE,
		onDelete = CASCADE
	)], indices = [Index("senderId")]
)
@Serializable
data class Message(
	@PrimaryKey @SerialName("message_id") val id: String,
	@SerialName("chat_id") val chatId: String,
	@SerialName("sender_id") val senderId: String,
	@SerialName("post_id") val postId: String? = null,
	val type: MessageType = MessageType.Message,
	var status: MessageStatus,
	var text: String,
	@SerialName("replied_to_id") val repliedToId: String? = null,
	@SerialName("forwarded_from_id") val forwardedFromId: String? = null,
	@SerialName("is_pinned") var isPinned: Boolean = false,
	@SerialName("sent_at") val sentAt: Long,
)


@Entity(
	tableName = "message_media",
	foreignKeys = [ForeignKey(Media::class, ["id"], ["mediaId"]), ForeignKey(
		Message::class, ["id"], ["messageId"], onUpdate = CASCADE, onDelete = CASCADE
	)],
	primaryKeys = ["messageId", "mediaId"],
	indices = [Index("messageId"), Index("mediaId")]
)
@Serializable
data class MessageMedia(
	@SerialName("message_id") val messageId: String,
	@SerialName("media_id") val mediaId: String,
	var status: BaseApplication.MediaStatus = BaseApplication.MediaStatus.Active,
	@SerialName("edited_at") var editedAt: Long? = null,
	@SerialName("deleted_at") var deletedAt: Long? = null,
	@SerialName("attached_at") val attachedAt: Long,
)


@Entity(
	tableName = "message_reactions",
	foreignKeys = [ForeignKey(User::class, ["id"], ["reactorId"]), ForeignKey(
		Message::class, ["id"], ["messageId"], onUpdate = CASCADE, onDelete = CASCADE
	)],
	primaryKeys = ["messageId", "reactorId"],
	indices = [Index("messageId"), Index("reactorId")]
)
@Serializable
data class MessageReaction(
	@SerialName("message_id") val messageId: String,
	@SerialName("reactor_id") val reactorId: String,
	var reaction: String,
	@SerialName("reacted_at") var reactedAt: Long,
)


@Entity(
	tableName = "message_reads",
	foreignKeys = [ForeignKey(User::class, ["id"], ["readerId"]), ForeignKey(
		Message::class, ["id"], ["messageId"], onUpdate = CASCADE, onDelete = CASCADE
	)],
	primaryKeys = ["messageId", "readerId"],
	indices = [Index("messageId"), Index("readerId")]
)
@Serializable
data class MessageRead(
	@SerialName("message_id") val messageId: String,
	@SerialName("reader_id") val readerId: String,
	@SerialName("read_at") val readAt: Long,
)


@Entity(
	tableName = "message_reports",
	foreignKeys = [ForeignKey(User::class, ["id"], ["reporterId"]), ForeignKey(
		Message::class, ["id"], ["messageId"], onUpdate = CASCADE, onDelete = CASCADE
	)],
	primaryKeys = ["messageId", "reporterId"],
	indices = [Index("messageId"), Index("reporterId")]
)
@Serializable
data class MessageReport(
	@SerialName("message_id") val messageId: String,
	@SerialName("reporter_id") val reporterId: String,
	val reason: String,
	@SerialName("reported_at") val reportedAt: Long,
)


@Entity(
	foreignKeys = [ForeignKey(User::class, ["id"], ["createdById"])],
	indices = [Index("createdById")]
)
@Serializable
data class Chat(
	@PrimaryKey @SerialName("chat_id") val id: Int,
	val type: BaseApplication.ChatType,
	var status: BaseApplication.ChatStatus,
	@SerialName("created_by")
	val createdById: String,
	@SerialName("created_at")
	val createdAt: Long,
	@SerialName("status_changed_at")
	var statusChangedAt: Long,
	@SerialName("last_message_at")
	var lastMessageAt: Long,
	@Embedded @SerialName("type_properties") val typeProperties: ChatTypeProperties? = null,
)

@Serializable
sealed class ChatTypeProperties {

	@Serializable
	@SerialName("private_chat")
	data class PrivateChat(
		@SerialName("associated_user1_id")
		val associatedUser1Id: String,
		@SerialName("associated_user2_id")
		val associatedUser2Id: String,
	)

	@Serializable
	@SerialName("group")
	data class Group(
		var name: String,
		@SerialName("avatar_media_id") var avatarMediaId: String,
		var description: String
	)

	@Serializable
	@SerialName("channel")
	data class Channel(
		var name: String,
		@SerialName("avatar_media_id") var avatarMediaId: String,
		var description: String
	)
}

@Entity(
	tableName = "group_members",
	primaryKeys = ["groupId", "memberId"],
	foreignKeys = [
		ForeignKey(
			entity = Chat::class,
			parentColumns = ["id"],
			childColumns = ["groupId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["memberId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		)
	],
	indices = [Index("groupId"), Index("memberId")]
)
@Serializable
data class GroupMember(
	@SerialName("group_id") val groupId: String,
	@SerialName("member_id") val memberId: String,
	@SerialName("joint_at") val jointAt: Long
)

@Entity(
	tableName = "group_admins",
	primaryKeys = ["groupId", "adminId"],
	foreignKeys = [
		ForeignKey(
			entity = Chat::class,
			parentColumns = ["id"],
			childColumns = ["groupId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["adminId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		)
	],
	indices = [Index("groupId"), Index("adminId")]
)
@Serializable
data class GroupAdmin(
	@SerialName("group_id") val groupId: String,
	@SerialName("admin_id") val adminId: String,
	@SerialName("promoted_at") val promotedAt: Long
)

@Entity(
	tableName = "group_bans",
	primaryKeys = ["groupId", "bannedUserId"],
	foreignKeys = [
		ForeignKey(
			entity = Chat::class,
			parentColumns = ["id"],
			childColumns = ["groupId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["bannedUserId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		)
	],
	indices = [Index("groupId"), Index("bannedUserId")]
)
@Serializable
data class GroupBan(
	@SerialName("group_id") val groupId: String,
	@SerialName("banned_user_id") val bannedUserId: String,
	val reason: String? = null,
	@SerialName("banned_at") val bannedAt: Long
)

@Entity(
	tableName = "channel_subscribers",
	primaryKeys = ["channelId", "subscriberId"],
	foreignKeys = [
		ForeignKey(
			entity = Chat::class,
			parentColumns = ["id"],
			childColumns = ["channelId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["subscriberId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		)
	],
	indices = [Index("channelId"), Index("subscriberId")]
)
@Serializable
data class ChannelSubscriber(
	@SerialName("channel_id") val channelId: String,
	@SerialName("subscriber_id") val subscriberId: String,
	@SerialName("joined_at") val joinedAt: Long
)

@Entity(
	tableName = "channel_admins",
	primaryKeys = ["channelId", "adminId"],
	foreignKeys = [
		ForeignKey(
			entity = Chat::class,
			parentColumns = ["id"],
			childColumns = ["channelId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["adminId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		)
	],
	indices = [Index("channelId"), Index("adminId")]
)
@Serializable
data class ChannelAdmin(
	@SerialName("channel_id") val channelId: String,
	@SerialName("admin_id") val adminId: String,
	@SerialName("promoted_at") val promotedAt: Long
)

@Entity(
	tableName = "channel_bans",
	primaryKeys = ["channelId", "bannedUserId"],
	foreignKeys = [
		ForeignKey(
			entity = Chat::class,
			parentColumns = ["id"],
			childColumns = ["channelId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["bannedUserId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		)
	],
	indices = [Index("channelId"), Index("bannedUserId")]
)
@Serializable
data class ChannelBan(
	@SerialName("channel_id") val channelId: String,
	@SerialName("banned_user_id") val bannedUserId: String,
	val reason: String? = null,
	@SerialName("banned_at") val bannedAt: Long
)



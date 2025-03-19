package ir.saltech.sokhanyar.data.local.entity.treatment

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ir.saltech.sokhanyar.BaseApplication.CallType
import ir.saltech.sokhanyar.data.local.dbconfig.Converters
import ir.saltech.sokhanyar.data.local.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Entity(tableName = "call")
@Serializable
@TypeConverters(Converters::class)
data class Call(
	@PrimaryKey val id: String,
	val type: CallType,
	val subject: String? = null,
	val questions: List<String>? = null,
	@SerialName("meet_url") val meetUrl: String? = null,
	@SerialName("started_at") val startedAt: Long? = null,
	@SerialName("ended_at") val endedAt: Long? = null,
	@SerialName("created_at") val createdAt: Long,
)

@Entity(
	tableName = "call_participants", foreignKeys = [ForeignKey(
		entity = Call::class,
		parentColumns = ["id"],
		childColumns = ["callId"],
		onDelete = ForeignKey.CASCADE,
		onUpdate = ForeignKey.CASCADE
	), ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["patientId"],
		onDelete = ForeignKey.CASCADE,
		onUpdate = ForeignKey.CASCADE
	)], primaryKeys = ["callId", "patientId"], indices = [Index("id"), Index("callId"), Index("patientId")]
)
@Serializable
@TypeConverters(Converters::class)
data class CallParticipant(
	val callId: String,
	val patientId: String,
	@SerialName("joined_at") val joinedAt: Long,
	@SerialName("left_at") val leftAt: Long? = null,
)

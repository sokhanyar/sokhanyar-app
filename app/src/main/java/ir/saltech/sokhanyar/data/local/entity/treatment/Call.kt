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


@Entity
@Serializable
@TypeConverters(Converters::class)
data class Call(
	@PrimaryKey @SerialName("call_id") val id: String,
	val type: CallType,
	val subject: String? = null,
	var questions: List<String>? = null,
	@SerialName("meet_url") var meetUrl: String? = null,
	@SerialName("started_at") var startedAt: Long? = null,
	@SerialName("ended_at") var endedAt: Long? = null,
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
	)], primaryKeys = ["callId", "patientId"], indices = [Index("callId"), Index("patientId")]
)
@Serializable
data class CallParticipant(
	val callId: String,
	@SerialName("patient_id") val patientId: String,
	@SerialName("joined_at") val joinedAt: Long,
	@SerialName("left_at") var leftAt: Long? = null,
)

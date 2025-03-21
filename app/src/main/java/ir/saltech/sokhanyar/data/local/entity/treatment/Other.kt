package ir.saltech.sokhanyar.data.local.entity.treatment

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.BaseApplication.VisitType
import ir.saltech.sokhanyar.data.local.dbconfig.Converters
import ir.saltech.sokhanyar.data.local.entity.User
import ir.saltech.sokhanyar.data.local.entity.serializer.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Entity(foreignKeys = [ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["issuerId"],
		onDelete = ForeignKey.CASCADE,
		onUpdate = ForeignKey.CASCADE
	)], indices = [Index("issuerId")]
)
@Serializable
@TypeConverters(Converters::class)
data class Visit(
	@PrimaryKey @SerialName("visit_id") val id: String,
	@SerialName("issuer")
	val issuerId: String,
	val type: VisitType = VisitType.Personal,
	val fee: Int? = null,
	@Serializable(DateSerializer::class)
	val date: Date,
	@SerialName("receipt_number") var receiptNumber: String? = null,
	@SerialName("paid_at") var paidAt: Long? = null,
	@SerialName("created_at") val createdAt: Long,
)

@Entity(foreignKeys = [ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["adviserId"],
		onDelete = ForeignKey.CASCADE,
		onUpdate = ForeignKey.CASCADE
	)], indices = [Index("adviserId")]
)
@Serializable
data class Advice(
	@PrimaryKey @SerialName("advice_id") val id: String,
	@SerialName("adviser_id") val adviserId: String,
	val subject: String? = null,
	val description: String? = null,
	@SerialName("previous_advice") var previousAdvice: String? = null,
	val transcription: String? = null,
	@SerialName("user_feedback") var userFeedback: BaseApplication.AiUserFeedback? = null,
	@SerialName("created_at") val createdAt: Long,
)

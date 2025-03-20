package ir.saltech.sokhanyar.data.local.entity.treatment
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ir.saltech.sokhanyar.BaseApplication.VisitType
import ir.saltech.sokhanyar.data.local.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(foreignKeys = [ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["issuer"],
		onDelete = ForeignKey.CASCADE,
		onUpdate = ForeignKey.CASCADE
	)], indices = [Index("id"), Index("issuer")]
)
@Serializable
data class Visit(
	@PrimaryKey val id: String,
	val issuer: String,
	val type: VisitType = VisitType.Personal,
	val fee: Int? = null,
	val date: Long,
	@SerialName("receipt_number") val receiptNumber: String? = null,
	@SerialName("paid_at") val paidAt: Long? = null,
	@SerialName("created_at") val createdAt: Long,
)

@Entity(foreignKeys = [ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["adviserId"],
		onDelete = ForeignKey.CASCADE,
		onUpdate = ForeignKey.CASCADE
	)], indices = [Index("id"), Index("adviserId")]
)
@Serializable
data class Advice(
	@PrimaryKey val id: String,
	val adviserId: String,
	val subject: String? = null,
	val description: String? = null,
	@SerialName("created_at") val createdAt: Long,
)

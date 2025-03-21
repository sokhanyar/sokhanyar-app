package ir.saltech.sokhanyar.data.local.entity.treatment

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ir.saltech.sokhanyar.data.local.entity.Media
import ir.saltech.sokhanyar.data.local.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(
	tableName = "practical_voices",
	foreignKeys = [ForeignKey(
		User::class,
		parentColumns = ["id"],
		childColumns = ["patientId"]
	), ForeignKey(
		Media::class,
		parentColumns = ["id"],
		childColumns = ["mediaId"]
	)], indices = [Index("patientId"), Index("mediaId")]
)
@Serializable
data class PracticalVoice(
	@PrimaryKey(autoGenerate = true)
	@SerialName("practical_voice_id")
	val id: Long,
	@SerialName("patient_id")
	val patientId: String? = null, // The user must be a type of role patient
	@SerialName("media_id") val mediaId: String, // The file must have a media id even though it wouldn't upload
	var subject: String? = null,
)

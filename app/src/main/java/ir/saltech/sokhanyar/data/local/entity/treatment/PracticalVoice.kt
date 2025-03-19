package ir.saltech.sokhanyar.data.local.entity.treatment

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.data.local.entity.Media
import ir.saltech.sokhanyar.data.local.entity.User
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Entity(
	foreignKeys = [ForeignKey(
		User::class,
		parentColumns = ["id"],
		childColumns = ["patientId"]
	)], indices = [Index("id"), Index("patientId")]
)
@Serializable
data class PracticalVoice(
	@PrimaryKey(autoGenerate = true)
	val id: Long,
	val patientId: String? = null, // The user must be a type of role patient
	@Deprecated("Use `media` instead; This property will be removed soon.")
	@SerialName("selected_file")
	@Contextual // TODO: This flag is temporarily.
	val selectedFile: File? = null,
	val media: Media? = null,
	val checksum: String? = null,
	val error: String? = null,
	val progress: Float? = null,
	val subject: String? = null,
	val response: VoiceResponse? = null,
)

@Serializable
data class VoiceResponse(
	val advice: String? = null,
	val lastAdvice: String? = null,
	val transcription: String? = null,
	val aiResponseFeedback: BaseApplication.AiResponseFeedback? = null,
)

package ir.saltech.sokhanyar.model.data.treatment

import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.model.data.general.Patient
import ir.saltech.sokhanyar.model.data.general.User
import ir.saltech.sokhanyar.model.data.messenger.MessageMedia
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class Voice(
    val patient: User? = null, // The user must be a type of role patient
    @Deprecated("Use `media` instead; This property will be removed soon.") @SerialName("selected_file") @Contextual // TODO: This flag is temporarily.
    val selectedFile: File? = null,
    val media: MessageMedia? = null,
    val checksum: String? = null,
    val error: String? = null,
    val progress: Float? = null,
    val subject: String? = null,
    val advice: String? = null,
    val transcription: String? = null,
    @Deprecated("use internal `advice` and `transcription` attributes instead; For dislike strategy, use other plans.")
    val response: VoiceResponse? = null,
)

@Serializable
data class VoiceResponse(
    val feedback: String? = null,
    val lastFeedback: String? = null,
    val transcription: String? = null,
    val feedbackOfFeedback: BaseApplication.FeedbackOfFeedback? = null
)

package ir.saltech.sokhanyar.model.data.treatment

import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.model.data.general.Patient
import java.io.File

data class Voice(
    val patient: Patient? = null,
    val selectedFile: File? = null,
    val mediaId: String? = null,
    val checksum: String? = null,
    val error: String? = null,
    val progress: Float? = null,
    val subject: String? = null,
    val response: VoiceResponse? = null,
)

data class VoiceResponse(
    val feedback: String? = null,
    val lastFeedback: String? = null,
    val transcription: String? = null,
    val feedbackOfFeedback: BaseApplication.FeedbackOfFeedback? = null
)

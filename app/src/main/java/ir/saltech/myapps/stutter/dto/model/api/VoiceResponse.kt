package ir.saltech.myapps.stutter.dto.model.api

data class VoiceResponse (
    val feedback: String? = null,
    val transcription: String? = null,
    val feedbackOfFeedback: String? = null
)
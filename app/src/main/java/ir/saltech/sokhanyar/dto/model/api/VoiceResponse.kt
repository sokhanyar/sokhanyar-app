package ir.saltech.sokhanyar.dto.model.api

import ir.saltech.sokhanyar.BaseApplication


data class VoiceResponse(
    val feedback: String? = null,
    val lastFeedback: String? = null,
    val transcription: String? = null,
    val feedbackOfFeedback: BaseApplication.FeedbackOfFeedback? = null
)
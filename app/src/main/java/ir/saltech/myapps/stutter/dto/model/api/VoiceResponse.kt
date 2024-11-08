package ir.saltech.myapps.stutter.dto.model.api

import ir.saltech.myapps.stutter.BaseApplication


data class VoiceResponse(
    val feedback: String? = null,
    val lastFeedback: String? = null,
    val transcription: String? = null,
    val feedbackOfFeedback: BaseApplication.FeedbackOfFeedback? = null
)
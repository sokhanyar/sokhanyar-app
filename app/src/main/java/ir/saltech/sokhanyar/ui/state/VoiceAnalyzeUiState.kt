package ir.saltech.sokhanyar.ui.state

import android.media.MediaPlayer
import ir.saltech.sokhanyar.model.data.general.User
import ir.saltech.sokhanyar.model.data.treatment.Voice

data class VoiceAnalyzeUiState(
    val user: User? = null,
    val voice: Voice? = null,
    val mediaPlayer: MediaPlayer? = null
)

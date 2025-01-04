package ir.saltech.sokhanyar.ui.state

import ir.saltech.sokhanyar.model.data.general.User
import ir.saltech.sokhanyar.model.data.general.Voice

data class VoiceAnalyzeUiState(
    val user: User = User(),
    val voice: Voice = Voice(),
)

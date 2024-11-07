package ir.saltech.myapps.stutter.ui.state

import ir.saltech.myapps.stutter.dto.model.data.general.User
import ir.saltech.myapps.stutter.dto.model.data.general.Voice
import ir.saltech.myapps.stutter.util.RecursiveFileObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class VoiceAnalyzeUiState(
    val user: User = User(),
    val voice: Voice = Voice(),
    var fileObserver: StateFlow<RecursiveFileObserver?> = MutableStateFlow(null)
)

package ir.saltech.sokhanyar.ui.state

import android.net.Uri
import ir.saltech.sokhanyar.model.data.general.User
import ir.saltech.sokhanyar.model.data.general.Voice
import ir.saltech.sokhanyar.util.RecursiveFileObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class VoiceAnalyzeUiState(
    val user: User = User(),
    val voice: Voice = Voice(),
    var fileObserver: StateFlow<RecursiveFileObserver?> = MutableStateFlow(null),
    val intentVoiceFiles: List<Uri>? = null
)

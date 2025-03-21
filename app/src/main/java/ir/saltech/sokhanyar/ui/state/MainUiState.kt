package ir.saltech.sokhanyar.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.model.data.general.Clinic
import ir.saltech.sokhanyar.model.data.general.User
import ir.saltech.sokhanyar.model.data.messenger.Chats
import ir.saltech.sokhanyar.model.data.treatment.report.DailyReport
import ir.saltech.sokhanyar.model.data.treatment.report.DailyReports
import ir.saltech.sokhanyar.model.data.treatment.report.WeeklyReport
import ir.saltech.sokhanyar.model.data.treatment.report.WeeklyReports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MainUiState(
    val activePages: MutableList<BaseApplication.Page> = mutableStateListOf(),
    val sentence: String? = null,
    val realAdvice: MutableState<String?> = mutableStateOf(null),
    val clinics: List<Clinic> = listOf(),
    var chats: StateFlow<Chats> = MutableStateFlow(Chats()),
    val dailyReports: DailyReports? = null,
    val weeklyReports: WeeklyReports? = null,
    val currentDailyReport: DailyReport? = null,
    val currentWeeklyReport: WeeklyReport? = null,
    val currentUser: User? = null,
)

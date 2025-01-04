package ir.saltech.sokhanyar.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.model.api.ChatHistory
import ir.saltech.sokhanyar.model.data.general.User
import ir.saltech.sokhanyar.model.data.reports.DailyReport
import ir.saltech.sokhanyar.model.data.reports.DailyReports
import ir.saltech.sokhanyar.model.data.reports.WeeklyReport
import ir.saltech.sokhanyar.model.data.reports.WeeklyReports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MainUiState(
    val activePages: MutableList<BaseApplication.Page> = mutableStateListOf(),
    val sentence: String? = null,
    val advice: MutableState<String?> = mutableStateOf(null),
    var chatHistory: StateFlow<ChatHistory> = MutableStateFlow(ChatHistory(0)),
    val dailyReport: DailyReport = DailyReport(),
    val weeklyReport: WeeklyReport = WeeklyReport(),
    val dailyReports: DailyReports? = null,
    val weeklyReports: WeeklyReports? = null,
    val user: User = User(),
)

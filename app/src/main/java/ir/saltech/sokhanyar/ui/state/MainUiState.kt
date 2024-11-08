package ir.saltech.sokhanyar.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.dto.model.api.ChatHistory
import ir.saltech.sokhanyar.dto.model.data.general.User
import ir.saltech.sokhanyar.dto.model.data.reports.DailyReport
import ir.saltech.sokhanyar.dto.model.data.reports.DailyReports
import ir.saltech.sokhanyar.dto.model.data.reports.WeeklyReport
import ir.saltech.sokhanyar.dto.model.data.reports.WeeklyReports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MainUiState(
    @Deprecated("use activePages instead")
    val page: BaseApplication.Page = BaseApplication.Page.Home,
    val activePages: MutableList<BaseApplication.Page> = mutableStateListOf(BaseApplication.Page.Home),
    val sentence: String? = null,
    val advice: MutableState<String?> = mutableStateOf(null),
    var chatHistory: StateFlow<ChatHistory> = MutableStateFlow(ChatHistory(0)),
    val dailyReport: _root_ide_package_.ir.saltech.sokhanyar.dto.model.data.reports.DailyReport = _root_ide_package_.ir.saltech.sokhanyar.dto.model.data.reports.DailyReport(),
    val weeklyReport: WeeklyReport = WeeklyReport(),
    val dailyReports: _root_ide_package_.ir.saltech.sokhanyar.dto.model.data.reports.DailyReports? = null,
    val weeklyReports: WeeklyReports? = null,
    val user: User = User()
)

package ir.saltech.myapps.stutter.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.dto.model.DailyReport
import ir.saltech.myapps.stutter.dto.model.DailyReports
import ir.saltech.myapps.stutter.dto.model.WeeklyReport
import ir.saltech.myapps.stutter.dto.model.WeeklyReports

data class MainUiState(
    @Deprecated("use activePages instead")
    val page: BaseApplication.Page = BaseApplication.Page.Home,
    val activePages: MutableList<BaseApplication.Page> = mutableStateListOf(BaseApplication.Page.Home),
    val sentence: String? = null,
    val advice: MutableState<String?> = mutableStateOf(null),
    //val chatHistory: ChatHistory? = null,
    val dailyReport: DailyReport = DailyReport(),
    val weeklyReport: WeeklyReport = WeeklyReport(),
    val dailyReports: DailyReports? = null,
    val weeklyReports: WeeklyReports? = null
)

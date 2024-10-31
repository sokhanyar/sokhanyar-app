package ir.saltech.myapps.stutter.ui.state

import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.dto.model.DailyReport
import ir.saltech.myapps.stutter.dto.model.DailyReports
import ir.saltech.myapps.stutter.dto.model.WeeklyReport
import ir.saltech.myapps.stutter.dto.model.WeeklyReports

data class MainUiState(
    val page: BaseApplication.Page = BaseApplication.Page.Home,
    val sentence: String? = null,
    val advice: String? = null,
    val dailyReport: DailyReport = DailyReport(),
    val weeklyReport: WeeklyReport = WeeklyReport(),
    val dailyReports: DailyReports? = null,
    val weeklyReports: WeeklyReports? = null
)

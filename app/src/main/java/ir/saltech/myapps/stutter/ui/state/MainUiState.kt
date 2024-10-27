package ir.saltech.myapps.stutter.ui.state

import androidx.compose.runtime.mutableStateListOf
import com.aallam.openai.api.chat.ChatChunk
import ir.saltech.myapps.stutter.dto.model.Credit
import ir.saltech.myapps.stutter.dto.model.DailyReport
import ir.saltech.myapps.stutter.dto.model.DailyReports
import ir.saltech.myapps.stutter.dto.model.WeeklyReport
import ir.saltech.myapps.stutter.dto.model.WeeklyReports

data class MainUiState(
    val sentence: MutableList<ChatChunk> = mutableStateListOf(),
    val advice: String? = null,
    val credit: Credit? = null,
    val speech: ByteArray? = null,
    val dailyReport: DailyReport = DailyReport(),
    val weeklyReport: WeeklyReport = WeeklyReport(),
    val dailyReports: DailyReports? = null,
    val weeklyReports: WeeklyReports? = null
)

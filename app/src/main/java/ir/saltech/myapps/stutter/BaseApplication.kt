package ir.saltech.myapps.stutter

import android.app.Application
import androidx.datastore.preferences.core.stringPreferencesKey

class BaseApplication : Application() {
    object Constants {
        const val API_KEY = "aa-2NLiIj2PuzdAXTcOBOYtCr4l1eORHkX5o1Raj1tKi0pNtJZU"
        const val BASE_URL = "https://api.avalai.ir"
        const val MAX_OF_NAME_CHARS = 25
        const val MAX_OF_DAILY_REPORT_PAGES = 4
        const val MAX_OF_WEEKLY_REPORT_PAGES = 3
        const val MOTIVATION_WITH_SPEECH = false
        const val AI_CREDITS_SHOW = false
    }

    object Key {
        val DailyReports = stringPreferencesKey("daily_reports")
        val WeeklyReports = stringPreferencesKey("weekly_reports")
    }

    enum class MenuItem {
        Motivation,
        SendDailyReport,
        SendWeeklyReport
    }

    enum class EffectSide {
        Forward, Backward, Unknown
    }
}
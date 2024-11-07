package ir.saltech.myapps.stutter.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.webkit.MimeTypeMap
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.aallam.openai.api.chat.ChatChunk
import com.google.gson.Gson
import gregorian_to_jalali
import ir.saltech.ai.client.generativeai.type.Content
import ir.saltech.ai.client.generativeai.type.asTextOrNull
import ir.saltech.ai.client.generativeai.type.content
import ir.saltech.myapps.stutter.BaseApplication
import ir.saltech.myapps.stutter.dto.model.api.ChatMessage
import ir.saltech.myapps.stutter.dto.model.data.reports.DailyReport
import ir.saltech.myapps.stutter.dto.model.data.reports.DailyReports
import ir.saltech.myapps.stutter.dto.model.data.reports.WeeklyReport
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import java.io.File
import java.util.Calendar
import java.util.Date


@Override
fun MutableList<ChatChunk>.response(): String =
    this.joinToString("") { it.delta?.content ?: "" }.trim()

inline fun <reified T> fromJson(json: String?): T? {
    return Gson().fromJson(json ?: return null, T::class.java)
}

inline fun <reified T> toJson(t: T?): String? {
    return Gson().toJson(t ?: return null)
}

fun String.asToken(): String {
    return "Bearer $this"
}

fun Date.toJalali(): IntArray {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val jalali = gregorian_to_jalali(
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH] + 1,
        calendar[Calendar.DAY_OF_MONTH]
    )
    return intArrayOf(0, 0, 0, calendar[Calendar.DAY_OF_WEEK]).apply { jalali.copyInto(this) }
}

fun Int.asJalaliMonth(): String {
    return arrayOf(
        "فروردین",
        "اردیبهشت",
        "خرداد",
        "تیر",
        "مرداد",
        "شهریور",
        "مهر",
        "آبان",
        "آذر",
        "دی",
        "بهمن",
        "اسفند"
    )[this - 1]
}

fun Int.asJalaliDay(): String {
    return arrayOf("یکشنبه", "دوشنبه", "سه شنبه", "چهارشنبه", "پنجشنبه", "جمعه", "شنبه")[this - 1]
}

fun IntArray.toDayReportDate(): String {
    return "${this[3].asJalaliDay()}، ${this[2]} ${this[1].asJalaliMonth()}"
}

private const val DAY_IN_MILLIS = 86400000

fun DailyReports.getLastDailyReports(): List<DailyReport>? {
    return this.list.filter {
        (it.date ?: return null) > Clock.System.now().toEpochMilliseconds() - (DAY_IN_MILLIS * 7)
    }.ifEmpty { null }
}

fun Clock.nowDay(): Long {
    return this.todayIn(TimeZone.currentSystemDefault()).toEpochDays().toLong() * DAY_IN_MILLIS
}

infix fun Long.isTomorrow(intended: Long?): Boolean {
    if (intended != null) {
        val toIntendedDay: Int = (intended / DAY_IN_MILLIS).toInt()
        val toCurrentDay: Int = (this / DAY_IN_MILLIS).toInt()
        return toCurrentDay > toIntendedDay
    } else {
        return true
    }
}

fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

fun Int.toRegularTime(): String {
    val hours = this / 60
    val minutes = this % 60
    return when {
        hours > 0 && minutes > 0 -> "$hours ساعت و $minutes دقیقه"
        hours > 0 -> "$hours ساعت"
        else -> "$minutes دقیقه"
    }
}

infix fun Int?.plusNull(second: Int?): Int {
    return (this ?: 0) + (second ?: 0)
}

fun WeeklyReport.getSumOfActivities(): WeeklyReport {
    return this.copy(sumOfActivities = this.let {
        val sumOfAllActivities =
            it.practiceDays plusNull it.desensitizationCount plusNull it.voicesProperties.challengesCount plusNull it.voicesProperties.sumOfChallengesDuration plusNull it.voicesProperties.sumOfConferencesDuration plusNull it.voicesProperties.conferenceDaysCount plusNull it.dailyReportsCount plusNull it.creationOfExceptionCount plusNull it.callsCount.groupCallsCount plusNull it.callsCount.teenSupportCallsCount plusNull it.callsCount.adultSupportCallsCount
        sumOfAllActivities
    })
}

fun getGreetingBasedOnTime(command: Boolean = false): String {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    return when (currentHour) {
        in 4..<11 -> if (command) "الان صبحه؛" else "سلام! 👋\n صبح قشنگت بخیر! 😇"
        in 11..<12 -> if (command) "الان نزدیک ظهره؛" else "سلام! 👋\n نزدیک ظهره، روزت بخیر! 🌞"
        in 12..<13 -> if (command) "الان ظهره؛" else "سلام! 👋\n ظهر بخیر، گرسنه‌ت نیست؟ 😋"
        in 13..<15 -> if (command) "الان بعد از ظهره؛" else "سلام! 👋\n بعد از ظهرت بخیر، امیدوارم عالی بگذره! 😊"
        in 15..<17 -> if (command) "الان عصره؛" else "سلام! 👋\n عصر بخیر، خسته نباشی! 😊"
        in 17..<20 -> if (command) "الان ابتدای شبه؛" else "سلام! 👋\n غروب زیبای امروز چطور بود؟ 🌇"
        in 20..23 -> if (command) "الان آخر شبه؛" else "سلام! 👋\n شب بخیر، خواب‌های خوب ببینی! 🌙"
        else -> if (command) "الان نیمه شبه؛" else "سلام! 👋\n به دنیای بیدارها خوش اومدی! 🦉" // for late night/early morning
    }
}

@Composable
fun Modifier.wrapToScreen(reverse: Boolean = false): Modifier {
    return this.let {
        if (androidx.compose.ui.platform.LocalConfiguration.current.screenHeightDp.dp < 600.dp) {
            if (reverse) {
                it.height(0.dp)
            } else {
                it.fillMaxHeight()
            }
        } else {
            it.wrapContentHeight()
        }
    }
}

fun MutableList<ChatMessage>.asAiContents(): List<Content> {
    // TODO: ChatMessage currently supports only texts.
    return this.map {
        content(it.role) {
            text(it.content)
        }
    }
}

fun ChatMessage.asAiContent(): Content {
    // TODO: ChatMessage currently supports only texts.
    return content(this@asAiContent.role) {
        text(this@asAiContent.content)
    }
}

fun Content.asChatMessage(previousMessage: ChatMessage?): ChatMessage? {
    return if (this.role != null && this.parts.isNotEmpty()) {
        ChatMessage(
            id = (previousMessage?.id ?: 0) + 1,
            role = this.role!!,
            content = this.parts[0].asTextOrNull() ?: return null
        )
    } else {
        null
    }
}

@SuppressLint("DefaultLocale")
fun Long.epochToHoursMinutes(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val minutes = calendar.get(Calendar.MINUTE)
    return String.format("%02d:%02d", hours, minutes)
}

@SuppressLint("DefaultLocale")
fun Long.epochToMonthDay(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val dayId = calendar.get(Calendar.DAY_OF_WEEK)
    val jalali = gregorian_to_jalali(year, month + 1, day)

    return if (Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year == year) {
        String.format("%s، %d %s", BaseApplication.Constants.JalaliDays[dayId], jalali[2], BaseApplication.Constants.JalaliMonths[jalali[1] - 1])
    } else {
        String.format("%s، %d %s %d", BaseApplication.Constants.JalaliDays[dayId], jalali[2], BaseApplication.Constants.JalaliMonths[jalali[1] - 1], jalali[0])
    }
}

@SuppressLint("DefaultLocale")
fun Long.epochToFullDateTime(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val dayId = calendar.get(Calendar.DAY_OF_WEEK)
    val jalali = gregorian_to_jalali(year, month + 1, day)

    return String.format("%s، %d/%s/%d، %d:%d", BaseApplication.Constants.JalaliDays[dayId], jalali[2], BaseApplication.Constants.JalaliMonths[jalali[1] - 1], jalali[0], hour, minute)
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

fun isNetworkAvailable(context: Context): Boolean {
    val result: Boolean
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    result = when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
    return result
}

fun Int.toDurationMinuteSecond(): String {
    val minutes = this / 60_000
    val seconds = (this % 60_000) / 1000
    return when {
        minutes in 0..9 && seconds in 0..9 -> "0$minutes:0$seconds"
        minutes in 0..9 -> "0$minutes:$seconds"
        seconds in 0..9 -> "$minutes:0$seconds"
        else -> "$minutes:$seconds"
    }
}

fun File.getMimeType(): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(this.absolutePath)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}

package ir.saltech.myapps.stutter.util

import com.aallam.openai.api.chat.ChatChunk
import com.google.gson.Gson
import gregorian_to_jalali
import ir.saltech.myapps.stutter.dto.model.DailyReport
import ir.saltech.myapps.stutter.dto.model.DailyReports
import ir.saltech.myapps.stutter.dto.model.WeeklyReport
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
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
            it.practiceDays plusNull it.desensitizationCount plusNull it.voicesProperties.challengesCount plusNull it.voicesProperties.sumOfConferencesDuration plusNull it.voicesProperties.conferenceDaysCount plusNull it.dailyReportsCount plusNull it.creationOfExceptionCount plusNull it.callsCount.groupCallsCount plusNull it.callsCount.teenSupportCallsCount plusNull it.callsCount.adultSupportCallsCount
        sumOfAllActivities
    })
}

fun getGreetingBasedOnTime(): String {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)

    return when (currentHour) {
        in 4..<11 -> "سلام! 👋\n صبح قشنگت بخیر! 😇"
        in 11..12 -> "سلام! 👋\n نزدیک ظهره، روزت بخیر! 🌞"
        in 12..13 -> "سلام! 👋\n ظهر بخیر، گرسنه‌ت نیست؟ 😋"
        in 13..<15 -> "سلام! 👋\n بعد از ظهرت بخیر، امیدوارم عالی بگذره! 😊"
        in 15..<17 -> "سلام! 👋\n عصر بخیر، خسته نباشی! 😊"
        in 17..<20 -> "سلام! 👋\n غروب زیبای امروز چطور بود؟ 🌇"
        in 20..23 -> "سلام! 👋\n شب بخیر، خواب‌های خوب ببینی! 🌙"
        else -> "سلام! 👋\n به دنیای بیدارها خوش اومدی! 🦉" // for late night/early morning
    }
}

fun getCommandBasedOnTime(): String {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)

    return when (currentHour) {
        in 4..<11 -> "الان صبحه؛"
        in 11..12 -> "الان نزدیک ظهره؛"
        in 12..13 -> "الان ظهره؛"
        in 13..<15 -> "الان بعد از ظهره؛"
        in 15..<17 -> "الان عصره؛"
        in 17..<20 -> "الان ابتدای شبه؛"
        in 20..23 -> "الان آخر شبه؛"
        else -> "الان نیمه شبه؛" // for late night/early morning
    }
}



package ir.saltech.sokhanyar.util

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import gregorian_to_jalali
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.data.local.entities.User
import ir.saltech.sokhanyar.data.local.entities.UserRoleProperties
import ir.saltech.sokhanyar.data.local.entities.treatment.DailyReport
import ir.saltech.sokhanyar.data.local.entities.treatment.DailyReports
import ir.saltech.sokhanyar.data.local.entities.treatment.WeeklyReport
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.Json
import java.io.File
import java.text.NumberFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


//@Override
//fun MutableList<ChatChunk>.response(): String =
//    this.joinToString("") { it.delta?.content ?: "" }.trim()
// TODO: Use the ktor client to gather sse response for streaming messages

inline fun <reified T> fromJson(json: String?): T? {
	return Json.decodeFromString<T>(json ?: return null)
}

inline fun <reified T> toJson(t: T?): String? {
	return Json.encodeToString(t ?: return null)
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

fun Int.asJalaliMonth(withEmoji: Boolean = false): String {
	val jalaliMonth = BaseApplication.Constants.JalaliMonths[this - 1]
	val selectedEmoji = BaseApplication.Constants.JalaliMonthsWithEmojis[jalaliMonth]?.random()
	return if (withEmoji) "$jalaliMonth $selectedEmoji" else jalaliMonth
}

fun Int.asJalaliDay(): String {
	return BaseApplication.Constants.JalaliDays[this - 1]
}

fun IntArray.toReportDate(): String {
	return "${this[3].asJalaliDay()}، ${this[2]} ${this[1].asJalaliMonth(true)} "
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
			it.practiceDays plusNull it.desensitizationCount plusNull it.voicesProperties.challengesCount plusNull it.voicesProperties.sumOfChallengesDuration plusNull it.voicesProperties.sumOfConferencesDuration plusNull it.voicesProperties.conferenceDaysCount plusNull it.dailyReportsCount plusNull it.creationOfExceptionCount plusNull it.callsCount.groupCallsCount plusNull it.callsCount.peerCallsCount
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
fun checkScreenIsMinimal(): Boolean {
	return LocalConfiguration.current.screenHeightDp.dp < BaseApplication.Constants.RESPONSIVE_MIN_HEIGHT.dp
}

@Composable
fun Modifier.wrapToScreen(reverse: Boolean = false): Modifier {
	return this.let {
		if (checkScreenIsMinimal()) {
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
//
//fun MutableList<ChatMessage>.asAiContents(): List<Content> {
//    // TODO: ChatMessage currently supports only texts.
//    return this.map {
//        content(it.role) {
//            text(it.content)
//        }
//    }
//}
//
//fun ChatMessage.asAiContent(): Content {
//    // TODO: ChatMessage currently supports only texts.
//    return content(this@asAiContent.role) {
//        text(this@asAiContent.content)
//    }
//}
//
//fun Content.asChatMessage(previousMessage: ChatMessage?): ChatMessage? {
//    return if (this.role != null && this.parts.isNotEmpty()) {
//        ChatMessage(
//            id = (previousMessage?.id ?: 0) + 1,
//            role = this.role!!,
//            content = this.parts[0].asTextOrNull() ?: return null
//        )
//    } else {
//        null
//    }
//}

fun Long.epochToMinutesSeconds(): String {
	val minutes = TimeUnit.SECONDS.toMinutes(this) % 60
	val seconds = TimeUnit.SECONDS.toSeconds(this) % 60

	return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
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
		String.format(
			"%s، %d %s",
			BaseApplication.Constants.JalaliDays[dayId - 1],
			jalali[2],
			BaseApplication.Constants.JalaliMonths[jalali[1] - 1]
		)
	} else {
		String.format(
			"%s، %d %s %d",
			BaseApplication.Constants.JalaliDays[dayId - 1],
			jalali[2],
			BaseApplication.Constants.JalaliMonths[jalali[1] - 1],
			jalali[0]
		)
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

	return String.format(
		"%s، %d/%s/%d، %d:%d",
		BaseApplication.Constants.JalaliDays[dayId - 1],
		jalali[2],
		BaseApplication.Constants.JalaliMonths[jalali[1] - 1],
		jalali[0],
		hour,
		minute
	)
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

fun isNetworkAvailable(context: Context): Boolean {
	val result: Boolean
	val connectivityManager =
		context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

fun User.getSummary(): String {
	val props = this.roleProperties as UserRoleProperties.Patient
	return """
        اطلاعات من در مورد خودم، لکنت و گفتارم که باید بدانی:
        ${if (this.displayName != null) "اسم من ${this.displayName} است." else ""}
        ${if (this.age != null) "من ${this.age} سال سن دارم." else ""}
        ${if (props.yearOfStartStuttering != null) "من از ${props.yearOfStartStuttering} سالگی دچار لکنت شدم." else ""}
        ${if (props.timesOfTherapy != null) "من ${props.timesOfTherapy} بار در طول این سالها برای درمانم تلاش کردم." else ""}
        ${if (!props.stutteringType.isNullOrBlank()) "نوع لکنت من ${props.stutteringType} است." else ""}
        ${if (props.previousStutteringSeverity != null) "درجه شدت لکنت من قبل از درمان ${props.previousStutteringSeverity} بود." else ""}
        ${if (props.currentStutteringSeverity != null) "درجه شدت لکنت من در حین درمان ${props.currentStutteringSeverity} شده." else ""}
        ${if (!props.dailyTherapyTime.isNullOrBlank()) "من روزانه ${props.dailyTherapyTime} برای درمان وقت می‌گذارم." else ""}
        ${if (props.currentTherapyDuration != null) "دوره درمان فعلی من ${props.currentTherapyDuration} ماه طول کشیده است." else ""}
        ${if (!props.treatmentStatus.isNullOrBlank()) "وضعیت فعلی درمان من: ${props.treatmentStatus}." else ""}
        ${if (!props.therapyMethod.isNullOrBlank()) "شیوه درمانی فعلی من ${props.therapyMethod} است." else ""}
        ${if (!props.stutteringSituations.isNullOrBlank()) "من در موقعیت‌های ${props.stutteringSituations} بیشتر لکنت می‌کنم." else ""}
        ${if (!props.emotionalImpact.isNullOrBlank()) "لکنت بر احساسات من این تأثیر را دارد: ${props.emotionalImpact}." else ""}
        ${if (!props.therapyGoals.isNullOrBlank()) "هدف من از درمان: ${props.therapyGoals}." else ""}
        ${if (!props.previousTherapies.isNullOrBlank()) "روش‌های درمانی قبلی که استفاده کرده‌ام: ${props.previousTherapies}." else ""}
        ${if (!props.familyHistory.isNullOrBlank()) "سابقه خانوادگی لکنت من: ${props.familyHistory}." else ""}
        ${if (!props.coOccurringConditions.isNullOrBlank()) "مشکلات گفتاری دیگر من: ${props.coOccurringConditions}." else ""}
        ${if (!props.supportSystems.isNullOrBlank()) "حمایت خانواده و دوستان من: ${props.supportSystems}." else ""}
        ${if (!props.escapingFromSpeechSituationsLevel.isNullOrBlank()) "میزان اجتناب من از موقعیت‌های گفتاری: ${props.escapingFromSpeechSituationsLevel}." else ""}
        ${if (!props.escapingFromSpeechSituationsLevel.isNullOrBlank()) "میزان اجتناب از کلمه (تغییر دادن کلمه ای که حس میکنم قراره لکنت کنم): ${props.escapingFromStutteredWordLevel}." else ""}
    """.trimIndent()
}

// TODO: Remove the validation and all of the patient fields; all of these questions must be interactive and summarized
fun validateUserInputs(patient: User): Boolean {
	// First, check if any required field (except name and age) is blank.
	val props = patient.roleProperties as UserRoleProperties.Patient
	val requiredFields = listOf(
		props.yearOfStartStuttering,
		props.timesOfTherapy,
		props.stutteringType,
		props.currentStutteringSeverity,
		props.previousStutteringSeverity,
		props.dailyTherapyTime,
		props.currentTherapyDuration,
		props.treatmentStatus,
		props.therapyMethod,
		props.stutteringSituations,
		props.emotionalImpact,
		props.therapyGoals,
		props.previousTherapies,
		props.familyHistory,
		props.supportSystems,
		props.escapingFromSpeechSituationsLevel,
		props.escapingFromStutteredWordLevel
	)

	// Check if any required field is blank (other than name and age)
	if (requiredFields.count { it != null } > 0) {
		if (requiredFields.any { it == null }) {
			return false // One or more required fields are missing
		}
	}

	// If all fields are filled, return true
	return !patient.displayName.isNullOrEmpty() && patient.age != null
}

fun String.analyzeError(): String {
	return when {
		this.contains("Connection failed") -> "اتصال به اینترنت ممکن نیست. لطفا اتصال خود را بررسی کنید و دوباره تلاش کنید."
		this.contains("otp code was sent recently") -> "کد قبلاً ارسال شده است."
		this.contains("Failed to send OTP code") -> "ارسال کد otp ناموفق بود"
		this.contains("invalid otp code") -> "کد otp وارد شده اشتباه است"
		this.contains("invalid phone number") -> "شماره تلفن وارد شده اشتباه است"
		else -> "خطای ناشناخته در سیستم"
	}
}

fun Long?.showingPrice(): String {
	if (this == null) return ""
	return NumberFormat.getNumberInstance(Locale.US).format(this)
}

fun String?.toPrice(): Long? {
	return this.let {
		if (it.isNullOrEmpty()) return null
		if (it.contains(",")) {
			it.replace(",", "")
		} else {
			it
		}
	}.toLongOrNull()
}


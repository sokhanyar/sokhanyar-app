package ir.saltech.sokhanyar.model.data.treatment.report

import ir.saltech.sokhanyar.model.data.general.Patient
import ir.saltech.sokhanyar.model.data.general.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class Report {
	abstract val patient: User     // Maybe the patient user's value should not be optional (nullable)
	abstract val date: Long?
	abstract val description: String?
	abstract val result: String?
	abstract val advice: String?
}

/////////////////////////////

@Serializable
data class DailyReport(
	override val patient: User,
	override val date: Long? = null,
	@SerialName("practice_time") val practiceTime: Int? = null,
	@SerialName("treat_method_usage") val treatMethodUsage: TreatMethodUsage = TreatMethodUsage(),
	@SerialName("desensitization_count") val desensitizationCount: Int? = null,
	@SerialName("intentional_stuttering_count") val intentionalStutteringCount: Int? = null,
	@SerialName("avoidance_detection_count") val avoidanceDetectionCount: Int? = null,
	@SerialName("calls_count") val callsCount: CallsCount = CallsCount(),
	@SerialName("voices_properties") val voicesProperties: VoicesProperties = VoicesProperties(),
	@SerialName("stutter_severity_rating") val stutterSeverityRating: Int? = null,
	@SerialName("self_satisfaction") val selfSatisfaction: Int? = null,
	override val description: String? = null,
	override val result: String? = null,
	override val advice: String? = null
) : Report()

@Serializable
data class DailyReports(
	@SerialName("daily_reports") val list: MutableList<DailyReport> = mutableListOf()
)

///////////////////////////////////////

@Serializable
data class WeeklyReport(
	override val patient: User,
	override val date: Long? = null,
	@SerialName("practice_days")
	val practiceDays: Int? = null,
	@SerialName("voices_properties")
	val voicesProperties: VoicesProperties = VoicesProperties(),
	@SerialName("calls_count")
	val callsCount: CallsCount = CallsCount(),
	@SerialName("desensitization_count")
	val desensitizationCount: Int? = null,
	@SerialName("creation_of_exception_count")
	val creationOfExceptionCount: Int? = null,
	@SerialName("daily_reports_count")
	val dailyReportsCount: Int? = null,
	@SerialName("sum_of_activities")
	val sumOfActivities: Int? = null,
	override val description: String? = null,
	override val result: String? = null,
	override val advice: String? = null
) : Report()

@Serializable
data class WeeklyReports(
	@SerialName("weekly_reports")
	val list: MutableList<WeeklyReport> = mutableListOf()
)

package ir.saltech.sokhanyar.data.local.entity.treatment

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ir.saltech.sokhanyar.data.local.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class Report {
	abstract val patientId: String     // Maybe the patient user's value should not be optional (nullable)
	abstract val date: Long?
	abstract val description: String?
	abstract val result: String?
	abstract val advice: String?
}

/////////////////////////////

@Entity(
	foreignKeys = [ForeignKey(
		User::class,
		parentColumns = ["id"],
		childColumns = ["patientId"]
	)], indices = [Index("id"), Index("patientId")]
)
@Serializable
data class DailyReport(
	@PrimaryKey(autoGenerate = true)
	val id: Long,
	override val patientId: String,
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
	override val advice: String? = null,
) : Report()

@Serializable
data class DailyReports(
	@SerialName("daily_reports") val list: MutableList<DailyReport> = mutableListOf(),
)

///////////////////////////////////////

@Entity(
	foreignKeys = [ForeignKey(
		User::class,
		parentColumns = ["uid"],
		childColumns = ["patientId"]
	)], indices = [Index("id"), Index("patientId")]
)
@Serializable
data class WeeklyReport(
	@PrimaryKey(autoGenerate = true)
	val id: Long,
	override val patientId: String,
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
	override val advice: String? = null,
) : Report()

@Serializable
data class WeeklyReports(
	@SerialName("weekly_reports")
	val list: MutableList<WeeklyReport> = mutableListOf(),
)

//////////////////////////////////

@Serializable
data class TreatMethodUsage(
	@SerialName("at_home")
	val atHome: Int? = null,
	@SerialName("at_school")
	val atSchool: Int? = null,
	@SerialName("with_others")
	val withOthers: Int? = null,
	@SerialName("with_family")
	val withFamily: Int? = null,
)

@Serializable
data class VoicesProperties(
	@SerialName("challenges_count")
	val challengesCount: Int? = null,
	@SerialName("sum_of_challenges_duration")
	val sumOfChallengesDuration: Int? = null,
	@SerialName("conferences_days_count")
	val conferenceDaysCount: Int? = null,
	@SerialName("sum_of_conferences_duration")
	val sumOfConferencesDuration: Int? = null,

	)

@Serializable
data class CallsCount(
	@SerialName("group_calls_count")
	val groupCallsCount: Int? = null,
	@SerialName("peer_calls_count")
	val peerCallsCount: Int? = null,
)


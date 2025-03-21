package ir.saltech.sokhanyar.data.local.entity.treatment

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ir.saltech.sokhanyar.data.local.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class Report {
	@SerialName("report_id") abstract val id: Long
	@SerialName("patient_id") abstract val patientId: String     // May be the `SerialName` should be defined for each report classes
	abstract val date: Long?
	abstract val description: String?
	abstract var textualResult: String?
}

/////////////////////////////

@Entity(
	tableName = "daily_reports",
	foreignKeys = [ForeignKey(
		User::class, parentColumns = ["id"], childColumns = ["patientId"]
	)], indices = [Index("patientId")]
)
@Serializable
data class DailyReport(
	@PrimaryKey(autoGenerate = true) override val id: Long,
	override val patientId: String,
	override val date: Long? = null,
	@SerialName("practice_time") val practiceTime: Int? = null,
	@Embedded @SerialName("treat_method_usage") val treatMethodUsage: TreatMethodUsage = TreatMethodUsage(),
	@SerialName("desensitization_count") val desensitizationCount: Int? = null,
	@SerialName("intentional_stuttering_count") val intentionalStutteringCount: Int? = null,
	@SerialName("avoidance_detection_count") val avoidanceDetectionCount: Int? = null,
	@Embedded @SerialName("calls_count") val callsCount: CallsCount = CallsCount(),
	@Embedded @SerialName("voices_properties") val voicesProperties: VoicesProperties = VoicesProperties(),
	@SerialName("stutter_severity_rating") val stutterSeverityRating: Int? = null,
	@SerialName("self_satisfaction") val selfSatisfaction: Int? = null,
	override val description: String? = null,
	override var textualResult: String? = null,
) : Report()

///////////////////////////////////////

@Entity(
	tableName = "weekly_reports",
	foreignKeys = [ForeignKey(
		User::class, parentColumns = ["id"], childColumns = ["patientId"]
	)], indices = [Index("patientId")]
)
@Serializable
data class WeeklyReport(
	@PrimaryKey(autoGenerate = true) override val id: Long,
	override val patientId: String,
	override val date: Long? = null,
	@SerialName("practice_days") val practiceDays: Int? = null,
	@Embedded @SerialName("voices_properties") val voicesProperties: VoicesProperties = VoicesProperties(),
	@Embedded @SerialName("calls_count") val callsCount: CallsCount = CallsCount(),
	@SerialName("desensitization_count") val desensitizationCount: Int? = null,
	@SerialName("creation_of_exception_count") val creationOfExceptionCount: Int? = null,
	@SerialName("daily_reports_count") val dailyReportsCount: Int? = null,
	@SerialName("sum_of_activities") val sumOfActivities: Int? = null,
	override val description: String? = null,
	override var textualResult: String? = null,
) : Report()

//////////////////////////////////

@Serializable
data class TreatMethodUsage(
	@SerialName("at_home") val atHome: Int? = null,
	@SerialName("at_school") val atSchool: Int? = null,
	@SerialName("with_others") val withOthers: Int? = null,
	@SerialName("with_family") val withFamily: Int? = null
)

@Serializable
data class VoicesProperties(
	@SerialName("challenges_count") val challengesCount: Int? = null,
	@SerialName("sum_of_challenges_duration") val sumOfChallengesDuration: Int? = null,
	@SerialName("conferences_days_count") val conferenceDaysCount: Int? = null,
	@SerialName("sum_of_conferences_duration") val sumOfConferencesDuration: Int? = null
)

@Serializable
data class CallsCount(
	@SerialName("group_calls_count") val groupCallsCount: Int? = null,
	@SerialName("peer_calls_count") val peerCallsCount: Int? = null
)


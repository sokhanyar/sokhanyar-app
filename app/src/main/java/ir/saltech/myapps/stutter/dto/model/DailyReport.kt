package ir.saltech.myapps.stutter.dto.model

import com.google.gson.annotations.SerializedName
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer

data class DailyReport(
    val name: String? = null,
    val date: Long? = null,
    @SerializedName("practice_time")
    val practiceTime: Int? = null,
    @SerializedName("methodUsage")
    val methodUsage: MethodUsage = MethodUsage(),
    @SerializedName("desensitization_count")
    val desensitizationCount: Int? = null,
    @SerializedName("intentional_stuttering_count")
    val intentionalStutteringCount: Int? = null,
    @SerializedName("avoidance_detection_count")
    val avoidanceDetectionCount: Int? = null,
    @SerializedName("calls_count")
    val callsCount: CallsCount = CallsCount(),
    @SerializedName("voices_properties")
    val voicesProperties: VoicesProperties = VoicesProperties(),
    @SerializedName("self_satisfaction")
    val selfSatisfaction: Int? = null,
    val description: String? = null
)

data class DailyReports(
    @SerializedName("daily_reports")
    val list: MutableList<DailyReport> = mutableListOf()
)


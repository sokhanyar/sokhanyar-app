package ir.saltech.myapps.stutter.dto.model

import com.google.gson.annotations.SerializedName

abstract class Report {
    abstract val name: String?
    abstract val date: Long?
    abstract val description: String?
    abstract val result: String?
}

data class DailyReport(
    override val name: String? = null,
    override val date: Long? = null,
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
    override val description: String? = null,
    override val result: String? = null
) : Report()

data class DailyReports(
    @SerializedName("daily_reports")
    val list: MutableList<DailyReport> = mutableListOf()
)


package ir.saltech.myapps.stutter.dto.model

import com.google.gson.annotations.SerializedName

data class WeeklyReport(
    override val name: String? = null,
    override val date: Long? = null,
    @SerializedName("practice_days")
    val practiceDays: Int? = null,
    @SerializedName("voices_properties")
    val voicesProperties: VoicesProperties = VoicesProperties(),
    @SerializedName("calls_count")
    val callsCount: CallsCount = CallsCount(),
    @SerializedName("desensitization_count")
    val desensitizationCount: Int? = null,
    @SerializedName("creation_of_exception_count")
    val creationOfExceptionCount: Int? = null,
    @SerializedName("daily_reports_count")
    val dailyReportsCount: Int? = null,
    @SerializedName("sum_of_activities")
    val sumOfActivities: Int? = null,
    override val description: String? = null,
    override val result: String? = null,
) : Report()

data class WeeklyReports(
    @SerializedName("weekly_reports")
    val list: MutableList<WeeklyReport> = mutableListOf()
)

package ir.saltech.sokhanyar.dto.model.data.reports

import com.google.gson.annotations.SerializedName
import ir.saltech.sokhanyar.dto.model.data.general.User

abstract class Report {
    abstract val user: User
    abstract val date: Long?
    abstract val description: String?
    abstract val result: String?
}

data class DailyReport(
    override val user: User = User(),
    override val date: Long? = null,
    @SerializedName("practice_time")
    val practiceTime: Int? = null,
    @SerializedName("methodUsage")
    val methodUsage: _root_ide_package_.ir.saltech.sokhanyar.dto.model.data.reports.MethodUsage = _root_ide_package_.ir.saltech.sokhanyar.dto.model.data.reports.MethodUsage(),
    @SerializedName("desensitization_count")
    val desensitizationCount: Int? = null,
    @SerializedName("intentional_stuttering_count")
    val intentionalStutteringCount: Int? = null,
    @SerializedName("avoidance_detection_count")
    val avoidanceDetectionCount: Int? = null,
    @SerializedName("calls_count")
    val callsCount: _root_ide_package_.ir.saltech.sokhanyar.dto.model.data.reports.CallsCount = _root_ide_package_.ir.saltech.sokhanyar.dto.model.data.reports.CallsCount(),
    @SerializedName("voices_properties")
    val voicesProperties: _root_ide_package_.ir.saltech.sokhanyar.dto.model.data.reports.VoicesProperties = _root_ide_package_.ir.saltech.sokhanyar.dto.model.data.reports.VoicesProperties(),
    @SerializedName("self_satisfaction")
    val selfSatisfaction: Int? = null,
    override val description: String? = null,
    override val result: String? = null
) : _root_ide_package_.ir.saltech.sokhanyar.dto.model.data.reports.Report()

data class DailyReports(
    @SerializedName("daily_reports")
    val list: MutableList<_root_ide_package_.ir.saltech.sokhanyar.dto.model.data.reports.DailyReport> = mutableListOf()
)


package ir.saltech.sokhanyar.api.request

import ir.saltech.sokhanyar.model.data.treatment.report.Report
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceRequest(
	@SerialName("clinic_id")
	val clinicId: String,
	@SerialName("phone_number")
	val phoneNumber: String,
	@SerialName("user_role")
	val userRole: String
)

@Serializable
data class OtpRequest(
	@SerialName("device_id")
	val deviceId: String
)

@Serializable
data class AccessTokenRequest(
	@SerialName("device_id")
	val deviceId: String,
	@SerialName("otp_code")
	val otpCode: Int
)

@Serializable
data class RenewAccessTokenRequest(
	@SerialName("device_id")
	val deviceId: String,
	@SerialName("refresh_token")
	val refreshToken: String,
)

@Serializable
data class GetVoiceMediaIdRequest(
	val checksum: String
)

@Serializable
data class AnalyzeVoiceRequest(
	@SerialName("model_name")
	val modelName: String? = null,
	val prompt: String? = null,
	val stream: Boolean = false
)

@Serializable
data class AnalyzeReportRequest(
	@SerialName("current_report")
	val currentReport: Report,
	@SerialName("last_reports")
	val lastReports: List<Report>,
	@SerialName("model_name")
	val modelName: String? = null,
	val prompt: String? = null,
	val stream: Boolean = false,
)

@Serializable
data class GenerateTextAiRequest(
	@SerialName("model_name")
	val modelName: String? = null,
	val prompt: String,
	val stream: Boolean = false
)

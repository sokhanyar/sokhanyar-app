package ir.saltech.sokhanyar.data.remote.response

import ir.saltech.sokhanyar.data.remote.entity.AiModel
import ir.saltech.sokhanyar.data.local.entity.Clinic
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ClinicsInfoResponse(
	val clinics: List<Clinic>,
)

@Serializable
data class RegisterDeviceResponse(
	@SerialName("device_id")
	val deviceId: String,
	@SerialName("user_id")
	val userId: String,
	val timestamp: Long,
)

@Serializable
data class AccessTokenResponse(
	@SerialName("access_token")
	val accessToken: String,
	@SerialName("refresh_token")
	val refreshToken: String,
	@SerialName("token_type")
	val tokenType: String,
	val timestamp: Long,
)

@Serializable
data class UploadMediaResponse(
	@SerialName("media_id")
	val mediaId: String,
	val timestamp: Long,
)

@Serializable
data class GetAiModelsResponse(
	@SerialName("available_models")
	val models: Map<String, List<AiModel>>
)

@Serializable
data class AnalyzeVoiceResponse(
	val advice: String,
	val transcription: String,
)

@Serializable
data class AnalyzeReportResponse(
	val advice: String,
)

@Serializable
data class GenerateMotivationTextResponse(
	@SerialName("text")
	val motivationText: String,
)

//@Serializable
//data class GenerateTextAiResponse(
////    val automaticFunctionCallingHistory: List<Any>,
//	val candidates: List<Candidate>,
//	val modelVersion: String,
//	val usageMetadata: UsageMetadata,
//)


//////////////// Basic Responses ////////////////

@Serializable
data class DefaultResponse(
	val message: String,
	val timestamp: Long = Clock.System.now().epochSeconds,
)

@Serializable
data class ErrorResponse (
	val detail: DefaultResponse
)
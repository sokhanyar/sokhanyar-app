package ir.saltech.sokhanyar.api.response

import ir.saltech.sokhanyar.model.data.ai.AvailableModels
import ir.saltech.sokhanyar.model.data.general.Clinic
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ClinicsInfoResponse(
	val clinics: List<Clinic>
)

data class RegisterDeviceResponse(
	@SerialName("device_id")
	val deviceId: String,
	@SerialName("user_id")
	val userId: String,
	val timestamp: Long
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
	val availableModels: AvailableModels,
	val status: String
)

@Serializable
data class AnalyzeVoiceResponse(
	val advice: String,
	val transcription: String
)

@Serializable
data class AnalyzeReportResponse(
	val advice: String
)

@Serializable
data class GenerateMotivationTextResponse(
	@SerialName("text")
	val motivationText: String
)

//@Serializable
//data class GenerateTextAiResponse(
////    val automaticFunctionCallingHistory: List<Any>,
//	val candidates: List<Candidate>,
//	val modelVersion: String,
//	val usageMetadata: UsageMetadata,
//)


//////////////// Basic Responses ////////////////

data class MessageResponse(
	val status: String,
	val message: String,
	val timestamp: Long = Clock.System.now().epochSeconds
)

data class ErrorResponse(
	val detail: MessageResponse,
)


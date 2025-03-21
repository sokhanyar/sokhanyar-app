package ir.saltech.sokhanyar.data.remote.api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.data.remote.request.AccessTokenRequest
import ir.saltech.sokhanyar.data.remote.request.AnalyzeReportRequest
import ir.saltech.sokhanyar.data.remote.request.AnalyzeVoiceRequest
import ir.saltech.sokhanyar.data.remote.request.GenerateMotivationTextRequest
import ir.saltech.sokhanyar.data.remote.request.GetVoiceMediaIdRequest
import ir.saltech.sokhanyar.data.remote.request.OtpCodeRequest
import ir.saltech.sokhanyar.data.remote.request.RegisterDeviceRequest
import ir.saltech.sokhanyar.data.remote.request.RenewAccessTokenRequest
import ir.saltech.sokhanyar.data.remote.response.AccessTokenResponse
import ir.saltech.sokhanyar.data.remote.response.AnalyzeReportResponse
import ir.saltech.sokhanyar.data.remote.response.AnalyzeVoiceResponse
import ir.saltech.sokhanyar.data.remote.response.ClinicsInfoResponse
import ir.saltech.sokhanyar.data.remote.response.DefaultResponse
import ir.saltech.sokhanyar.data.remote.response.GenerateMotivationTextResponse
import ir.saltech.sokhanyar.data.remote.response.GetAiModelsResponse
import ir.saltech.sokhanyar.data.remote.response.RegisterDeviceResponse
import ir.saltech.sokhanyar.data.remote.response.UploadMediaResponse
import kotlinx.coroutines.flow.Flow

class SokhanYarApi(engine: HttpClientEngine = Android.create()) {
	private val baseUrl = BaseApplication.BaseUrl.SokhanYar
	private val oneShotClient = ApiClient.OneShot(baseUrl, engine)
	private val sseClient = ApiClient.ServerSideEvents(baseUrl, engine)

	suspend fun getClinicsInfo(): ClinicsInfoResponse =
		oneShotClient.get(endpoint = "department/clinic/get")

	suspend fun registerDevice(request: RegisterDeviceRequest): RegisterDeviceResponse =
		oneShotClient.post(endpoint = "auth/device/register", body = request)

	suspend fun requestOtpCodeAuth(request: OtpCodeRequest): DefaultResponse =
		oneShotClient.post(endpoint = "auth/otp/request", body = request)

	suspend fun requestAccessTokenAuth(request: AccessTokenRequest): AccessTokenResponse =
		oneShotClient.post(endpoint = "auth/access/request", body = request)

	suspend fun renewAccessTokenAuth(request: RenewAccessTokenRequest): AccessTokenResponse =
		oneShotClient.put(endpoint = "auth/access/renew", body = request)

	suspend fun uploadTreatmentVoice(
		accessToken: String,
		fileByteArray: ByteArray,
		fileName: String,
		fileContentType: String,
		progressListener: (Long, Long?) -> Unit,
	): UploadMediaResponse = oneShotClient.uploadFile<UploadMediaResponse>(
		endpoint = "treatment/voice/upload",
		authToken = accessToken,
		fileByteArray = fileByteArray,
		fileName = fileName,
		fileContentType = fileContentType,
		progressListener = progressListener
	).getOrThrow()

	suspend fun getTreatmentVoiceMediaIdByChecksum(
		accessToken: String,
		request: GetVoiceMediaIdRequest,
	): UploadMediaResponse = oneShotClient.get(
		endpoint = "treatment/voice/getId", body = request, authToken = accessToken
	)

	/////////////// AI Based Endpoints ////////////////

	// TODO: For each SSE functions (because may be has not streamed) create two functions .. 1. For OneShot 2. For SSE

	suspend fun analyzeTreatmentVoiceMediaId(
		accessToken: String,
		mediaId: String,
		request: AnalyzeVoiceRequest,
	): AnalyzeVoiceResponse = oneShotClient.post(
		endpoint = "treatment/voice/$mediaId/analyze/generate",
		body = request,
		authToken = accessToken
	)

	fun analyzeTreatmentVoiceMediaIdStream(
		accessToken: String,
		mediaId: String,
		request: AnalyzeVoiceRequest,
	): Flow<AnalyzeVoiceResponse> = sseClient.post(
		endpoint = "treatment/voice/$mediaId/analyze/generateStream",
		body = request,
		authToken = accessToken
	)

	suspend fun analyzeTreatmentVoiceMediaFile(
		accessToken: String,
		fileByteArray: ByteArray,
		fileName: String,
		fileContentType: String,
		progressListener: (Long, Long?) -> Unit,
	): AnalyzeVoiceResponse = oneShotClient.uploadFile<AnalyzeVoiceResponse>(
		endpoint = "treatment/voice/analyze/generate",
		authToken = accessToken,
		fileByteArray = fileByteArray,
		fileName = fileName,
		fileContentType = fileContentType,
		progressListener = progressListener
	).getOrThrow()

	fun analyzeTreatmentVoiceMediaFileStream(
		accessToken: String,
		fileByteArray: ByteArray,
		fileName: String,
		progressListener: (Long, Long?) -> Unit,
	): Flow<AnalyzeVoiceResponse> = sseClient.uploadFile<AnalyzeVoiceResponse>(
		endpoint = "treatment/voice/analyze/generateStream",
		authToken = accessToken,
		fileByteArray = fileByteArray,
		fileName = fileName,
		progressListener = progressListener
	)

	suspend fun analyzeTreatmentReport(
		accessToken: String,
		reportType: String,
		request: AnalyzeReportRequest,
	): AnalyzeReportResponse = oneShotClient.post(
		endpoint = "treatment/report/$reportType/analyze/generate",
		body = request,
		authToken = accessToken
	)

	fun analyzeTreatmentReportStream(
		accessToken: String,
		reportType: String,
		request: AnalyzeReportRequest,
	): Flow<AnalyzeReportResponse> = sseClient.post(
		endpoint = "treatment/report/$reportType/analyze/generateStream",
		body = request,
		authToken = accessToken
	)

	suspend fun generateMotivationText(
		accessToken: String,
		request: GenerateMotivationTextRequest,
	): GenerateMotivationTextResponse = oneShotClient.post(
		endpoint = "treatment/motivation/generate", body = request, authToken = accessToken
	)

	fun generateMotivationTextStream(
		accessToken: String,
		request: GenerateMotivationTextRequest,
	): Flow<String> = sseClient.post(
		endpoint = "treatment/motivation/generateStream", body = request, authToken = accessToken
	)

	suspend fun getModelsAi(accessToken: String): GetAiModelsResponse = oneShotClient.get(
		endpoint = "ai/models/get", authToken = accessToken
	)
}

package ir.saltech.sokhanyar.api.config

import ir.saltech.sokhanyar.api.request.AccessTokenRequest
import ir.saltech.sokhanyar.api.request.AnalyzeReportRequest
import ir.saltech.sokhanyar.api.request.AnalyzeVoiceRequest
import ir.saltech.sokhanyar.api.request.GenerateMotivationTextRequest
import ir.saltech.sokhanyar.api.request.GetVoiceMediaIdRequest
import ir.saltech.sokhanyar.api.request.OtpRequest
import ir.saltech.sokhanyar.api.request.RegisterDeviceRequest
import ir.saltech.sokhanyar.api.request.RenewAccessTokenRequest
import ir.saltech.sokhanyar.api.response.AccessTokenResponse
import ir.saltech.sokhanyar.api.response.AnalyzeReportResponse
import ir.saltech.sokhanyar.api.response.AnalyzeVoiceResponse
import ir.saltech.sokhanyar.api.response.ClinicsInfoResponse
import ir.saltech.sokhanyar.api.response.GenerateMotivationTextResponse
import ir.saltech.sokhanyar.api.response.GetAiModelsResponse
import ir.saltech.sokhanyar.api.response.MessageResponse
import ir.saltech.sokhanyar.api.response.RegisterDeviceResponse
import ir.saltech.sokhanyar.api.response.UploadMediaResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

private const val AUTHORIZATION_HEADER = "Authorization"
private const val API_VERSION = "v1"


interface SokhanyarApi {

	@GET("$API_VERSION/department/clinic/get")
	fun getClinicsInfo(): Call<ClinicsInfoResponse>

	@POST("$API_VERSION/auth/device/register")
	fun registerDevice(@Body request: RegisterDeviceRequest): Call<RegisterDeviceResponse>

	@POST("$API_VERSION/auth/otp/request")
	fun requestOtpCodeAuth(@Body request: OtpRequest): Call<MessageResponse>

	@POST("$API_VERSION/auth/access/request")
	fun requestAccessTokenAuth(@Body request: AccessTokenRequest): Call<AccessTokenResponse>

	@PATCH("$API_VERSION/auth/access/renew")
	fun renewAccessTokenAuth(@Body request: RenewAccessTokenRequest): Call<AccessTokenResponse>

	@Multipart
	@POST("$API_VERSION/treatment/voice/upload")
	fun uploadTreatmentVoice(
		@Header(AUTHORIZATION_HEADER) accessToken: String,
		@Part file: MultipartBody.Part,
	): Call<UploadMediaResponse>

	@GET("$API_VERSION/treatment/voice/getId")
	fun getTreatmentVoiceMediaIdByChecksum(
		@Header(AUTHORIZATION_HEADER) accessToken: String,
		@Body request: GetVoiceMediaIdRequest,
	): Call<UploadMediaResponse>

	@POST("$API_VERSION/treatment/voice/{mediaId}/analyze")
	fun analyzeTreatmentVoiceMediaId(
		@Header(AUTHORIZATION_HEADER) accessToken: String,
		@Path("mediaId") mediaId: String,
		@Body request: AnalyzeVoiceRequest,
	): Call<AnalyzeVoiceResponse>

	@Multipart
	@POST("$API_VERSION/treatment/voice/analyze")
	fun analyzeTreatmentVoiceMediaFile(
		@Header(AUTHORIZATION_HEADER) accessToken: String,
		@Part file: MultipartBody.Part,
	): Call<AnalyzeVoiceResponse>

	@POST("$API_VERSION/treatment/report/{reportType}")
	fun analyzeTreatmentReport(
		@Header(AUTHORIZATION_HEADER) accessToken: String,
		@Path("reportType") reportType: String,
		@Body request: AnalyzeReportRequest,
	): Call<AnalyzeReportResponse>

	@POST("$API_VERSION/treatment/motivation/generate")
	fun generateMotivationText(
		@Header(AUTHORIZATION_HEADER) accessToken: String,
		@Body request: GenerateMotivationTextRequest,
	): Call<GenerateMotivationTextResponse>

	@GET("$API_VERSION/ai/models/get")
	fun getModelsAi(@Header(AUTHORIZATION_HEADER) accessToken: String): Call<GetAiModelsResponse>

}
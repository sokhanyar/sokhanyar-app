package ir.saltech.sokhanyar.api

import ir.saltech.sokhanyar.model.api.ResponseObject
import ir.saltech.sokhanyar.model.data.general.AuthInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

private const val AUTHORIZATION_HEADER = "Authorization"

interface SokhanyarApi {
	@POST("api/v1/auth/send-otp")
	fun doSignIn(@Body authInfo: AuthInfo): Call<ResponseObject>

	@POST("api/v1/auth/verify-otp")
	fun verifyOtp(@Body authInfo: AuthInfo): Call<AuthInfo>

//	@POST("api/v1/ai/generate/text")
//	fun generateText(
//		@Header(AUTHORIZATION_HEADER) authToken: String,
//        @Body promptInfo: PromptInfo)
//	): Call<>
}
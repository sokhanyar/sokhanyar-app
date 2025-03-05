package ir.saltech.sokhanyar.model.data.general

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Device(
	@SerialName("device_id")
	val id: String? = null,
	@SerialName("refresh_token") val refreshToken: String? = null,
	@SerialName("access_token") val accessToken: String? = null,
	@SerialName("token_type") val tokenType: String? = null,
	@SerialName("otp_code") val otpCode: Int? = null,
	val otpRequestStatus: OtpRequestStatus = OtpRequestStatus.NOT_REQUESTED
)

enum class OtpRequestStatus {
	NOT_REQUESTED, REQUESTED, ERROR
}

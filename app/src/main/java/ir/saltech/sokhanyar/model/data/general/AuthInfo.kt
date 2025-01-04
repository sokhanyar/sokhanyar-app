package ir.saltech.sokhanyar.model.data.general

data class AuthInfo(
    val phoneNumber: Long? = null,
    val refreshToken: String? = null,
    val accessToken: String? = null,
    val tokenType: String? = null,
    val deviceId: String? = null,
    val otpCode: Int? = null,
    val otpRequestStatus: OtpRequestStatus = OtpRequestStatus.NOT_REQUESTED
)

enum class OtpRequestStatus {
    NOT_REQUESTED,
    REQUESTED,
    ERROR
}

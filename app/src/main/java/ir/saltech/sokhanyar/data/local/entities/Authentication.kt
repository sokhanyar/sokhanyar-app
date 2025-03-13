package ir.saltech.sokhanyar.data.local.entities

import androidx.room.Entity
import androidx.room.TypeConverters
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.data.local.dbconfig.Converters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity()
@Serializable
@TypeConverters(Converters::class)
data class Device(
	@SerialName("device_id") val id: String,
	@SerialName("refresh_token") val refreshToken: String? = null,
	@SerialName("access_token") val accessToken: String? = null,
	@SerialName("token_type") val tokenType: String? = null,
	@SerialName("otp_code") val otpCode: Int? = null,
	val otpRequestStatus: BaseApplication.OtpRequestStatus = BaseApplication.OtpRequestStatus.NOT_REQUESTED,
)
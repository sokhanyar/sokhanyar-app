package ir.saltech.sokhanyar.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import ir.saltech.sokhanyar.BaseApplication
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(foreignKeys = [ForeignKey(User::class, ["id"], ["userId"], onUpdate = CASCADE, onDelete = CASCADE)], indices = [Index("id"), Index("userId")])
@Serializable
data class Device(
	@PrimaryKey
	@SerialName("device_id") val id: String,
	@SerialName("user_id") val userId: String,
	@SerialName("refresh_token") val refreshToken: String? = null,
	@SerialName("access_token") val accessToken: String? = null,
	@SerialName("token_type") val tokenType: String? = null,
	@SerialName("otp_code") val otpCode: Int? = null,
	val otpRequestStatus: BaseApplication.OtpRequestStatus = BaseApplication.OtpRequestStatus.NOT_REQUESTED,
)
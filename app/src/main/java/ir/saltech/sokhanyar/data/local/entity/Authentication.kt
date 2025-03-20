package ir.saltech.sokhanyar.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import ir.saltech.sokhanyar.BaseApplication
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(foreignKeys = [ForeignKey(User::class, parentColumns = ["id"], childColumns = ["userId"], onUpdate = CASCADE, onDelete = CASCADE)], indices = [Index("userId")])
@Serializable
data class Device(
	@PrimaryKey
	@SerialName("device_id") val id: String,
	@SerialName("user_id") val userId: String,
	@SerialName("refresh_token") var refreshToken: String? = null,
	@SerialName("access_token") var accessToken: String? = null,
	@SerialName("token_type") var tokenType: String? = null,
	@SerialName("otp_code") var otpCode: Int? = null,
	var otpRequestStatus: BaseApplication.OtpRequestStatus = BaseApplication.OtpRequestStatus.NOT_REQUESTED,
)
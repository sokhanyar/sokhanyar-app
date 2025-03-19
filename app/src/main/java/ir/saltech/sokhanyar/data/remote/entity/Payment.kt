package ir.saltech.sokhanyar.data.remote.entity

import ir.saltech.sokhanyar.BaseApplication
import kotlinx.serialization.Serializable

@Serializable
data class DonationPayment(
	val amount: Long,
	val mobile: String,
	val referer: String = BaseApplication.Constants.SALTECH_PAY_REFERER_LINK,
	val orderId: String,
)

@Serializable
data class PaymentResult(
	val message: String,
	val status: String,
	val timestamp: Int,
	val trackId: Long,
)


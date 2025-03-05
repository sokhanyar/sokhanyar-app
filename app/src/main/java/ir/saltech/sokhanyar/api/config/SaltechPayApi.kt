package ir.saltech.sokhanyar.api.config

import ir.saltech.sokhanyar.model.api.DonationPayment
import ir.saltech.sokhanyar.model.api.PaymentResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SaltechPayApi {

	@POST("begin")
	fun startDonationPayment(
		@Body payment: DonationPayment
	): Call<PaymentResult>

}
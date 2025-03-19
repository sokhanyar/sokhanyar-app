package ir.saltech.sokhanyar.data.remote.api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.data.remote.entity.DonationPayment
import ir.saltech.sokhanyar.data.remote.entity.PaymentResult

class SalTechPayApi(engine: HttpClientEngine = Android.create()) {
	private val baseUrl = BaseApplication.BaseUrl.SalTechPay
	private val client = ApiClient.OneShot(baseUrl, engine)

	suspend fun startDonationPayment(payment: DonationPayment): PaymentResult =
		client.post(endpoint = "begin", body = payment)
}

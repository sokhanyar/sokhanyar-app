package ir.saltech.sokhanyar.model.api

data class DonationPayment(
	val amount: Long,
    val mobile: String,
    val referer: String = "https://s.saltech.ir/sokhanyar",
    val orderId: String,
)

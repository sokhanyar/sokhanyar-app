package ir.saltech.sokhanyar.model.api

data class PaymentResult(
    val message: String,
    val status: String,
    val timestamp: Int,
    val trackId: Long
)
package ir.saltech.sokhanyar.model.api

data class ErrorObject(
    val code: Int,
    val details: List<Detail>,
    val message: String,
    val status: String
)
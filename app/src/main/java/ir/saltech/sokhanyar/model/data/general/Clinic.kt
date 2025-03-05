package ir.saltech.sokhanyar.model.data.general

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Clinic(
    @SerialName("accepted_insurances")
    val acceptedInsurances: List<String>,
    val address: String,
    @SerialName("created_at")
    val createdAt: Int,
    val email: String,
    val id: String,
    val name: String,
    @SerialName("opening_days")
    val openingDays: List<String>,
    @SerialName("opening_hours")
    val openingHours: List<List<Int>>,
    @SerialName("phone_numbers")
    val phoneNumbers: List<String>,
    val website: String
)
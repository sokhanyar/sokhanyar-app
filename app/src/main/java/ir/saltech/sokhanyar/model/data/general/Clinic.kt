package ir.saltech.sokhanyar.model.data.general

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Clinic(
    @SerialName("clinic_id")
    val id: String,
    val name: String,
    val email: String,
    val address: String,
    val website: String,
    @SerialName("accepted_insurances")
    val acceptedInsurances: List<String>,
    @SerialName("created_at")
    val createdAt: Int,
    @SerialName("opening_days")
    val openingDays: List<String>,
    @SerialName("opening_hours")
    val openingHours: List<List<Int>>,
    @SerialName("phone_numbers")
    val phoneNumbers: List<String>,
)
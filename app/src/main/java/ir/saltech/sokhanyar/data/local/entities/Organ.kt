package ir.saltech.sokhanyar.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class Clinic(
	@PrimaryKey
	val id: String,
	val name: String,
	val email: String?,
	val address: String,
	val website: String?,
	@SerialName("accepted_insurances")
	val acceptedInsurances: List<String>?,
	@SerialName("opening_days")
	val openingDays: List<String>?,
	@SerialName("opening_hours")
	val openingHours: List<Array<Int>>?,
	@SerialName("phone_numbers")
	val phoneNumbers: List<String>,
	@SerialName("accept_viewers")
	val acceptViewers: Boolean,
	@SerialName("accept_consultants")
	val acceptConsultants: Boolean,
	@SerialName("created_at")
	val createdAt: Long
)
package ir.saltech.sokhanyar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ir.saltech.sokhanyar.data.local.dbconfig.Converters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
@TypeConverters(Converters::class)
data class Clinic(
	@PrimaryKey
	val id: String,
	val name: String,
	val email: String? = null,
	val address: String,
	val website: String? = null,
	@SerialName("accepted_insurances")
	val acceptedInsurances: List<String>? = null,
	@SerialName("opening_days")
	val openingDays: List<String>? = null,
	@SerialName("opening_hours")
	val openingHours: List<Array<Int>>? = null,
	@SerialName("phone_numbers")
	val phoneNumbers: List<String>,
	@SerialName("accept_viewers")
	val acceptViewers: Boolean,
	@SerialName("accept_consultants")
	val acceptConsultants: Boolean,
	@SerialName("created_at")
	val createdAt: Long
)
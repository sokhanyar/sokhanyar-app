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
	@SerialName("clinic_id")
	val id: String,
	val name: String,
	var email: String? = null,
	var address: String,
	var website: String? = null,
	@SerialName("accepted_insurances")
	var acceptedInsurances: List<String>? = null,
	@SerialName("opening_days")
	var openingDays: List<String>? = null,
	@SerialName("opening_hours")
	var openingHours: List<Array<Int>>? = null,
	@SerialName("phone_numbers")
	var phoneNumbers: List<String>,
	@SerialName("accept_viewers")
	var acceptViewers: Boolean,
	@SerialName("accept_consultants")
	var acceptConsultants: Boolean,
	@SerialName("created_at")
	val createdAt: Long
)
package ir.saltech.sokhanyar.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ir.saltech.sokhanyar.BaseApplication.Gender
import ir.saltech.sokhanyar.BaseApplication.UserRole
import ir.saltech.sokhanyar.data.local.dbconfig.Converters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity
@Serializable
@TypeConverters(Converters::class)
data class User(
	@PrimaryKey
	@SerialName("user_id")
	val uid: String,
	val age: Int? = null,
	@SerialName("clinic_id")
	val clinicId: String? = null, // the associated clinic to this user
	@SerialName("phone_number") val phoneNumber: String? = null,
	val device: Device? = null,
	@SerialName("display_name") var displayName: String? = null,
	@SerialName("national_code") val nationalCode: String? = null,
	val username: String? = null,
	val email: String? = null,
	@SerialName("birth_date") val birthDate: Long? = null,
	val gender: Gender? = null,
	val bio: String? = null,
	val role: UserRole = UserRole.Patient,
	@Embedded
	@SerialName("role_properties")
	val roleProperties: UserRoleProperties? = null
)


@Serializable
sealed class UserRoleProperties {

	@Serializable
	@SerialName("patient")
	data class Patient(
		@SerialName("year_of_start_stuttering") val yearOfStartStuttering: Int? = null,
		@SerialName("times_of_therapy") val timesOfTherapy: Int? = null,
		@SerialName("stuttering_type") val stutteringType: String? = null,
		@SerialName("previous_stuttering_severity") val previousStutteringSeverity: Int? = null,
		@SerialName("current_stuttering_severity") val currentStutteringSeverity: Int? = null,
		@SerialName("daily_therapy_time") val dailyTherapyTime: String? = null,
		@SerialName("current_therapy_duration") val currentTherapyDuration: Int? = null,
		@SerialName("treatment_status") val treatmentStatus: String? = null,
		@SerialName("therapy_method") val therapyMethod: String? = null,
		@SerialName("stuttering_situations") val stutteringSituations: String? = null,
		@SerialName("emotional_impact") val emotionalImpact: String? = null,
		@SerialName("therapy_goals") val therapyGoals: String? = null,
		@SerialName("previous_therapies") val previousTherapies: String? = null,
		@SerialName("family_history") val familyHistory: String? = null,
		@SerialName("co_occurring_conditions") val coOccurringConditions: String? = null,
		@SerialName("support_systems") val supportSystems: String? = null,
		@SerialName("escaping_from_speech_situations_level") val escapingFromSpeechSituationsLevel: String? = null,
		@SerialName("escaping_from_stuttered_word_level") val escapingFromStutteredWordLevel: String? = null,
	) : UserRoleProperties()


}

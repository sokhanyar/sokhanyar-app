package ir.saltech.sokhanyar.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ir.saltech.sokhanyar.BaseApplication.FriendshipStatus
import ir.saltech.sokhanyar.BaseApplication.Gender
import ir.saltech.sokhanyar.BaseApplication.TreatmentStatus
import ir.saltech.sokhanyar.BaseApplication.UserRole
import ir.saltech.sokhanyar.BaseApplication.UserStatus
import ir.saltech.sokhanyar.data.local.dbconfig.Converters
import ir.saltech.sokhanyar.data.local.entity.serializer.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Entity
@Serializable
@TypeConverters(Converters::class)
data class User(
	@PrimaryKey @SerialName("user_id") val id: String,
	@SerialName("clinic_id") val clinicId: String? = null,
	@SerialName("phone_number") val phoneNumber: String? = null,
	@SerialName("display_name") var displayName: String? = null,
	@SerialName("national_code") val nationalCode: String? = null,
	val username: String? = null,
	val email: String? = null,
	@SerialName("birth_date") @Serializable(DateSerializer::class) val birthDate: Date? = null,
	val gender: Gender? = null,
	var bio: String? = null,
	@SerialName("has_consented") val hasMlConsent: Boolean = false,
	@SerialName("avatar_url") val avatarUrl: String? = null,
	@SerialName("two_factor_password") val twoFactorPassword: String? = null,
	@SerialName("two_factor_password_hint") val twoFactorPasswordHint: String? = null,
	val status: UserStatus = UserStatus.Active,
	val role: UserRole = UserRole.Patient,
	@Embedded @SerialName("role_properties") val roleProperties: UserRoleProperties? = null,
	@SerialName("last_seen_at") val lastSeenAt: Long? = null,
	// Removed friendsId and blockedUsersId lists - we'll use relationship tables instead
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
		@SerialName("treatment_status") val treatmentStatus: TreatmentStatus? = null,
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
		@SerialName("treatment_started_at") val treatmentStartedAt: Long? = null,
		@SerialName("stability_started_at") val stabilityStartedAt: Long? = null,
		@SerialName("treatment_end_at") val treatmentEndAt: Long? = null,
	) : UserRoleProperties()

	@Serializable
	@SerialName("doctor")
	data class Doctor(
		@SerialName("expertise") val expertise: String? = null,
		@SerialName("medical_council_code") val medicalCouncilCode: String,
		@SerialName("years_of_experience") val yearsOfExperience: Int? = null,
		@SerialName("visit_personal_fee") val visitPersonalFee: Int? = null,
		@SerialName("visit_group_fee") val visitGroupFee: Int? = null,
	) : UserRoleProperties()

	@Serializable
	@SerialName("consultant")
	data class Consultant(
		@SerialName("expertise") val expertise: String? = null,
	) : UserRoleProperties()
//
//	@Serializable
//	@SerialName("assistant")
//	data class Assistant(val modelName: String) : UserRoleProperties()
//
//	@Serializable
//	@SerialName("viewer")
//	data class Viewer(
//		// Can add specific fields for viewers in the future
//	) : UserRoleProperties()
}

@Entity(
	tableName = "user_friend",
	foreignKeys = [
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["userId"],
			onDelete = CASCADE,
			onUpdate = CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["friendId"],
			onDelete = CASCADE,
			onUpdate = CASCADE
		)
	],
	primaryKeys = ["userId", "friendId"]
)
@Serializable
data class UserFriend(
	@SerialName("user_id") val userId: String,
	@SerialName("friend_id") val friendId: String,
	@SerialName("status") val status: FriendshipStatus = FriendshipStatus.Pending,
	@SerialName("added_at") val addedAt: Long,
	@SerialName("status_changed_at") val statusChangedAt: Long? = null
)

@Entity(
	tableName = "user_block",
	foreignKeys = [
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["userId"],
			onDelete = CASCADE,
			onUpdate = CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["blockedId"],
			onDelete = CASCADE,
			onUpdate = CASCADE
		)
	],
	primaryKeys = ["userId", "blockedId"]
)
@Serializable
data class UserBlock(
	@SerialName("user_id") val userId: String,
	@SerialName("blocked_id") val blockedId: String,
	@SerialName("reason") val reason: String? = null,
	@SerialName("blocked_at") val blockedAt: Long
)

// Avatar relationship improved
@Entity(
	tableName = "user_avatar",
	foreignKeys = [
		ForeignKey(
			entity = Media::class,
			parentColumns = ["id"],
			childColumns = ["mediaId"],
			onUpdate = CASCADE,
			onDelete = CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["userId"],
			onDelete = CASCADE,
			onUpdate = CASCADE
		)
	],
	primaryKeys = ["userId", "mediaId"]
)
@Serializable
data class UserAvatar(
	@SerialName("media_id") val mediaId: String,
	@SerialName("user_id") val userId: String,
	@SerialName("attached_at") val attachedAt: Long
)

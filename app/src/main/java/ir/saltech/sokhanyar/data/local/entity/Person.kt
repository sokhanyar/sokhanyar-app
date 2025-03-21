package ir.saltech.sokhanyar.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ir.saltech.sokhanyar.BaseApplication.FriendshipStatus
import ir.saltech.sokhanyar.BaseApplication.Gender
import ir.saltech.sokhanyar.BaseApplication.TreatmentStatus
import ir.saltech.sokhanyar.BaseApplication.UserRole
import ir.saltech.sokhanyar.BaseApplication.UserStatus
import ir.saltech.sokhanyar.data.local.dbconfig.Converters
import ir.saltech.sokhanyar.data.local.entity.serializer.DateSerializer
import kotlinx.datetime.Clock
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
	@SerialName("national_code") var nationalCode: String? = null,
	var username: String? = null,
	var email: String? = null,
	@SerialName("birth_date") @Serializable(DateSerializer::class) val birthDate: Date? = null,
	val age: Int? = ((birthDate?.time)?.let { Clock.System.now().toEpochMilliseconds() - it })?.toInt(),    // It must be read-only and just changed based on the birth date
	var gender: Gender? = null,
	var bio: String? = null,
	@SerialName("has_consented") var hasMlConsent: Boolean = false,
	@SerialName("two_factor_password") var twoFactorPassword: String? = null,
	@SerialName("two_factor_password_hint") var twoFactorPasswordHint: String? = null,
	var status: UserStatus = UserStatus.Active,
	val role: UserRole = UserRole.Patient,
	@Embedded @SerialName("role_properties") val roleProperties: UserRoleProperties? = null,
	@SerialName("last_seen_at") var lastSeenAt: Long? = null,
	@SerialName("created_at") val signedUpAt: Long
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
		@SerialName("expertise") var expertise: String? = null,
		@SerialName("medical_council_code") val medicalCouncilCode: String,
		@SerialName("years_of_experience") var yearsOfExperience: Int? = null,
		@SerialName("visit_personal_fee") var visitPersonalFee: Int? = null,
		@SerialName("visit_group_fee") var visitGroupFee: Int? = null,
	) : UserRoleProperties()

	@Serializable
	@SerialName("consultant")
	data class Consultant(
		@SerialName("expertise") var expertise: String? = null,
	) : UserRoleProperties()

	@Serializable
	@SerialName("companion")
	data class CompanionU(
		@SerialName("can_view_tutorials") var canViewTutorials: Boolean = true,     // That means whether user can see the doctors' clips and videos about stuttering
	) : UserRoleProperties()

	@Serializable
	@SerialName("assistant")
	data class Assistant(@SerialName("model_name") var modelName: String? = null) : UserRoleProperties()

	@Serializable
	@SerialName("viewer")
	data class Viewer(
		@SerialName("can_view") var canView: Boolean = true,
	) : UserRoleProperties()
}

@Entity(
	tableName = "user_friends", foreignKeys = [ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["userId"],
		onDelete = CASCADE,
		onUpdate = CASCADE
	), ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["friendId"],
		onDelete = CASCADE,
		onUpdate = CASCADE
	)], primaryKeys = ["userId", "friendId"], indices = [Index("userId"), Index("friendId")]
)
@Serializable
data class UserFriend(
	@SerialName("user_id") val userId: String,
	@SerialName("friend_id") val friendId: String,
	@SerialName("status") var status: FriendshipStatus = FriendshipStatus.Pending,
	@SerialName("added_at") val addedAt: Long,
	@SerialName("status_changed_at") var statusChangedAt: Long? = null,
)

@Entity(
	tableName = "user_blocks", foreignKeys = [ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["userId"],
		onDelete = CASCADE,
		onUpdate = CASCADE
	), ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["blockedId"],
		onDelete = CASCADE,
		onUpdate = CASCADE
	)], primaryKeys = ["userId", "blockedId"], indices = [Index("userId"), Index("blockedId")]
)
@Serializable
data class UserBlock(
	@SerialName("user_id") val userId: String,
	@SerialName("blocked_id") val blockedId: String,    // Blocked User
	@SerialName("reason") val reason: String? = null,
	@SerialName("blocked_at") val blockedAt: Long,
)

@Entity(
	tableName = "user_reports", foreignKeys = [ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["userId"],
		onDelete = CASCADE,
		onUpdate = CASCADE
	), ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["reportedId"],
		onDelete = CASCADE,
		onUpdate = CASCADE
	)], primaryKeys = ["userId", "reportedId"], indices = [Index("userId"), Index("reportedId")]
)
@Serializable
data class UserReport(
	@SerialName("user_id") val userId: String,
	@SerialName("reported_id") val reportedId: String,    // Reported User
	@SerialName("reason") val reason: String? = null,
	@SerialName("reported_at") val reportedAt: Long,
)

@Entity(
	tableName = "user_avatars", foreignKeys = [ForeignKey(
		entity = Media::class,
		parentColumns = ["id"],
		childColumns = ["mediaId"],
		onUpdate = CASCADE,
		onDelete = CASCADE
	), ForeignKey(
		entity = User::class,
		parentColumns = ["id"],
		childColumns = ["userId"],
		onDelete = CASCADE,
		onUpdate = CASCADE
	)], primaryKeys = ["userId", "mediaId"], indices = [Index("userId"), Index("mediaId")]
)
@Serializable
data class UserAvatar(
	@SerialName("media_id") val mediaId: String,
	@SerialName("user_id") val userId: String,
	@SerialName("attached_at") val attachedAt: Long,
)

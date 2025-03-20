package ir.saltech.sokhanyar.data.local.entity

import ir.saltech.sokhanyar.data.local.entity.treatment.Visit
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

// Association Tables
@Entity(
	tableName = "doctor_patient_visit_association",
	foreignKeys = [
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["doctorId"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["patientId"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = Visit::class,
			parentColumns = ["id"],
			childColumns = ["visitId"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		)
	],
	primaryKeys = ["doctorId", "patientId", "visitId"],
	indices = [
		Index("doctorId"),
		Index("patientId"),
		Index("visitId")
	]
)
@Serializable
data class DoctorPatientVisitCrossRef(
	val doctorId: String,
	val patientId: String,
	val visitId: String,
)

@Entity(
	tableName = "consultant_patient_association",
	foreignKeys = [
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["consultantId"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["patientId"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		)
	],
	primaryKeys = ["consultantId", "patientId"],
	indices = [
		Index("consultantId"),
		Index("patientId")
	]
)
@Serializable
data class ConsultantPatientCrossRef(
	val consultantId: String,
	val patientId: String,
)

@Entity(tableName = "companion_relationship_type")
@Serializable
data class CompanionRelationshipType(
	@PrimaryKey(autoGenerate = true) val id: Long,
	val name: String,
)

@Entity(
	tableName = "companion_patient_association",
	foreignKeys = [
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["companionId"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["patientId"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = CompanionRelationshipType::class,
			parentColumns = ["id"],
			childColumns = ["relationshipTypeId"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		)
	],
	primaryKeys = ["companionId", "patientId"],
	indices = [
		Index("companionId"),
		Index("patientId"),
		Index("relationshipTypeId")
	]
)
@Serializable
data class CompanionPatientCrossRef(
	val companionId: String,
	val patientId: String,
	val relationshipTypeId: String,
)

@Entity(
	tableName = "user_clinic_association",
	foreignKeys = [
		ForeignKey(
			entity = User::class,
			parentColumns = ["id"],
			childColumns = ["userId"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = Clinic::class,
			parentColumns = ["id"],
			childColumns = ["clinicId"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		)
	],
	primaryKeys = ["userId", "clinicId"],
	indices = [
		Index("userId"),
		Index("clinicId")
	]
)
@Serializable
data class UserClinicCrossRef(
	val userId: String,
	val clinicId: String,
)

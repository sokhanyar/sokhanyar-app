package ir.saltech.sokhanyar.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.saltech.sokhanyar.BaseApplication.UserRole
import ir.saltech.sokhanyar.data.local.entity.Clinic
import ir.saltech.sokhanyar.data.local.entity.User

@Dao
interface ClinicDao {
	@Query("select * from clinic")
	fun getAll(): List<Clinic>

	@Query(
		"SELECT user.* FROM user join clinic on user.clinicId == clinic.id GROUP BY user.id having user.role == :userRole"
	)
	fun getUsersByUserRole(userRole: UserRole): List<User>

	@Query(
		"select * from clinic where clinic.id == :clinicId"
	)
	fun findByClinicId(clinicId: String): Clinic

	@Insert
	fun add(clinic: Clinic)

	@Update
	fun update(clinic: Clinic)
}
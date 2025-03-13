package ir.saltech.sokhanyar.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ir.saltech.sokhanyar.data.local.entities.Clinic
import ir.saltech.sokhanyar.data.local.entities.User

@Dao
interface ClinicDao {
	@Query("select * from clinic")
	fun getAll(): List<Clinic>

	@Query(
		"SELECT user.* FROM user join clinic on user.clinicId == clinic.id GROUP BY user.uid"
	)
	fun loadUserAndBookNames(): List<User>

}
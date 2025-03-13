package ir.saltech.sokhanyar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ir.saltech.sokhanyar.data.local.entities.User

@Dao
interface UserDao {
	@Query("SELECT * FROM user")
	fun getAll(): List<User>

	@Query("SELECT * FROM user WHERE displayName LIKE :displayName LIMIT 1")
	fun findByDisplayName(displayName: String): User

	@Query("SELECT * FROM user WHERE phoneNumber=:phoneNumber")
	fun findByPhoneNumber(phoneNumber: String): User

	@Insert
	fun insert(user: User)

	@Insert
	fun insertAll(vararg users: User)

	@Delete
	fun delete(user: User)
}

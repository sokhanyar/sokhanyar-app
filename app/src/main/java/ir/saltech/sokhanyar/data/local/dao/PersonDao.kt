package ir.saltech.sokhanyar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import ir.saltech.sokhanyar.BaseApplication.AppDatabase
import ir.saltech.sokhanyar.data.local.entities.User

@Dao
interface UserDao {
	@Query("SELECT * FROM user")
	fun getAll(): List<User>

	@Query("SELECT * FROM user WHERE uid IN (:userIds)")
	fun loadAllByIds(userIds: IntArray): List<User>

	@Query("SELECT * FROM user WHERE username LIKE :first AND " +
			" LIKE :last LIMIT 1")
	fun findByName(first: String, last: String): User

	@Insert
	fun insertAll(vararg users: User)

	@Delete
	fun delete(user: User)
}



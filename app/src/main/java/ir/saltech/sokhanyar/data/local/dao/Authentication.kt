package ir.saltech.sokhanyar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.saltech.sokhanyar.data.local.entity.Device

@Dao
interface DeviceDao {
	@Query("select * from device where userId == :userId")
	fun findByUserId(userId: String): Device

	@Query("select * from device")
	fun getAll(): List<Device>

	@Insert
	fun add(device: Device)

	@Delete
	fun remove(device: Device)

	@Update
	fun update(device: Device)
}

package ir.saltech.sokhanyar.data.local.dbconfig

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ir.saltech.sokhanyar.data.local.dao.ClinicDao
import ir.saltech.sokhanyar.data.local.dao.DeviceDao
import ir.saltech.sokhanyar.data.local.dao.UserDao
import ir.saltech.sokhanyar.data.local.entity.Clinic
import ir.saltech.sokhanyar.data.local.entity.Device
import ir.saltech.sokhanyar.data.local.entity.User
import ir.saltech.sokhanyar.data.local.entity.UserRoleProperties
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@Database(entities = [User::class, Device::class, Clinic::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao
	abstract fun deviceDao(): DeviceDao
	abstract fun clinicDao(): ClinicDao
}

object Converters {
	// For List<String>
	@TypeConverter
	fun fromStringList(value: String?): List<String>? {
		if (value == null) return null
		return Json.Default.decodeFromString(ListSerializer(serializer()), value)
	}

	@TypeConverter
	fun toStringList(list: List<String>?): String? {
		if (list == null) return null
		return Json.Default.encodeToString(ListSerializer(serializer()), list)
	}

	@TypeConverter
	fun fromIntArrayList(value: String?): List<Array<Int>>? {
		if (value == null) return null
		return Json.Default.decodeFromString(ListSerializer(serializer()), value)
	}

	@TypeConverter
	fun toIntArrayList(list: List<Array<Int>>?) : String? {
		if (list == null) return null
		return Json.Default.encodeToString(ListSerializer(serializer()), list)
	}

	// For UserRoleProperties
	@TypeConverter
	fun fromUserRoleProperties(value: String?): UserRoleProperties? {
		if (value == null) return null
		return Json.Default.decodeFromString(value)
	}

	@TypeConverter
	fun toUserRoleProperties(userRoleProperties: UserRoleProperties?): String? {
		if (userRoleProperties == null) return null
		return Json.Default.encodeToString(userRoleProperties)
	}

	// For Device
	@TypeConverter
	fun fromDevice(value: String?): Device? {
		if (value == null) return null
		return Json.Default.decodeFromString(value)
	}

	@TypeConverter
	fun toDevice(device: Device?): String? {
		if (device == null) return null
		return Json.Default.encodeToString(device)
	}

	// For Clinic
	@TypeConverter
	fun fromClinic(value: String?): Clinic? {
		if (value == null) return null
		return Json.Default.decodeFromString(value)
	}

	@TypeConverter
	fun toClinic(clinic: Clinic?): String? {
		if (clinic == null) return null
		return Json.Default.encodeToString(clinic)
	}
}
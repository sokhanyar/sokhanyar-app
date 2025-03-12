package ir.saltech.sokhanyar.data.local.dbconfig

import android.location.Address
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ir.saltech.sokhanyar.data.local.dao.UserDao
import ir.saltech.sokhanyar.data.local.entities.Device
import ir.saltech.sokhanyar.data.local.entities.User
import ir.saltech.sokhanyar.data.local.entities.UserRoleProperties
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@Database(entities = [User::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao
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

	// For Custom Class (e.g., Address)
	@TypeConverter
	fun fromAddress(value: String?): Address? {
		if (value == null) return null
		return Json.Default.decodeFromString(value)
	}

	@TypeConverter
	fun toAddress(address: Address?): String? {
		if (address == null) return null
		return Json.Default.encodeToString(address)
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
}
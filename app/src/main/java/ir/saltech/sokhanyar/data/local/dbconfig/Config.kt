package ir.saltech.sokhanyar.data.local.dbconfig

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ir.saltech.sokhanyar.data.local.dao.AdviceDao
import ir.saltech.sokhanyar.data.local.dao.AssociationsDao
import ir.saltech.sokhanyar.data.local.dao.CallDao
import ir.saltech.sokhanyar.data.local.dao.ChatDao
import ir.saltech.sokhanyar.data.local.dao.ClinicDao
import ir.saltech.sokhanyar.data.local.dao.DeviceDao
import ir.saltech.sokhanyar.data.local.dao.MediaDao
import ir.saltech.sokhanyar.data.local.dao.MessageDao
import ir.saltech.sokhanyar.data.local.dao.PerformanceReportDao
import ir.saltech.sokhanyar.data.local.dao.PracticalVoiceDao
import ir.saltech.sokhanyar.data.local.dao.UserDao
import ir.saltech.sokhanyar.data.local.dao.VisitDao
import ir.saltech.sokhanyar.data.local.entity.ChannelAdmin
import ir.saltech.sokhanyar.data.local.entity.ChannelBan
import ir.saltech.sokhanyar.data.local.entity.ChannelSubscriber
import ir.saltech.sokhanyar.data.local.entity.Chat
import ir.saltech.sokhanyar.data.local.entity.Clinic
import ir.saltech.sokhanyar.data.local.entity.CompanionPatientCrossRef
import ir.saltech.sokhanyar.data.local.entity.CompanionRelationshipType
import ir.saltech.sokhanyar.data.local.entity.ConsultantPatientCrossRef
import ir.saltech.sokhanyar.data.local.entity.Device
import ir.saltech.sokhanyar.data.local.entity.DoctorPatientVisitCrossRef
import ir.saltech.sokhanyar.data.local.entity.GroupAdmin
import ir.saltech.sokhanyar.data.local.entity.GroupBan
import ir.saltech.sokhanyar.data.local.entity.GroupMember
import ir.saltech.sokhanyar.data.local.entity.Media
import ir.saltech.sokhanyar.data.local.entity.Message
import ir.saltech.sokhanyar.data.local.entity.MessageMedia
import ir.saltech.sokhanyar.data.local.entity.MessageReaction
import ir.saltech.sokhanyar.data.local.entity.MessageRead
import ir.saltech.sokhanyar.data.local.entity.MessageReport
import ir.saltech.sokhanyar.data.local.entity.User
import ir.saltech.sokhanyar.data.local.entity.UserAvatar
import ir.saltech.sokhanyar.data.local.entity.UserBlock
import ir.saltech.sokhanyar.data.local.entity.UserClinicCrossRef
import ir.saltech.sokhanyar.data.local.entity.UserFriend
import ir.saltech.sokhanyar.data.local.entity.UserReport
import ir.saltech.sokhanyar.data.local.entity.UserRoleProperties
import ir.saltech.sokhanyar.data.local.entity.treatment.Advice
import ir.saltech.sokhanyar.data.local.entity.treatment.Call
import ir.saltech.sokhanyar.data.local.entity.treatment.CallParticipant
import ir.saltech.sokhanyar.data.local.entity.treatment.DailyReport
import ir.saltech.sokhanyar.data.local.entity.treatment.PracticalVoice
import ir.saltech.sokhanyar.data.local.entity.treatment.Visit
import ir.saltech.sokhanyar.data.local.entity.treatment.WeeklyReport
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.File
import java.util.Date

@Database(
	entities = [
		User::class, Device::class, UserAvatar::class, UserFriend::class, UserBlock::class, UserReport::class,
		Clinic::class, WeeklyReport::class, DailyReport::class, PracticalVoice::class, Call::class, CallParticipant::class, Advice::class, Visit::class,
		Media::class, Message::class, Chat::class, MessageMedia::class, MessageReaction::class, MessageRead::class, MessageReport::class,
		GroupMember::class, GroupAdmin::class, GroupBan::class, ChannelSubscriber::class, ChannelAdmin::class, ChannelBan::class,
		DoctorPatientVisitCrossRef::class, ConsultantPatientCrossRef::class, CompanionRelationshipType::class, CompanionPatientCrossRef::class, UserClinicCrossRef::class],
	version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao
	abstract fun deviceDao(): DeviceDao
	abstract fun clinicDao(): ClinicDao
	abstract fun mediaDao(): MediaDao
	abstract fun messageDao(): MessageDao
	abstract fun chatDao(): ChatDao
	abstract fun adviceDao(): AdviceDao
	abstract fun callDao(): CallDao
	abstract fun practicalVoiceDao(): PracticalVoiceDao
	abstract fun performanceReportDao(): PerformanceReportDao
	abstract fun visitDao(): VisitDao
	abstract fun associationsDao(): AssociationsDao
}

object Converters {
	// For Date
	@TypeConverter
	fun fromDate(value: Long?): Date? {
		return value?.let { Date(it) }
	}

	@TypeConverter
	fun toDate(date: Date?): Long? {
		return date?.time
	}

	// For File
	@TypeConverter
	fun fromFile(value: String?): File? {
		if (value == null) return null
		return File(value)
	}

	@TypeConverter
	fun toFile(file: File?): String? {
		return file?.absolutePath
	}

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

	// for IntArray
	@TypeConverter
	fun fromIntArrayList(value: String?): List<Array<Int>>? {
		if (value == null) return null
		return Json.Default.decodeFromString(ListSerializer(serializer()), value)
	}

	@TypeConverter
	fun toIntArrayList(list: List<Array<Int>>?): String? {
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
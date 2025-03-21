package ir.saltech.sokhanyar

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ir.saltech.sokhanyar.AuthDbInstrumentedTest.TestUtil.shouldBe
import ir.saltech.sokhanyar.AuthDbInstrumentedTest.TestUtil.shouldHaveSize
import ir.saltech.sokhanyar.AuthDbInstrumentedTest.TestUtil.shouldNotBe
import ir.saltech.sokhanyar.BaseApplication.UserRole
import ir.saltech.sokhanyar.data.local.dbconfig.AppDatabase
import ir.saltech.sokhanyar.data.local.entity.Clinic
import ir.saltech.sokhanyar.data.local.entity.Device
import ir.saltech.sokhanyar.data.local.entity.User
import kotlinx.io.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthDbInstrumentedTest {
	private lateinit var db: AppDatabase

	@Before
	fun createDb() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		db = Room.inMemoryDatabaseBuilder(
			context, AppDatabase::class.java
		).build()
	}

	@After
	@Throws(IOException::class)
	fun closeDb() {
		db.close()
	}

	@Test
	@Throws(Exception::class)
	fun writeUserAndCheckSubmitted() {
		val testName = "george"
		val user: User = User(id = "P(G*udzf8jdzf8p9hjf-9hjdf98hjdf", signedUpAt = 1L).apply {
			copy(displayName = testName)
		}
		db.userDao().add(user)
		db.userDao().getAll() shouldHaveSize 1
	}

	@Test
	fun registerDeviceAndCheckSubmitted() {
		val userId = "aldkjd;lfkjas;dlkjasd;lkjasdf;lkjasdf"
		val deviceId = "a.djasdfkjaksdfnj;lasdjflkjsadfkljasldkfjk;lasdf"
		val device = Device(deviceId, userId)
		val user = User(userId, signedUpAt = 1L)
		db.userDao().add(user)
		db.deviceDao().add(device)
		db.deviceDao().findByUserId(userId).id shouldBe deviceId
		db.userDao().getAll().contains(user) shouldBe true
	}

	@Test
	fun updateDeviceAndCheckSubmitted() {
		val userId = "aldkjd;lfkjas;dlkjasd;lkjasdf;lkjasdf"
		val deviceId = "a.djasdfkjaksdfnj;lasdjflkjsadfkljasldkfjk;lasdf"
		var device = Device(deviceId, userId)
		var user = User(userId, phoneNumber = "09138549727", signedUpAt = 1L)
		db.userDao().add(user)
		db.deviceDao().add(device)
		user = user.copy()
	}

	@Test
	fun addUserToClinicAndCheckSubmitted() {
		val userId = "as;dfkasdf;adsjlkajdl;kjadlksfjl;kdajf'lkajdf"
		val clinicId = "as;ldkfjsdljasd;lfkasdf;lkjasdf"
		val clinic = Clinic(id = clinicId, name = "URSIRR", address = "asldkjad;ljadsf", phoneNumbers = listOf("sd"), acceptConsultants = false, acceptViewers = false, createdAt = 1L)
		val user = User(userId, clinicId = clinicId, role = UserRole.Patient, signedUpAt = 1L)
		db.userDao().add(user)
		db.clinicDao().add(clinic)
		db.clinicDao().getAll().isEmpty() shouldNotBe true
	}

	@Test
	fun checkPatientUserAddedToClinicList() {
		val userId = "as;dfkasdf;adsjlkajdl;kjadlksfjl;kdajf'lkajdf"
		val clinicId = "as;ldkfjsdljasd;lfkasdf;lkjasdf"
		val clinic = Clinic(id = clinicId, name = "URSIRR", address = "asldkjad;ljadsf", phoneNumbers = listOf("sd"), acceptConsultants = false, acceptViewers = false, createdAt = 1L)
		val user = User(userId, clinicId = clinicId, role = UserRole.Patient, signedUpAt = 1L)
		println(UserRole.Patient)
		db.userDao().add(user)
		db.clinicDao().add(clinic)
		db.clinicDao().getUsersByUserRole(UserRole.Patient).isEmpty() shouldNotBe true
	}

	private object TestUtil {
		inline infix fun <reified T> T.shouldBe(t: T) = assert(this == t)
		inline infix fun <reified T> T.shouldNotBe(t: T) = assert(this != t)
		inline infix fun <reified T> Iterable<T>.shouldHaveSize(size: Int) = assert(this.toList().size == size)
	}
}

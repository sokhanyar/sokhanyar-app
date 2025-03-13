package ir.saltech.sokhanyar

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ir.saltech.sokhanyar.DatabaseInstrumentedTest.TestUtil.createUser
import ir.saltech.sokhanyar.DatabaseInstrumentedTest.TestUtil.shouldHaveSize
import ir.saltech.sokhanyar.data.local.dao.UserDao
import ir.saltech.sokhanyar.data.local.dbconfig.AppDatabase
import ir.saltech.sokhanyar.data.local.entities.User
import kotlinx.io.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseInstrumentedTest {
	private lateinit var db: AppDatabase
	private lateinit var userDao: UserDao

	@Before
	fun createDb() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		db = Room.inMemoryDatabaseBuilder(
			context, AppDatabase::class.java
		).build()
		userDao = db.userDao()
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
		val user: User = createUser(id = "P(G*udzf8jdzf8p9hjf-9hjdf98hjdf").apply {
			copy(displayName = testName)
		}
		userDao.insertAll(user)
		userDao.getAll() shouldHaveSize 1
	}

	private object TestUtil {
		fun createUser(id: String): User {
			return User(id)
		}

		inline infix fun <reified T> T.shouldBe(t: T) = assert(this == t)
		inline infix fun <reified T> Iterable<T>.shouldHaveSize(size: Int) = assert(this.toList().size == size)
	}
}

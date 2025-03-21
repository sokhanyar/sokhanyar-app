package ir.saltech.sokhanyar

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ir.saltech.sokhanyar.data.local.dbconfig.AppDatabase
import ir.saltech.sokhanyar.data.local.entity.User
import kotlinx.io.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatDbInstrumentedTest {
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
	fun writeMediaAndCheckSubmitted() {
		val userId = "aslkdfjasdfjklasef;klj"
		val mediaId = "kjlkxjidies"
		val user = User(id=userId, signedUpAt = 1L)
//		val media = Media(id=mediaId, uploaderId = userId, )
//		TODO: Test another sections of chat later. (Can Use Mocks?)
	}

	private object TestUtil {
		inline infix fun <reified T> T.shouldBe(t: T) = assert(this == t)
		inline infix fun <reified T> T.shouldNotBe(t: T) = assert(this != t)
		inline infix fun <reified T> Iterable<T>.shouldHaveSize(size: Int) = assert(this.toList().size == size)
	}
}

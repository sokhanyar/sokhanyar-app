package ir.saltech.sokhanyar

import androidx.test.ext.junit.runners.AndroidJUnit4
import ir.saltech.sokhanyar.data.remote.api.SokhanYarApi
import ir.saltech.sokhanyar.util.getMimeType
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ApiInstrumentedTest {

	@Test
	fun shouldAnalyzeVoiceFileDirectReturnsValidAdviceResponse() = runTest {
		// TODO: Test needs to be fixed
		val filePath = "C:\\Users\\saleh\\Documents\\Sound Recordings\\Recording (2).m4a"
		val testFile = File(filePath)
		val testFileName = testFile.name
		var testFileBA: ByteArray? = null
		with(testFile) {
			testFileBA = testFile.readBytes()
		}
		val response = SokhanYarApi().analyzeTreatmentVoiceMediaFile(
			accessToken = """
			eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwNzk4ODNhZS1lYjE5LTRmZGQtODhhYS04MjE5NDE3MjJkOWMiLCJpc3MiOiJzb2toYW55YWFyLmlyIiwic3ViIjoiMTYzYzRjYWYxYWQyMjNiYmRkODRlYTVhZTQ2OWVlMmY0ODJlZDI1MiIsImlhdCI6MTc0MTY2Mjk0MiwibmJmIjoxNzQxNjYyOTQyLCJleHAiOjE3NDE3NDkzNDIsInNjb3BlIjoiY2xpZW50In0.D4Gq02SHTIxL-ob56l_KEPG2-skMQjbkEUw2D3NxNqJ1xR270-wSkF2awtHfhv478laCxKIlwXhc0FnYeKTJZw
		""".trimIndent(),
			fileByteArray = testFileBA!!,
			fileName = testFileName,
			fileContentType = testFile.getMimeType() ?: return@runTest
		) { bytesSent: Long, contentLength: Long? ->
			{
				println("Sending file ${testFile.name} --> sent: $bytesSent | whole: $contentLength | ${(bytesSent / (contentLength ?: 1)) * 100}")
			}
		}
		assert(response.advice.isNotEmpty())
	}
}
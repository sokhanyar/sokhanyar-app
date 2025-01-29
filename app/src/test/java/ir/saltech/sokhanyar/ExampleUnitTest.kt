package ir.saltech.sokhanyar

import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ir.saltech.sokhanyar.model.api.ResponseObject
import ir.saltech.sokhanyar.model.data.general.AuthInfo
import ir.saltech.sokhanyar.util.StreamGenerateText
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
	@Test
	fun `connect should emit data when receiving valid 'data' prefixed lines from the server`() {
		// Arrange
		val mockClient = mockk<HttpClient>()
		val mockResponse = mockk<HttpResponse>()
		val mockChannel = mockk<ByteReadChannel>()
		val streamGenerateText = StreamGenerateText()
		streamGenerateText.client = mockClient

//		coEvery { mockClient.post(any(), any()) } returns mockResponse
//		every { mockResponse.bodyAsChannel() } returns mockChannel
		coEvery { mockChannel.isClosedForRead } returnsMany listOf(false, false, true)
		coEvery { mockChannel.readUTF8Line() } returnsMany listOf(
			"data: Test1",
			"invalid line",
			"data: Test2"
		)

		// Act
//		val result = streamGenerateText.connect("testEndpoint", "testToken").asL()

		// Assert
//		assertEquals(listOf("Test1", "Test2"), result)
		coVerify(exactly = 1) {
			mockClient.post("${BaseApplication.Constants.SOKHANYAR_BASE_URL}testEndpoint", any())
		}
	}

	@Test
	fun `doSignIn should construct correct endpoint URL with different input parameters`() {
		// Arrange
		val mockClient = mockk<HttpClient>()
		val mockResponse = mockk<HttpResponse>()
		val streamGenerateText = StreamGenerateText()
		streamGenerateText.client = mockClient

		val authInfo = AuthInfo(phoneNumber = 1234567890)
		val responseObject = ResponseObject(status = "success", message = "OTP sent")

		coEvery {
			mockClient.post("https://api.sokhanyaar.ir/api/v1/auth/send-otp") {
				setBody(AuthInfo())
			}
		} returns mockResponse

		coEvery {
			mockResponse.body<ResponseObject>()
		} returns responseObject

		// Act & Assert
		runBlocking {

			// Test with custom endpoint
			streamGenerateText.doSignIn(authInfo = authInfo)
			coVerify {
				mockClient.post(
					"https://api.sokhanyaar.ir/api/v1/auth/send-otp"
				) {

				}
			}
		}
		responseObject shouldBe responseObject
	}
}
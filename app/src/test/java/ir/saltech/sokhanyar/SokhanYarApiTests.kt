package ir.saltech.sokhanyar

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveMinLength
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import ir.saltech.sokhanyar.BaseApplication.UserRole
import ir.saltech.sokhanyar.data.remote.api.ApiClient
import ir.saltech.sokhanyar.data.remote.api.SokhanYarApi
import ir.saltech.sokhanyar.data.remote.request.GenerateMotivationTextRequest
import ir.saltech.sokhanyar.data.remote.request.OtpCodeRequest
import ir.saltech.sokhanyar.data.remote.request.RegisterDeviceRequest
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SokhanYarApiTests {

	@Test
	fun `(clinics info request) returns expected clinic id (mock)`() = runTest {
		val mockResponse = """
			{
				"clinics": [
					{
						"name": "کلینیک نگاه نو",
						"phone_numbers": [
							"09135363557",
							"03535249369"
						],
						"website": "https://clinic-negaheno.com/",
						"opening_hours": [
							[
								15,
								21
							],
							[
								15,
								21
							],
							[
								15,
								21
							],
							[
								15,
								21
							],
							[
								15,
								21
							],
							[
								15,
								21
							]
						],
						"accepted_insurances": null,
						"accept_consultants": true,
						"id": "5UJcr8WfWT-LSZzkyT0Qj",
						"address": "یزد بلوار دولت آباد کوچه دهم پلاک ۲۴۲ گفتاردرمانی و کاردرمانی نگاه نو",
						"email": "info@clinic-negaheno.com",
						"opening_days": [
							"saturday",
							"sunday",
							"monday",
							"tuesday",
							"wednesday",
							"thursday"
						],
						"accept_viewers": true,
						"created_at": 1741491803
					}
				]
			}
		""".trimIndent()
		mockApi(mockResponse).getClinicsInfo().clinics[0].id shouldBe "5UJcr8WfWT-LSZzkyT0Qj"
	}

	@Test
	fun `(clinics info request) returns expected opening hours (mock)`() = runTest {
		val mockResponse = """
			{
				"clinics": [
					{
						"name": "کلینیک نگاه نو",
						"phone_numbers": [
							"09135363557",
							"03535249369"
						],
						"website": "https://clinic-negaheno.com/",
						"opening_hours": [
							[
								15,
								21
							],
							[
								15,
								21
							],
							[
								15,
								21
							],
							[
								15,
								21
							],
							[
								15,
								21
							],
							[
								15,
								21
							]
						],
						"accepted_insurances": null,
						"accept_consultants": true,
						"id": "5UJcr8WfWT-LSZzkyT0Qj",
						"address": "یزد بلوار دولت آباد کوچه دهم پلاک ۲۴۲ گفتاردرمانی و کاردرمانی نگاه نو",
						"email": "info@clinic-negaheno.com",
						"opening_days": [
							"saturday",
							"sunday",
							"monday",
							"tuesday",
							"wednesday",
							"thursday"
						],
						"accept_viewers": true,
						"created_at": 1741491803
					}
				]
			}
		""".trimIndent()
		mockApi(mockResponse).getClinicsInfo().clinics[0].openingHours shouldBe listOf(
			intArrayOf(
				15, 21
			), intArrayOf(
				15, 21
			), intArrayOf(
				15, 21
			), intArrayOf(
				15, 21
			), intArrayOf(
				15, 21
			), intArrayOf(
				15, 21
			)
		)
	}

	@Test
	fun `(register device request) returns expected device id and user id`() = runTest {
		val response = SokhanYarApi().registerDevice(RegisterDeviceRequest("5UJcr8WfWT-LSZzkyT0Qj", "09138549727", UserRole.Patient))
		response.userId shouldBe "163c4caf1ad223bbdd84ea5ae469ee2f482ed252"
		response.deviceId shouldBe "91a53d4360b329d418671c461604fb584d34e0a7"
	}

	@Test
	fun `(register device request) throws error when mismatch items value requested`() = runTest {
		shouldThrow<ApiClient.ResponseError> {
			SokhanYarApi().registerDevice(RegisterDeviceRequest("5UJcr8WfWT-LSZzkyT0Q6j", "09138549727", UserRole.Patient))
		}
	}

	@Test
	fun `(otp request) returns expected result`() = runTest {
		val mockResponse = """
			{
				"message": "otp sent",
				"timestamp": 1824890323
			}
		""".trimIndent()
		mockApi(mockResponse).requestOtpCodeAuth(OtpCodeRequest("91a53d4360b329d418671c461604fb584d34e0a7")).message shouldBe "otp sent"
	}

	@Test
	fun `(otp request) throws error when mismatch items value requested`() = runTest {
		shouldThrow<ApiClient.ResponseError> {
			SokhanYarApi().requestOtpCodeAuth(OtpCodeRequest("91a53d4360b329d418671c461604fb584d34e0a7u"))
		}
	}

	@Test
	fun `(generate motivation text) returns valid response`() = runTest {
		val response = SokhanYarApi().generateMotivationText(accessToken = """
			eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwNzk4ODNhZS1lYjE5LTRmZGQtODhhYS04MjE5NDE3MjJkOWMiLCJpc3MiOiJzb2toYW55YWFyLmlyIiwic3ViIjoiMTYzYzRjYWYxYWQyMjNiYmRkODRlYTVhZTQ2OWVlMmY0ODJlZDI1MiIsImlhdCI6MTc0MTY2Mjk0MiwibmJmIjoxNzQxNjYyOTQyLCJleHAiOjE3NDE3NDkzNDIsInNjb3BlIjoiY2xpZW50In0.D4Gq02SHTIxL-ob56l_KEPG2-skMQjbkEUw2D3NxNqJ1xR270-wSkF2awtHfhv478laCxKIlwXhc0FnYeKTJZw
		""".trimIndent(), request = GenerateMotivationTextRequest())
		response.motivationText shouldHaveMinLength 5
	}

	@Test
	fun `(generate motivation text) returns valid stream response`() = runTest {
		val response = SokhanYarApi().generateMotivationTextStream(accessToken = """
			eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwNzk4ODNhZS1lYjE5LTRmZGQtODhhYS04MjE5NDE3MjJkOWMiLCJpc3MiOiJzb2toYW55YWFyLmlyIiwic3ViIjoiMTYzYzRjYWYxYWQyMjNiYmRkODRlYTVhZTQ2OWVlMmY0ODJlZDI1MiIsImlhdCI6MTc0MTY2Mjk0MiwibmJmIjoxNzQxNjYyOTQyLCJleHAiOjE3NDE3NDkzNDIsInNjb3BlIjoiY2xpZW50In0.D4Gq02SHTIxL-ob56l_KEPG2-skMQjbkEUw2D3NxNqJ1xR270-wSkF2awtHfhv478laCxKIlwXhc0FnYeKTJZw
		""".trimIndent(), request = GenerateMotivationTextRequest())

		response.collect { it shouldHaveMinLength 1 }
	}

	///////////////////////////////////

	private fun mockApi(mockResponse: String): SokhanYarApi = SokhanYarApi(getMockEngine(mockResponse))

	private fun getMockEngine(mockResponse: String): MockEngine = MockEngine { request ->
		respond(
			content = ByteReadChannel(mockResponse),
			status = HttpStatusCode.OK,
			headers = headersOf(HttpHeaders.ContentType, "application/json")
		)
	}
}
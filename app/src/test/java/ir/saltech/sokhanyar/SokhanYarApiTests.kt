package ir.saltech.sokhanyar

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.maps.shouldHaveSize
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
import ir.saltech.sokhanyar.util.getMimeType
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File

class SokhanYarApiTests {

	@Test
	fun `(clinics info request) should returns expected clinic id (mock)`() = runTest {
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
	fun `(clinics info request) should returns expected opening hours (mock)`() = runTest {
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
	fun `(register device request) should returns expected device id and user id`() = runTest {
		val response = SokhanYarApi().registerDevice(
			RegisterDeviceRequest(
				"5UJcr8WfWT-LSZzkyT0Qj",
				"09138549727",
				UserRole.Patient
			)
		)
		response.userId shouldBe "163c4caf1ad223bbdd84ea5ae469ee2f482ed252"
		response.deviceId shouldBe "91a53d4360b329d418671c461604fb584d34e0a7"
	}

	@Test
	fun `(register device request) throws error when mismatch items value requested`() = runTest {
		shouldThrow<ApiClient.ApiError> {
			SokhanYarApi().registerDevice(
				RegisterDeviceRequest(
					"5UJcr8WfWT-LSZzkyT0Q6j",
					"09138549727",
					UserRole.Patient
				)
			)
		}
	}

	@Test
	fun `(otp request) should returns expected result (mock)`() = runTest {
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
		shouldThrow<ApiClient.ApiError> {
			SokhanYarApi().requestOtpCodeAuth(OtpCodeRequest("91a53d4360b329d418671c461604fb584d34e0a7u"))
		}
	}

	@Test
	fun `(get ai models info) should returns valid general ai models info (mock)`() = runTest {
		val mockResponse = """
			{
				"available_models": {
					"released": [
						{
							"name": "models/gemini-1.5-flash-8b-latest",
							"displayName": "Gemini 1.5 Flash-8B Latest",
							"description": "Alias that points to the most recent production (non-experimental) release of Gemini 1.5 Flash-8B, our smallest and most cost effective Flash model, released in October of 2024.",
							"version": "001",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 1000000,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"createCachedContent",
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-1.5-flash-latest",
							"displayName": "Gemini 1.5 Flash Latest",
							"description": "Alias that points to the most recent production (non-experimental) release of Gemini 1.5 Flash, our fast and versatile multimodal model for scaling across diverse tasks.",
							"version": "001",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 1000000,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-1.5-pro-latest",
							"displayName": "Gemini 1.5 Pro Latest",
							"description": "Alias that points to the most recent production (non-experimental) release of Gemini 1.5 Pro, our mid-size multimodal model that supports up to 2 million tokens.",
							"version": "001",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 2000000,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						}
					],
					"preview": [
						{
							"name": "models/gemini-2.0-pro-exp",
							"displayName": "Gemini 2.0 Pro Experimental",
							"description": "Experimental release (February 5th, 2025) of Gemini 2.0 Pro",
							"version": "2.0",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 2097152,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-2.0-pro-exp-02-05",
							"displayName": "Gemini 2.0 Pro Experimental 02-05",
							"description": "Experimental release (February 5th, 2025) of Gemini 2.0 Pro",
							"version": "2.0",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 2097152,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-exp-1206",
							"displayName": "Gemini Experimental 1206",
							"description": "Experimental release (February 5th, 2025) of Gemini 2.0 Pro",
							"version": "2.0",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 2097152,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-2.0-flash-thinking-exp-01-21",
							"displayName": "Gemini 2.0 Flash Thinking Experimental 01-21",
							"description": "Experimental release (January 21st, 2025) of Gemini 2.0 Flash Thinking",
							"version": "2.0-exp-01-21",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 1048576,
							"outputTokenLimit": 65536,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-2.0-flash-exp",
							"displayName": "Gemini 2.0 Flash Experimental",
							"description": "Gemini 2.0 Flash Experimental",
							"version": "2.0",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 1048576,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens",
								"bidiGenerateContent"
							]
						},
						{
							"name": "models/gemini-2.0-flash-thinking-exp-1219",
							"displayName": "Gemini 2.0 Flash Thinking Experimental",
							"description": "Gemini 2.0 Flash Thinking Experimental",
							"version": "2.0",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 1048576,
							"outputTokenLimit": 65536,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						}
					]
				}
			}
		""".trimIndent()
		mockApi(mockResponse).getModelsAi(
			accessToken = """
			eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwNzk4ODNhZS1lYjE5LTRmZGQtODhhYS04MjE5NDE3MjJkOWMiLCJpc3MiOiJzb2toYW55YWFyLmlyIiwic3ViIjoiMTYzYzRjYWYxYWQyMjNiYmRkODRlYTVhZTQ2OWVlMmY0ODJlZDI1MiIsImlhdCI6MTc0MTY2Mjk0MiwibmJmIjoxNzQxNjYyOTQyLCJleHAiOjE3NDE3NDkzNDIsInNjb3BlIjoiY2xpZW50In0.D4Gq02SHTIxL-ob56l_KEPG2-skMQjbkEUw2D3NxNqJ1xR270-wSkF2awtHfhv478laCxKIlwXhc0FnYeKTJZw
		""".trimIndent()
		).models shouldHaveSize 2
	}

	@Test
	fun `(get ai models info) should returns valid ai models list for each category info (mock)`() =
		runTest {
			val mockResponse = """
			{
				"available_models": {
					"released": [
						{
							"name": "models/gemini-1.5-flash-8b-latest",
							"displayName": "Gemini 1.5 Flash-8B Latest",
							"description": "Alias that points to the most recent production (non-experimental) release of Gemini 1.5 Flash-8B, our smallest and most cost effective Flash model, released in October of 2024.",
							"version": "001",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 1000000,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"createCachedContent",
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-1.5-flash-latest",
							"displayName": "Gemini 1.5 Flash Latest",
							"description": "Alias that points to the most recent production (non-experimental) release of Gemini 1.5 Flash, our fast and versatile multimodal model for scaling across diverse tasks.",
							"version": "001",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 1000000,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-1.5-pro-latest",
							"displayName": "Gemini 1.5 Pro Latest",
							"description": "Alias that points to the most recent production (non-experimental) release of Gemini 1.5 Pro, our mid-size multimodal model that supports up to 2 million tokens.",
							"version": "001",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 2000000,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						}
					],
					"preview": [
						{
							"name": "models/gemini-2.0-pro-exp",
							"displayName": "Gemini 2.0 Pro Experimental",
							"description": "Experimental release (February 5th, 2025) of Gemini 2.0 Pro",
							"version": "2.0",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 2097152,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-2.0-pro-exp-02-05",
							"displayName": "Gemini 2.0 Pro Experimental 02-05",
							"description": "Experimental release (February 5th, 2025) of Gemini 2.0 Pro",
							"version": "2.0",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 2097152,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-exp-1206",
							"displayName": "Gemini Experimental 1206",
							"description": "Experimental release (February 5th, 2025) of Gemini 2.0 Pro",
							"version": "2.0",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 2097152,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-2.0-flash-thinking-exp-01-21",
							"displayName": "Gemini 2.0 Flash Thinking Experimental 01-21",
							"description": "Experimental release (January 21st, 2025) of Gemini 2.0 Flash Thinking",
							"version": "2.0-exp-01-21",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 1048576,
							"outputTokenLimit": 65536,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						},
						{
							"name": "models/gemini-2.0-flash-exp",
							"displayName": "Gemini 2.0 Flash Experimental",
							"description": "Gemini 2.0 Flash Experimental",
							"version": "2.0",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 1048576,
							"outputTokenLimit": 8192,
							"supportedActions": [
								"generateContent",
								"countTokens",
								"bidiGenerateContent"
							]
						},
						{
							"name": "models/gemini-2.0-flash-thinking-exp-1219",
							"displayName": "Gemini 2.0 Flash Thinking Experimental",
							"description": "Gemini 2.0 Flash Thinking Experimental",
							"version": "2.0",
							"endpoints": null,
							"labels": null,
							"tunedModelInfo": {
								"baseModel": null,
								"createTime": null,
								"updateTime": null
							},
							"inputTokenLimit": 1048576,
							"outputTokenLimit": 65536,
							"supportedActions": [
								"generateContent",
								"countTokens"
							]
						}
					]
				}
			}
		""".trimIndent()
			mockApi(mockResponse).getModelsAi(
				accessToken = """
			eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwNzk4ODNhZS1lYjE5LTRmZGQtODhhYS04MjE5NDE3MjJkOWMiLCJpc3MiOiJzb2toYW55YWFyLmlyIiwic3ViIjoiMTYzYzRjYWYxYWQyMjNiYmRkODRlYTVhZTQ2OWVlMmY0ODJlZDI1MiIsImlhdCI6MTc0MTY2Mjk0MiwibmJmIjoxNzQxNjYyOTQyLCJleHAiOjE3NDE3NDkzNDIsInNjb3BlIjoiY2xpZW50In0.D4Gq02SHTIxL-ob56l_KEPG2-skMQjbkEUw2D3NxNqJ1xR270-wSkF2awtHfhv478laCxKIlwXhc0FnYeKTJZw
		""".trimIndent()
			).models["released"]?.size shouldBe 3
		}
}

class SokhanYarAiGenerateApiTests {
	@Test
	fun `(generate motivation text) should returns valid response`() = runTest {
		val response = SokhanYarApi().generateMotivationText(
			accessToken = """
			eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwNzk4ODNhZS1lYjE5LTRmZGQtODhhYS04MjE5NDE3MjJkOWMiLCJpc3MiOiJzb2toYW55YWFyLmlyIiwic3ViIjoiMTYzYzRjYWYxYWQyMjNiYmRkODRlYTVhZTQ2OWVlMmY0ODJlZDI1MiIsImlhdCI6MTc0MTY2Mjk0MiwibmJmIjoxNzQxNjYyOTQyLCJleHAiOjE3NDE3NDkzNDIsInNjb3BlIjoiY2xpZW50In0.D4Gq02SHTIxL-ob56l_KEPG2-skMQjbkEUw2D3NxNqJ1xR270-wSkF2awtHfhv478laCxKIlwXhc0FnYeKTJZw
		""".trimIndent(), request = GenerateMotivationTextRequest()
		)
		response.motivationText shouldHaveMinLength 5
	}

	@Test
	fun `(generate motivation text stream) should returns valid stream response`() = runTest {
		val response = SokhanYarApi().generateMotivationTextStream(
			accessToken = """
			eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwNzk4ODNhZS1lYjE5LTRmZGQtODhhYS04MjE5NDE3MjJkOWMiLCJpc3MiOiJzb2toYW55YWFyLmlyIiwic3ViIjoiMTYzYzRjYWYxYWQyMjNiYmRkODRlYTVhZTQ2OWVlMmY0ODJlZDI1MiIsImlhdCI6MTc0MTY2Mjk0MiwibmJmIjoxNzQxNjYyOTQyLCJleHAiOjE3NDE3NDkzNDIsInNjb3BlIjoiY2xpZW50In0.D4Gq02SHTIxL-ob56l_KEPG2-skMQjbkEUw2D3NxNqJ1xR270-wSkF2awtHfhv478laCxKIlwXhc0FnYeKTJZw
		""".trimIndent(), request = GenerateMotivationTextRequest()
		)

		response.collect { it shouldHaveMinLength 1 }
	}

	@Test
	fun `(analyze voice file (direct)) should returns unsupported file type response`() = runTest {
		val filePath = "C:\\Users\\saleh\\Downloads\\mohsaleh04.jpeg"
		val testFile = File(filePath)
		val testFileName = testFile.name
		var testFileBA: ByteArray? = null
		with(testFile) {
			testFileBA = testFile.readBytes()
		}
		shouldThrow<ApiClient.ApiError> {
			SokhanYarApi().analyzeTreatmentVoiceMediaFile(
				accessToken = """
			eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwNzk4ODNhZS1lYjE5LTRmZGQtODhhYS04MjE5NDE3MjJkOWMiLCJpc3MiOiJzb2toYW55YWFyLmlyIiwic3ViIjoiMTYzYzRjYWYxYWQyMjNiYmRkODRlYTVhZTQ2OWVlMmY0ODJlZDI1MiIsImlhdCI6MTc0MTY2Mjk0MiwibmJmIjoxNzQxNjYyOTQyLCJleHAiOjE3NDE3NDkzNDIsInNjb3BlIjoiY2xpZW50In0.D4Gq02SHTIxL-ob56l_KEPG2-skMQjbkEUw2D3NxNqJ1xR270-wSkF2awtHfhv478laCxKIlwXhc0FnYeKTJZw
		""".trimIndent(), fileByteArray = testFileBA!!, fileName = testFileName, fileContentType = testFile.getMimeType() ?: return@runTest
			) { bytesSent: Long, contentLength: Long? -> {
					println("Sending file ${testFile.name} --> sent: $bytesSent | whole: $contentLength | ${(bytesSent / (contentLength ?: 1)) * 100}")
				}
			}
		}
	}
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

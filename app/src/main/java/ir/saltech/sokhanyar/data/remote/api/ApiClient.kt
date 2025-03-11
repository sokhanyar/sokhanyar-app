package ir.saltech.sokhanyar.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.compression.compress
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.readUTF8Line
import ir.saltech.sokhanyar.data.remote.response.ErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

sealed class ApiClient(
	open val baseUrl: String,
	engine: HttpClientEngine,
) {
	val apiClient: HttpClient = HttpClient(engine) {
		install(HttpTimeout) { requestTimeoutMillis = 300_000 }
		install(ContentEncoding) {
			deflate(1.0f)
			gzip(0.7f)
		}
		install(ContentNegotiation) { json() }
		install(Logging) { level = LogLevel.ALL }
	}

	class OneShot(override val baseUrl: String, engine: HttpClientEngine) :
		ApiClient(baseUrl, engine) {
		suspend inline fun <reified T> get(
			endpoint: String,
			authToken: String? = null,
			tokenType: String = "bearer",

			): T {
			print("")
			val response = apiClient.get("$baseUrl/$endpoint") {
				headers {
					if (authToken != null) {
						append(HttpHeaders.Authorization, "${tokenType.lowercase()} $authToken")
					}
					append(HttpHeaders.Accept, "application/json")
				}
			}
			when (response.status.value) {
				in 200..299 -> {
					return response.body()
				}

				else -> {
					throw ApiError((response.body() as ErrorResponse).detail.message)
				}
			}
		}

		suspend inline fun <reified T, reified K> get(
			endpoint: String,
			body: K,
			authToken: String? = null,
			tokenType: String = "bearer",
		): T {
			val response = apiClient.get("$baseUrl/$endpoint") {
				headers {
					if (authToken != null) {
						append(HttpHeaders.Authorization, "${tokenType.lowercase()} $authToken")
					}
					append(HttpHeaders.Accept, "application/json")
				}
				if (body != null) {
					contentType(ContentType.Application.Json)
					setBody(body)
				}
			}
			when (response.status.value) {
				in 200..299 -> {
					return response.body()
				}

				else -> {
					throw ApiError((response.body() as ErrorResponse).detail.message)
				}
			}
		}

		suspend inline fun <reified T, reified K> post(
			endpoint: String,
			body: K,
			authToken: String? = null,
			tokenType: String = "bearer",
		): T {
			val response = apiClient.post("$baseUrl/$endpoint") {
				headers {
					if (authToken != null) {
						append(HttpHeaders.Authorization, "${tokenType.lowercase()} $authToken")
					}
					append(HttpHeaders.Accept, "application/json")
				}
				contentType(ContentType.Application.Json)
				setBody(body)
			}
			when (response.status.value) {
				in 200..299 -> {
					return response.body()
				}

				else -> {
					throw ApiError((response.body() as ErrorResponse).detail.message)
				}
			}
		}

		suspend inline fun <reified T, reified K> delete(
			endpoint: String,
			body: K,
			authToken: String? = null,
			tokenType: String = "bearer",
		): T {
			val response = apiClient.delete("$baseUrl/$endpoint") {
				headers {
					if (authToken != null) {
						append(HttpHeaders.Authorization, "${tokenType.lowercase()} $authToken")
					}
					append(HttpHeaders.Accept, "application/json")
				}
				if (body != null) {
					contentType(ContentType.Application.Json)
					setBody(body)
				}
			}
			when (response.status.value) {
				in 200..299 -> {
					return response.body()
				}

				else -> {
					throw ApiError((response.body() as ErrorResponse).detail.message)
				}
			}
		}

		suspend inline fun <reified T, reified K> put(
			endpoint: String,
			body: K,
			authToken: String? = null,
			tokenType: String = "bearer",
		): T {
			val response = apiClient.put("$baseUrl/$endpoint") {
				headers {
					if (authToken != null) {
						append(HttpHeaders.Authorization, "${tokenType.lowercase()} $authToken")
					}
					append(HttpHeaders.Accept, "application/json")
				}
				if (body != null) {
					contentType(ContentType.Application.Json)
					setBody(body)
				}
			}
			when (response.status.value) {
				in 200..299 -> {
					return response.body()
				}

				else -> {
					throw ApiError((response.body() as ErrorResponse).detail.message)
				}
			}
		}

		suspend inline fun <reified T, reified K> patch(
			endpoint: String,
			body: K,
			authToken: String? = null,
			tokenType: String = "bearer",
		): T {
			val response = apiClient.patch("$baseUrl/$endpoint") {
				headers {
					if (authToken != null) {
						append(HttpHeaders.Authorization, "${tokenType.lowercase()} $authToken")
					}
					append(HttpHeaders.Accept, "application/json")
				}
				if (body != null) {
					contentType(ContentType.Application.Json)
					setBody(body)
				}
			}
			when (response.status.value) {
				in 200..299 -> {
					return response.body()
				}

				else -> {
					throw ApiError((response.body() as ErrorResponse).detail.message)
				}
			}
		}

		suspend inline fun <reified T> uploadFile(
			endpoint: String,
			authToken: String,
			fileByteArray: ByteArray,
			fileName: String,
			fileContentType: String,
			tokenType: String = "bearer",
			crossinline progressListener: (Long, Long?) -> Unit,
		): Result<T> = runCatching {
			if (fileByteArray.isEmpty()) throw Exception("File byte array must not be empty!")
			val response = apiClient.post("$baseUrl/$endpoint") {
				headers {
					append(HttpHeaders.Authorization, "${tokenType.lowercase()} $authToken")
					append(HttpHeaders.Accept, "application/json")
				}
				contentType(ContentType.MultiPart.FormData)
				compress("gzip")
				setBody(
					MultiPartFormDataContent(
						formData {
							append("description", "Practical Voice File")
							append("file", value = fileByteArray, headers = Headers.build {
								append(HttpHeaders.ContentType, fileContentType)
								append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
							})
						}, boundary = "WebAppBoundary"
					)
				)
				onUpload { bytesSentTotal, contentLength ->
					progressListener(bytesSentTotal, contentLength)
				}
			}
			when (response.status.value) {
				in 200..299 -> {
					return response.body()
				}

				else -> {
					throw ApiError((response.body() as ErrorResponse).detail.message)
				}
			}
		}
	}

	class ServerSideEvents(override val baseUrl: String, engine: HttpClientEngine) :
		ApiClient(baseUrl, engine) {
		object Identifier {
			const val DATA = "data: "
			const val ERROR = "error: "
		}

		inline fun <reified T> get(
			endpoint: String,
			authToken: String? = null,
			tokenType: String = "bearer",
		): Flow<T> = flow {
			apiClient.get("$baseUrl$endpoint") {
				headers {
					if (authToken != null) {
						append(HttpHeaders.Authorization, "${tokenType.lowercase()} $authToken")
					}
					append(HttpHeaders.Accept, "text/event-stream")
				}
			}.bodyAsChannel().apply {
				while (!isClosedForRead) {
					val line = readUTF8Line() ?: continue
					if (line.startsWith(Identifier.DATA)) {
						// Parse response
						emit(Json.decodeFromString<T>(line.removePrefix(Identifier.DATA).trim()))
					}
					if (line.startsWith(Identifier.ERROR)) {
						// Parse Error
						try {
							throw ApiError(
								Json.decodeFromString<ErrorResponse>(
									line.removePrefix(
										Identifier.ERROR
									).trim()
								).detail.message
							)
						} catch (_: SerializationException) {
							throw ApiError(
								line.removePrefix(
									Identifier.ERROR
								).trim()
							)
						}
					}
				}
			}
		}

		inline fun <reified T, reified K> get(
			endpoint: String,
			body: K,
			authToken: String? = null,
			tokenType: String = "bearer",
		): Flow<T> = flow {
			apiClient.get("$baseUrl$endpoint") {
				headers {
					if (authToken != null) {
						append(HttpHeaders.Authorization, "${tokenType.lowercase()} $authToken")
					}
					append(HttpHeaders.Accept, "text/event-stream")
				}
				if (body != null) {
					contentType(ContentType.Application.Json)
					setBody(body)
				}
			}.bodyAsChannel().apply {
				while (!isClosedForRead) {
					val line = readUTF8Line() ?: continue
					if (line.startsWith(Identifier.DATA)) {
						// Parse response
						emit(Json.decodeFromString<T>(line.removePrefix(Identifier.DATA).trim()))
					}
					if (line.startsWith(Identifier.ERROR)) {
						// Parse Error
						try {
							throw ApiError(
								Json.decodeFromString<ErrorResponse>(
									line.removePrefix(
										Identifier.ERROR
									).trim()
								).detail.message
							)
						} catch (_: SerializationException) {
							throw ApiError(
								line.removePrefix(
									Identifier.ERROR
								).trim()
							)
						}
					}
				}
			}
		}

		inline fun <reified T, reified K> post(
			endpoint: String,
			body: K,
			authToken: String? = null,
			tokenType: String = "bearer",
		): Flow<T> = flow {
			apiClient.post("$baseUrl$endpoint") {
				headers {
					if (authToken != null) {
						append(HttpHeaders.Authorization, "${tokenType.lowercase()} $authToken")
					}
					append(HttpHeaders.Accept, "text/event-stream")
				}
				if (body != null) {
					contentType(ContentType.Application.Json)
					setBody(body)
				}
			}.bodyAsChannel().apply {
				while (!isClosedForRead) {
					val line = readUTF8Line() ?: continue
					if (line.startsWith(Identifier.DATA)) {
						// Parse response
						emit(Json.decodeFromString<T>(line.removePrefix(Identifier.DATA).trim()))
					}
					if (line.startsWith(Identifier.ERROR)) {
						// Parse Error
						try {
							throw ApiError(
								Json.decodeFromString<ErrorResponse>(
									line.removePrefix(
										Identifier.ERROR
									).trim()
								).detail.message
							)
						} catch (_: SerializationException) {
							throw ApiError(
								line.removePrefix(
									Identifier.ERROR
								).trim()
							)
						}
					}
				}
			}
		}

		inline fun <reified T> uploadFile(
			endpoint: String,
			authToken: String,
			fileByteArray: ByteArray,
			fileName: String,
			tokenType: String = "bearer",
			crossinline progressListener: (Long, Long?) -> Unit,
		): Flow<T> = flow {
			if (fileByteArray.isEmpty()) throw Exception("File byte array must not be empty!")
			apiClient.post("$baseUrl/$endpoint") {
				headers {
					append(HttpHeaders.Authorization, "${tokenType.lowercase()} $authToken")
					append(HttpHeaders.Accept, "application/json")
				}
				contentType(ContentType.MultiPart.FormData)
				compress("gzip")
				setBody(
					MultiPartFormDataContent(
						formData {
							append(key = "file", value = fileByteArray, headers = Headers.build {
								append(HttpHeaders.ContentType, "multipart")
								append(HttpHeaders.ContentDisposition, "filename=$fileName")
							})
						}, boundary = "AndroidAppBoundary"
					)
				)
				onUpload { bytesSentTotal, contentLength ->
					progressListener(bytesSentTotal, contentLength)
				}
			}.bodyAsChannel().apply {
				while (!isClosedForRead) {
					val line = readUTF8Line() ?: continue
					if (line.startsWith(Identifier.DATA)) {
						// Parse response
						emit(Json.decodeFromString<T>(line.removePrefix(Identifier.DATA).trim()))
					}
					if (line.startsWith(Identifier.ERROR)) {
						// Parse Error
						try {
							throw ApiError(
								Json.decodeFromString<ErrorResponse>(
									line.removePrefix(
										Identifier.ERROR
									).trim()
								).detail.message
							)
						} catch (_: SerializationException) {
							throw ApiError(
								line.removePrefix(
									Identifier.ERROR
								).trim()
							)
						}
					}
				}
			}
		}
	}

	class ApiError(message: String? = null, cause: Throwable? = null) : Error(message, cause)
}

package ir.saltech.sokhanyar.util

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.readUTF8Line
import ir.saltech.sokhanyar.BaseApplication
import ir.saltech.sokhanyar.api.Post
import ir.saltech.sokhanyar.model.api.PromptInfo
import ir.saltech.sokhanyar.model.api.ResponseObject
import ir.saltech.sokhanyar.model.data.general.AuthInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StreamGenerateText() {
	var client = HttpClient(CIO) {
		install(ContentNegotiation) {
			json()
		}
	}


	@Post("/v1/asdfe")
	fun connect(endpoint: String,  authToken: String): Flow<String> = flow {
		client.post("${BaseApplication.Constants.SOKHANYAR_BASE_URL}$endpoint") {
			headers {
				append(HttpHeaders.Authorization, authToken)
				append(HttpHeaders.Accept, "text/event-stream")
			}
			contentType(ContentType.Application.Json)
			setBody(PromptInfo())
		}.bodyAsChannel().apply {
			while (!isClosedForRead) {
				val line = readUTF8Line() ?: continue
				if (line.startsWith("data:")) {
					emit(line.removePrefix("data:").trim())
				}
			}
		}
	}

	suspend fun doSignIn(authInfo: AuthInfo): ResponseObject = client.post("https://api.sokhanyaar.ir/api/v1/auth/send-otp") {
		headers {
			append(HttpHeaders.Accept, "application/json")
		}
		contentType(ContentType.Application.Json)
		setBody(authInfo)
	}.body(typeInfo = TypeInfo(ResponseObject::class))
}
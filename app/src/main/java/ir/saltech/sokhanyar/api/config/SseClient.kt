package ir.saltech.sokhanyar.api.config

import io.ktor.client.HttpClient
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
import io.ktor.utils.io.readUTF8Line
import ir.saltech.sokhanyar.BaseApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// TODO: in the next sprint add sse support
class StreamGenerateText() {
	var client = HttpClient(CIO) {
		install(ContentNegotiation) {
			json()
		}
	}

	inline fun <reified T> connect(endpoint: String,  authToken: String, bodyContent: T): Flow<String> = flow {
		client.post("${BaseApplication.Constants.SOKHANYAR_BASE_URL}$endpoint") {
			headers {
				append(HttpHeaders.Authorization, authToken)
				append(HttpHeaders.Accept, "text/event-stream")
			}
			contentType(ContentType.Application.Json)
			setBody(bodyContent)
		}.bodyAsChannel().apply {
			while (!isClosedForRead) {
				val line = readUTF8Line() ?: continue
				if (line.startsWith("data:")) {
					emit(line.removePrefix("data:").trim())
				}
			}
		}
	}
}
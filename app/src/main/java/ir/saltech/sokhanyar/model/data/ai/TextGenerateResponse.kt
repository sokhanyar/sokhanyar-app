package ir.saltech.sokhanyar.model.data.ai

import kotlinx.serialization.Serializable


@Serializable
data class Candidate(
	val avgLogprobs: Double,
	val content: Content,
	val finishMessage: String,
	val finishReason: String,
	val logprobsResult: Double,
)

@Serializable
data class Content(
	val parts: List<Part>,
	val role: String,
)

@Serializable
data class Part(
//    val codeExecutionResult: Any,
//    val executableCode: Any,
//    val fileData: Any,
//    val functionCall: Any,
//    val functionResponse: Any,
//    val inlineData: Any,
	val text: String,
	val thought: String?,
	val videoMetadata: String?,
)

@Serializable
data class UsageMetadata(
	val cachedContentTokenCount: Int?,
	val candidatesTokenCount: Int,
	val promptTokenCount: Int,
	val totalTokenCount: Int,
)

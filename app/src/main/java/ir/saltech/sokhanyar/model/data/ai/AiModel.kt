package ir.saltech.sokhanyar.model.data.ai

import kotlinx.serialization.Serializable


@Serializable
data class AiModel(
	val name: String,
	val version: String,
	val description: String,
	val displayName: String,
	val inputTokenLimit: Int,
	val outputTokenLimit: Int,
	val supportedActions: List<String>,
)

@Serializable
data class AvailableModels(
	val models: Map<String, List<AiModel>>
)

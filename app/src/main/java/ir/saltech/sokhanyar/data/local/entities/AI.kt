package ir.saltech.sokhanyar.data.local.entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity
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
	val models: Map<String, List<AiModel>>,
)

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
	val endpoints: List<String>?,
	val labels: List<String>?,
	val inputTokenLimit: Int,
	val outputTokenLimit: Int,
	val supportedActions: List<String>,
	val tunedModelInfo: TunedModelInfo?
)

@Serializable
data class TunedModelInfo(
	val baseModel: String?,
	val createTime: String?,
	val updateTime: String?
)

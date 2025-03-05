package ir.saltech.sokhanyar.model.data.treatment.report

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CallsCount(
	@SerialName("group_calls_count")
	val groupCallsCount: Int? = null,
	@SerialName("supporting_p2p_calls_count")
	@Deprecated("it changed to peer calls count")
	val supportingP2PCallsCount: Int? = null,
	@SerialName("peer_calls_count")
    val peerCallsCount: Int? = supportingP2PCallsCount
)

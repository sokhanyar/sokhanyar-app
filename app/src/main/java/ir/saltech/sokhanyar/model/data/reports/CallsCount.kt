package ir.saltech.sokhanyar.model.data.reports

import com.google.gson.annotations.SerializedName

data class CallsCount(
    @SerializedName("group_calls_count")
    val groupCallsCount: Int? = null,
    @SerializedName("supporting_p2p_calls_count")
    val supportingP2PCallsCount: Int? = null
    // TODO: apply this supporting calls count variables into FastAPI
)

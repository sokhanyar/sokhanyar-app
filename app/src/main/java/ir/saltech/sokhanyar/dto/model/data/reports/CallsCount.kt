package ir.saltech.sokhanyar.dto.model.data.reports

import com.google.gson.annotations.SerializedName

data class CallsCount(
    @SerializedName("group_calls_count")
    val groupCallsCount: Int? = null,
    @SerializedName("adult_support_calls_count")
    val adultSupportCallsCount: Int? = null,
    @SerializedName("teen_support_calls_count")
    val teenSupportCallsCount: Int? = null
)

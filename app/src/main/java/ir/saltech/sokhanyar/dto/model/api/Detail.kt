package ir.saltech.sokhanyar.dto.model.api

import com.google.gson.annotations.SerializedName

data class Detail(
    @SerializedName("@type")
    val type: String,
    val domain: String,
    val metadata: Metadata,
    val reason: String
)
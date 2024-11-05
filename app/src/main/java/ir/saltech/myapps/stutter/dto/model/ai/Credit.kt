package ir.saltech.myapps.stutter.dto.model.ai

import com.google.gson.annotations.SerializedName

data class Credit(
    val limit: Double,
    @SerializedName("remaining_irt")
    val remainingIrt: Double,
    @SerializedName("remaining_unit")
    val remainingUnit: Double,
    @SerializedName("total_unit")
    val totalUnit: Double
)
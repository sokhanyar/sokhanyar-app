package ir.saltech.myapps.stutter.dto.model.data.reports

import com.google.gson.annotations.SerializedName

data class MethodUsage(
    @SerializedName("at_home")
    val atHome: Int? = null,
    @SerializedName("at_school")
    val atSchool: Int? = null,
    @SerializedName("with_others")
    val withOthers: Int? = null,
    @SerializedName("with_family")
    val withFamily: Int? = null
)

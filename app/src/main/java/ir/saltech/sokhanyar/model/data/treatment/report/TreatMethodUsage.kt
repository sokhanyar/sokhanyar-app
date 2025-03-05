package ir.saltech.sokhanyar.model.data.treatment.report

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TreatMethodUsage(
    @SerialName("at_home")
    val atHome: Int? = null,
    @SerialName("at_school")
    val atSchool: Int? = null,
    @SerialName("with_others")
    val withOthers: Int? = null,
    @SerialName("with_family")
    val withFamily: Int? = null
)

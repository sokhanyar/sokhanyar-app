package ir.saltech.sokhanyar.model.data.treatment.report

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VoicesProperties(
    @SerialName("challenges_count")
    val challengesCount: Int? = null,
    @SerialName("sum_of_challenges_duration")
    val sumOfChallengesDuration: Int? = null,
    @SerialName("conferences_days_count")
    val conferenceDaysCount: Int? = null,
    @SerialName("sum_of_conferences_duration")
    val sumOfConferencesDuration: Int? = null

)

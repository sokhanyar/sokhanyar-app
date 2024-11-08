package ir.saltech.myapps.stutter.dto.model.data.general

data class User(
    val id: Int = 0,
    val name: String? = null,
    val age: Int? = null,
    val yearOfStartStuttering: Int? = null,
    val timesOfTherapy: Int? = null,
    val stutteringType: String? = null,
    val tirednessLevel: String? = null,
    val previousStutteringSeverity: Int? = null,
    val currentStutteringSeverity: Int? = null,
    val dailyTherapyTime: String? = null,
    val currentTherapyDuration: Int? = null,
    val therapyStatus: String? = null,
    val therapyMethod: String? = null,
    val stutteringSituations: String? = null,
    val emotionalImpact: String? = null,
    val therapyGoals: String? = null,
    val previousTherapies: String? = null,
    val familyHistory: String? = null,
    val coOccurringConditions: String? = null,
    val supportSystems: String? = null,
    val escapingFromSpeechSituationsLevel: String? = null,
    val escapingFromStutteredWordLevel: String? = null
)

package ir.saltech.myapps.stutter.dto.model.ui

interface ChatActionWantedListener {
    fun onSendWanted()

    fun onStartOverWanted()

    fun onScheduledSendWanted()
}
package ir.saltech.sokhanyar.dto.model.ui

interface ChatActionWantedListener {
    fun onSendWanted()

    fun onStartOverWanted()

    fun onScheduledSendWanted()
}
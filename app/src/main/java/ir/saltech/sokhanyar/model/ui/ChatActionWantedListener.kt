package ir.saltech.sokhanyar.model.ui

interface ChatActionWantedListener {
    fun onSendWanted()

    fun onStartOverWanted()

    fun onScheduledSendWanted()
}
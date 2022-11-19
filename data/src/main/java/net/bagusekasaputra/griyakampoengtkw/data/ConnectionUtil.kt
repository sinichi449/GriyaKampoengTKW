package net.bagusekasaputra.griyakampoengtkw.data

import java.util.*

object ConnectionUtil {

    var serverTimeout = 3000L

    fun createRequestTimeout(gotResult: Boolean, onTimeOut: () -> Unit) {
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                timer.cancel()

                if (!gotResult) {
                    onTimeOut()
                }
            }
        }

        timer.schedule(timerTask, serverTimeout)
    }
}
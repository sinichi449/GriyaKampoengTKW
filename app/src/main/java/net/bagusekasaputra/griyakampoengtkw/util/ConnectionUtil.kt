package net.bagusekasaputra.griyakampoengtkw.util

import java.util.*

object ConnectionUtil {

    fun createRequestTimeout(gotResult: Boolean, onTimeOut: () -> Unit, timeoutMillis: Long) {
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                timer.cancel()

                if (!gotResult) {
                    onTimeOut()
                }
            }
        }

        timer.schedule(timerTask, timeoutMillis)
    }
}
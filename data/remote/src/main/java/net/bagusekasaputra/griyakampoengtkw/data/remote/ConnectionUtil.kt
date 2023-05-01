package net.bagusekasaputra.griyakampoengtkw.data.remote

import java.util.Timer
import java.util.TimerTask

object ConnectionUtil {

    // Here I have increased the timeout because I have setup the offline mode,
    // so only when the user has a really broken connectivity the system then
    // emitting from local.
    var serverTimeout = 5000L

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
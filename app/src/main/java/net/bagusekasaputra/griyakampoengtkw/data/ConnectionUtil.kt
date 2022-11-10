package net.bagusekasaputra.griyakampoengtkw.data

import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes
import java.util.*

object ConnectionUtil {

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

        timer.schedule(timerTask, GriyaNodes.SERVER_TIMEOUT_MILLIS)
    }
}
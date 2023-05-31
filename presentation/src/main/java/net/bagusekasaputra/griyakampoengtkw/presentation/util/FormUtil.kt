package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.app.Activity
import android.content.Intent

object FormUtil {

    fun sendResultAndExit(activity: Activity, resultCode: Int, dataToSend: Intent?) {
        with(activity) {
            setResult(resultCode, dataToSend)
            finish()
        }
    }

}
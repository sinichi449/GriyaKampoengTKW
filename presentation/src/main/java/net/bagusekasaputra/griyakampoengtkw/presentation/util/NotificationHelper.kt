package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.receiver.ProgressReceiver

object NotificationUtil {

    fun createNotification(
        activity: FragmentActivity,
        title: String,
        content: String,
        finished: Boolean = true,
    ) {
        val intent = Intent(activity.applicationContext, ProgressReceiver::class.java).apply {
            this.putExtra("INTENT_NOTIFICATION_IS_FINISHED", finished)
            this.putExtra("INTENT_NOTIFICATION_TITLE", title)
            this.putExtra("INTENT_NOTIFICATION_CONTENT", content)
        }

        activity.sendBroadcast(intent)
    }
}
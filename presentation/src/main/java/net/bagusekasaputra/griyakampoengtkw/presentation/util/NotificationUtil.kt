package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.receiver.ProgressReceiver

object NotificationUtil {
    const val CHANNEL_NAME = "GriyaKampoengTKW"
    const val PROGRESS_CHANNEL = "GktTaskProgress"
    const val UPLOAD_INDEN_BOOKING_ID = 99

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

    fun createNotification(
        context: Context,
        title: String,
        text: String,
        channelId: String = PROGRESS_CHANNEL
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

        createChannel(notificationManager, channelId)

        val notification = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_baseline_hourglass_top_24)
            setContentTitle(title)
            setContentText(text)
        }.build()

        notificationManager.notify(99, notification)
    }

    private fun createChannel(notificationManagerCompat: NotificationManagerCompat, id: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "GriyaKampoengTKW"
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            notificationManagerCompat.createNotificationChannel(channel)
        }
    }
}
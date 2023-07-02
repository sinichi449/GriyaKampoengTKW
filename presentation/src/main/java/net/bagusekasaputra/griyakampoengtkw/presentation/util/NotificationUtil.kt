package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.receiver.ProgressReceiver

object NotificationUtil {
    const val CHANNEL_NAME = "GriyaKampoengTKW"
    const val PROGRESS_CHANNEL = "GktTaskProgress"
    const val DEFAULT_CHANNEL = "DefaultChannel"
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

    fun FragmentActivity.createNotification(
        channelId: String = DEFAULT_CHANNEL,
        notificationId: Int = 7613,
        content: NotificationCompat.Builder.() -> Unit,
    ) {
        val context = this.applicationContext
        val notificationManager = NotificationManagerCompat.from(context)

        createChannel(notificationManager, channelId)

        val notification = NotificationCompat.Builder(context, channelId).apply {
            content(this)
        }.build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(notificationId, notification)
    }

    fun createNotification(
        activity: FragmentActivity,
        title: String,
        text: String,
        @DrawableRes icon: Int = R.drawable.ic_baseline_hourglass_top_24,
        channelId: String = PROGRESS_CHANNEL,
        notificationId: Int = 7613,
    ) {
        val context = activity.applicationContext
        val notificationManager = NotificationManagerCompat.from(context)

        createChannel(notificationManager, channelId)

        val notification = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(icon)
            setContentTitle(title)
            setContentText(text)
        }.build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(notificationId, notification)
    }

    private fun createChannel(notificationManagerCompat: NotificationManagerCompat, id: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "GriyaKampoengTKW"
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            notificationManagerCompat.createNotificationChannel(channel)
        }
    }
}
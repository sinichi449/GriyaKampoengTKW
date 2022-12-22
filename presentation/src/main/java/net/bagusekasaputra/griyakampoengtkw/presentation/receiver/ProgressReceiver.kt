package net.bagusekasaputra.griyakampoengtkw.presentation.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R

@AndroidEntryPoint
class ProgressReceiver: BroadcastReceiver() {

    private val CHANNEL_ID = "progress_notification"
    private lateinit var notificationManager: NotificationManager

    override fun onReceive(ctx: Context?, intent: Intent?) {
        if (ctx != null) {
            val title = intent?.extras?.getString("INTENT_NOTIFICATION_TITLE")
            val contentMsg = intent?.extras?.getString("INTENT_NOTIFICATION_CONTENT")
            val isFinished = intent?.extras?.getBoolean("INTENT_NOTIFICATION_IS_FINISHED")

            createNotificationChannel(ctx)

            val notification = NotificationCompat.Builder(ctx, CHANNEL_ID).apply {
                this.setContentTitle(title ?: "Null")
                this.setContentText(contentMsg ?: "Null description")
                isFinished?.let {
                    if (it.not()) {
                        this.setProgress(0, 0, true)
                        this.setSmallIcon(R.drawable.ic_baseline_hourglass_top_24)
                    } else {
                        this.setSmallIcon(R.drawable.ic_baseline_check_circle_18)
                    }
                }
                this.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                this.color = ContextCompat.getColor(ctx, com.google.android.material.R.color.design_default_color_primary)
            }.build()
            notificationManager.notify(1, notification)
        }
    }

    private fun createNotificationChannel(ctx: Context) {
        notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appName = ctx.getString(R.string.app_name)
            val channel = NotificationChannel(CHANNEL_ID, appName, NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(channel)
        }
    }
}
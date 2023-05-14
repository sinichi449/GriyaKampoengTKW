@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.presentation.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.legacy.content.WakefulBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.activities.MainActivity
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes

@AndroidEntryPoint
class AlarmReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val mTitle = intent?.extras?.getString(GriyaNodes.INTENT_EXTRA_ALARM_TITLE)
        val mContent = intent?.extras?.getString(GriyaNodes.INTENT_EXTRA_ALARM_CONTENT)

        if (context != null) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            val mChannelId = "griya_kampoeng_tkw"
            if (Build.VERSION.SDK_INT > 26) {
                val channel = NotificationChannel(mChannelId,
                    "simsalabim",
                    NotificationManager.IMPORTANCE_HIGH)
                    .apply {
                        enableVibration(true)
                        enableLights(true)
                        lightColor = Color.CYAN
                    }
                notificationManager.createNotificationChannel(channel)
            }

            // Create pending intent
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    context,
                    111,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    context,
                    111,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            // Start notification
            val mNotification = NotificationCompat.Builder(context, mChannelId).apply {
                setSmallIcon(R.drawable.ic_baseline_attach_money_24)
                setLargeIcon(BitmapFactory.decodeResource(context.resources,
                    R.drawable.kampoeng_tkw))
                setContentTitle(mTitle)
                setContentText(mContent)
                setOnlyAlertOnce(true)
                setContentIntent(pendingIntent)
            }.build()

            notificationManager.notify(1234, mNotification)
        } else {
            Log.d("DEBUG_ME", "onReceive: Context and Reminder is null")
        }
    }
}
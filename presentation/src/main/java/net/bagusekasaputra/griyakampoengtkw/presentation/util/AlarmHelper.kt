package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import net.bagusekasaputra.griyakampoengtkw.presentation.receiver.AlarmReceiver
import java.util.*

class AlarmHelper(private val context: Context) {
    private val milMonth = 2592000000L;

    private val mAlarmManager = context.getSystemService(Context.ALARM_SERVICE)
            as AlarmManager

    fun setMonthyRepeatAlarm(calendar: Calendar, pengingat: Pengingat) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(GriyaNodes.INTENT_EXTRA_ALARM_TITLE, pengingat.title)
            putExtra(GriyaNodes.INTENT_EXTRA_ALARM_CONTENT, pengingat.content)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            pengingat.id?.toInt() ?: 30,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        mAlarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            milMonth,
            pendingIntent,
        )
    }

    fun cancelAlarm(id: Long?) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id?.toInt() ?: 30,
            Intent(context, AlarmReceiver::class.java),
            0
        )
        mAlarmManager.cancel(pendingIntent)
    }
}
package net.thebookofcode.www.amlnotes

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper

class NotificationHelper(context: Context?) : ContextWrapper(context) {
    var manager: NotificationManager? = null
        get() {
            if (field == null) {
                field = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return field
        }
        private set

    fun getNotification1(title: String?, body: String?): Notification.Builder {
        return Notification.Builder(applicationContext, PRIMARY_CHANNEL)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(smallIcon)
            .setAutoCancel(true)
    }

    fun notify(id: Int, notification: Notification.Builder) {
        manager!!.notify(id, notification.build())
    }

    private val smallIcon: Int
        private get() = R.drawable.stat_notify_chat

    companion object {
        const val PRIMARY_CHANNEL = "default"
    }

    init {
        val channel = NotificationChannel(
            PRIMARY_CHANNEL,
            "AML Notes Alarm",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Your Alarm is ringing"
        manager!!.createNotificationChannel(channel)
    }
}

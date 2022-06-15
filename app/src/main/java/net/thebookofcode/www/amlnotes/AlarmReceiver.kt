package net.thebookofcode.www.amlnotes

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        var nb: Notification.Builder? = null
        nb = notificationHelper.getNotification1("AML Notes Alarm", "Your Alarm is ringing")
        notificationHelper.notify(1, nb)
    }
}

package uz.gita.mysensor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build

/**
 *   Created by Jamik on 6/13/2023 ot 3:45 PM
 **/

class Notification constructor(private val context: Context) {

    companion object {
        const val Chanel_ID = "notification"
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val myChannel =
                NotificationChannel(Chanel_ID, "notify", importance)

            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(myChannel)
        }

    }

    private fun createNotification() {

    }

}
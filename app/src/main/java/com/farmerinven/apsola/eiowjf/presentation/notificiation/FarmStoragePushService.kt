package com.farmerinven.apsola.eiowjf.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.farmerinven.apsola.FarmStorageActivity
import com.farmerinven.apsola.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val FARM_STORAGE_CHANNEL_ID = "farm_storage_notifications"
private const val FARM_STORAGE_CHANNEL_NAME = "FarmStorage Notifications"
private const val FARM_STORAGE_NOT_TAG = "FarmStorage"

class FarmStoragePushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                farmStorageShowNotification(it.title ?: FARM_STORAGE_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                farmStorageShowNotification(it.title ?: FARM_STORAGE_NOT_TAG, it.body ?: "", data = null)
            }
        }

    }

    private fun farmStorageShowNotification(title: String, message: String, data: String?) {
        val farmStorageNotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FARM_STORAGE_CHANNEL_ID,
                FARM_STORAGE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            farmStorageNotificationManager.createNotificationChannel(channel)
        }

        val farmStorageIntent = Intent(this, FarmStorageActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val farmStoragePendingIntent = PendingIntent.getActivity(
            this,
            0,
            farmStorageIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val farmStorageNotification = NotificationCompat.Builder(this, FARM_STORAGE_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.farm_storage_noti_ic)
            .setAutoCancel(true)
            .setContentIntent(farmStoragePendingIntent)
            .build()

        farmStorageNotificationManager.notify(System.currentTimeMillis().toInt(), farmStorageNotification)
    }

}
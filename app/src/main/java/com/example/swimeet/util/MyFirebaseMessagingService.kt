package com.example.swimeet.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.swimeet.R
import com.example.swimeet.ui.CompetitionDetailActivity
import com.example.swimeet.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val PERMISSION_REQUEST_CODE = 123

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.notification?.let {
            if (it.title == "Nuevo comentario")
                sendNotificationNewComment(it.title, it.body, message.data)
            else if (it.title == "Nueva competición")
                sendNotificationNewCompetition(it.title!!, it.body, message.data)
        }
    }

    private fun sendNotificationNewCompetition(
        title: String,
        body: String?,
        data: Map<String, String>
    ) {
        if (data["userId"] == FirebaseUtil.getCurrentUserID()) {
            return
        }

        val latitude: String = getLatLong(data["location"])[0]
        val longitude: String = getLatLong(data["location"])[1]

        val intent = Intent(this, CompetitionDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("id", data["competitionId"])
            putExtra("name", data["name"])
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
            putExtra("type", data["type"])
            putExtra("link", data["link"])
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "default_channel"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Default Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun getLatLong(location: String?): List<String> {
        return location!!.split(",")
    }

    private fun sendNotificationNewComment(
        title: String?,
        message: String?,
        data: Map<String, String>
    ) {
        if (data["userId"] == FirebaseUtil.getCurrentUserID()) {
            return
        }

        val latitude: String = getLatLong(data["location"])[0]
        val longitude: String = getLatLong(data["location"])[1]

        val intent = Intent(this, CompetitionDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("id", data["competitionId"])
            putExtra("name", data["name"])
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
            putExtra("type", data["type"])
            putExtra("link", data["link"])
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "default_channel"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Default Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager.notify(0, notificationBuilder.build())

    }
}
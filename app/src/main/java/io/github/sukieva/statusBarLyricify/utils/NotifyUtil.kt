package io.github.sukieva.statusBarLyricify.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.NotificationCompat
import io.github.sukieva.statusBarLyricify.MyApp
import io.github.sukieva.statusBarLyricify.R
import io.github.sukieva.statusBarLyricify.ui.MainActivity


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
class NotifyUtil : ContextWrapper(MyApp.context) {

    init {
        val channel = NotificationChannel(PRIMARY_CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        getManager()!!.createNotificationChannel(channel)
    }

    private var manager: NotificationManager? = null

    private fun getManager(): NotificationManager? {
        if (manager == null) {
            manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager!!.cancel(1)
        }
        return manager
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setIntent(): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        val flags = if (Build.VERSION.SDK_INT > 30) {
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        } else {
            FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(this, 0, intent, flags)
    }


    private fun getNotification(title: String?, body: String?): Notification =
        NotificationCompat.Builder(MyApp.context, PRIMARY_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            //.setSmallIcon(R.drawable.ic_github)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(setIntent())
            .build()


    fun notify(title: String?, body: String?) {
        val notification = getNotification(title, body)
        getManager()!!.notify(1, notification)
    }


    companion object {
        const val PRIMARY_CHANNEL_ID = "Keep Alive"
    }
}
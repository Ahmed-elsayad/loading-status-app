package com.udacity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi


private const val NOTIFICATION_ID = 0
private const val CHANNEL_ID = "project_3"
 const val EXTRA_NAME = "download_name"
const val EXTRA_DOWNLOAD_STATUS = "download_status"

@RequiresApi(Build.VERSION_CODES.O)
fun NotificationManager.sendNotification(repo: Repo, downloadStatus : Boolean, applicationContext : Context){
// intent to send repo data to the detail Activity
    val contentIntent = Intent(applicationContext, DetailActivity::class.java).apply {
        putExtra(EXTRA_NAME,repo.title)
        putExtra(EXTRA_DOWNLOAD_STATUS,downloadStatus)
    }
// giving pending intent to the notification which will help us to  pass our data while pressing on the notification
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
//setting the visual and auditory behavior that is applied to the notification in that channel.
    val notificationChannel = NotificationChannel(CHANNEL_ID, applicationContext.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
    notificationChannel.enableLights(true)
    notificationChannel.lightColor = Color.GREEN
    notificationChannel.enableVibration( true)

//This is how you tell the user that something has happened in the background.
    val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)

    val notificationAction : Notification.Action = Notification.Action.Builder(
        Icon.createWithResource(applicationContext, R.drawable.ic_assistant_black_24dp),
        applicationContext.getString(R.string.check_status),
        contentPendingIntent
    ).build()

   /*As of Android Build.VERSION_CODES.S,
    apps targeting API level Build.VERSION_CODES.S or higher won't be able to start activities while processing broadcast receivers or services in response to notification action clicks.
    To launch an activity in those cases, provide a PendingIntent for the activity itself.*/
    val notification = Notification.Builder(
        applicationContext,
        NOTIFICATION_ID.toString()
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setChannelId(CHANNEL_ID)
        .addAction(notificationAction)
        .build()
/*
Post a notification to be shown in the status bar.
 If a notification with the same id has already been posted by your application and has not yet been canceled,
  it will be replaced by the updated information.*/
    notify(NOTIFICATION_ID, notification)
}

fun NotificationManager.cancelNotifications() {
    /*Cancels a previously posted notification.
If the notification does not currently represent a foreground service,
it will be removed from the UI and live notification listeners will be informed so they can remove the notification from their UIs.*/
    cancelAll()
}
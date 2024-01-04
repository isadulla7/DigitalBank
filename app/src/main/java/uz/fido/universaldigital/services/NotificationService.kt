package uz.fido.universaldigital.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.utils.const.Const
import uz.fido.utils.log.Logger

class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification.let { notification ->
            /**
             * SEND NOTIFICATION
             */
            notification?.let {
                sendNotification(notification.title, notification.body)
            }
        }
    }

    private fun sendNotification(messageTitle: String?, messageBody: String?) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent =
            PendingIntent.getActivity(
                applicationContext, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        val channelId = getString(R.string.push_channel_id)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_universal_logo_white)
            .setContentTitle(messageTitle ?: getString(R.string.app_name))
            .setContentText(messageBody ?: getString(R.string.new_message))
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, getString(R.string.push_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(
            System.currentTimeMillis().hashCode(),
            notificationBuilder.build()
        )
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Paper.book().write(Const.PAPER_FCM_TOKEN, p0)
        Logger.writeLogByKey(Const.PAPER_FCM_TOKEN, p0)
    }

}
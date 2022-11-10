package io.github.elaydis.calllogmonitor.data.serverservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.IBinder
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.elaydis.calllogmonitor.R
import io.github.elaydis.calllogmonitor.presentation.MainActivity
import io.github.elaydis.calllogmonitor.utils.marshmallowOnly
import io.github.elaydis.calllogmonitor.utils.nougat
import io.github.elaydis.calllogmonitor.utils.oreo
import javax.inject.Inject


const val ACTION_START_SERVER = "action_start_server"
const val ACTION_STOP_SERVER = "action_stop_server"
private const val NOTIFICATION_PRIORITY_CHANNEL_ID = "notification priority channel id"

@AndroidEntryPoint
class ServerServiceService : Service(), ServerServiceView {

    private val receiver =
        CallBroadcastReceiver { state, number ->
            serverServicePresenter.broadCastReceived(
                state,
                number
            )
        }

    private lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var serverServicePresenter: ServerServicePresenter

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            application.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            when (it) {
                ACTION_START_SERVER -> serverServicePresenter.startServerIntentReceived()
                ACTION_STOP_SERVER -> serverServicePresenter.stopServerIntentReceived()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        serverServicePresenter.serviceOnDestroy()
        super.onDestroy()
    }

    override fun startForeground() {
        val mainActivityIntent = createMainActivityPendingIntent()
        val stopServerIntent = createStopServerPendingIntent()

        val notification: Notification =
            NotificationCompat.Builder(this, NOTIFICATION_PRIORITY_CHANNEL_ID)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentIntent(mainActivityIntent)
                .addAction(0, resources.getString(R.string.stop_server_button_text), stopServerIntent)
                .build()

        startForeground(System.currentTimeMillis().toInt(), notification)
    }

    private fun createStopServerPendingIntent(): PendingIntent {
        val intent = Intent(applicationContext, ServerServiceService::class.java).apply {
            action = ACTION_STOP_SERVER
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(applicationContext, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun stopForeground() {
        marshmallowOnly {
            stopForeground(true)
        }
        nougat {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    override fun registerCallBroadcastReceiver() {
        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun queryContactForNumber(number: String): String {
        var name = ""
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        val phoneLookupCursor = contentResolver.query(
            uri,
            arrayOf(BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME),
            null,
            null,
            null
        )
        phoneLookupCursor?.takeIf { it.count != 0 }?.let { cursor ->
            while (cursor.moveToNext()) {
                cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME).takeIf { it >= 0 }
                    ?.let { index ->
                        name = cursor.getString(index)
                    }
            }
        }
        phoneLookupCursor?.close()
        return name
    }

    private fun createNotificationChannel() {
        oreo {
            val name = resources.getString(R.string.notification_channel_name)
            val description = resources.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel =
                NotificationChannel(NOTIFICATION_PRIORITY_CHANNEL_ID, name, importance)
            notificationChannel.description = description
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
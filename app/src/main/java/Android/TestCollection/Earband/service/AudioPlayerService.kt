package Android.TestCollection.Earband.service

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.model.Audio
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AudioPlayerService : Service() {

    private lateinit var audioPlayer: AudioPlayer
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var audio: Audio
    private var isPlaying: Boolean = false
    override fun onCreate() {
        super.onCreate()
        audioPlayer = AudioPlayer(this)
        audioPlayer.initiatePlayer()

        createNotificationChannel()

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.BROADCAST_ACTION_MINI_PLAYER_PLAY -> {
                        audioPlayer.resumePlayer()
                        isPlaying = true
                    }
                    Constants.BROADCAST_ACTION_MINI_PLAYER_PAUSE -> {
                        audioPlayer.pausePlayer()
                        isPlaying = false
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(Constants.BROADCAST_ACTION_MINI_PLAYER_PLAY)
            addAction(Constants.BROADCAST_ACTION_MINI_PLAYER_PAUSE)
        }
        if (isAndroidVersionHigherOrEqualTiramisu()) {
            registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            //registerReceiver(broadcastReceiver, intentFilter)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val audio: Audio =
            if (isAndroidVersionHigherOrEqualTiramisu()) {
                intent?.getParcelableExtra("AUDIO", Audio::class.java) ?: Audio.emptyAudio
            } else {
                @Suppress("DEPRECATION")
                intent?.getParcelableExtra("AUDIO") ?: Audio.emptyAudio
            }
        audioPlayer.preparePlayer(audio.data)
        audioPlayer.resumePlayer()
        val audioTitle = audio.title
        val notification = buildNotification(audioTitle)
        startForeground(1, notification)
        return START_NOT_STICKY
    }


    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Audio Playback Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }

    private fun buildNotification(audioTitle: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playing Audio")
            .setContentText(audioTitle)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(isPlaying) // Prevents users from swiping away the notification (Not Working)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun isAndroidVersionHigherOrEqualTiramisu(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        audioPlayer.stopPlayer()
        audioPlayer.releasePlayer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    companion object {
        const val CHANNEL_ID = "AudioServiceChannel"
    }
}
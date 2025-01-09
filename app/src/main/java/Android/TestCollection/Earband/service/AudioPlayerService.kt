package Android.TestCollection.Earband.service

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.application.AudioPlayerData
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
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerService : Service() {

    @Inject
    lateinit var audioPlayerData: AudioPlayerData

    private lateinit var audioPlayer: AudioPlayer
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Service Created")

        audioPlayer = AudioPlayer(this)
        audioPlayer.initiatePlayer()
        audioPlayer.addPlayerListener()

        createNotificationChannel()

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.BROADCAST_ACTION_MINI_PLAYER_PLAY -> {
                        playPlayer()
                    }

                    Constants.BROADCAST_ACTION_MINI_PLAYER_PAUSE -> {
                        pausePlayer()
                    }

                    Constants.BROADCAST_ACTION_SEEKBAR_PROGRESSION_CHANGES -> {
                        val newPosition = intent.getLongExtra("PROGRESSION", 0)
                        audioPlayer.changePosition(newPosition)
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(Constants.BROADCAST_ACTION_MINI_PLAYER_PLAY)
            addAction(Constants.BROADCAST_ACTION_MINI_PLAYER_PAUSE)
            addAction(Constants.BROADCAST_ACTION_SEEKBAR_PROGRESSION_CHANGES)
        }
        if (Util.isAndroidVersionHigherOrEqualTiramisu()) {
            registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            //registerReceiver(broadcastReceiver, intentFilter)
        }


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val audio: Audio = audioPlayerData.getSelectedAudio()
        val command = intent?.getStringExtra("MINI_PLAYER_COMMAND") ?: "PAUSE"
        when (command) {
            "PLAY" -> {
                audioPlayer.preparePlayer(audioPlayerData.selectedAudio.value.data)
                playPlayer()
            }

            "BACKWARD" -> {
                val isPlayingBackward = audioPlayer.playAudioBackwardOrResetAudio()
                if (isPlayingBackward) Util.broadcastState(this, Constants.BROADCAST_ACTION_PLAYER_BACKWARD)
            }
        }

        val audioTitle = audio.title
        val notification = buildNotification(audioTitle)
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        audioPlayer.stopPlayer()
        audioPlayer.releasePlayer()
    }

    private fun playPlayer() {
        audioPlayer.playPlayer()
        audioPlayerData.setIsPlaying(true)
    }

    private fun pausePlayer() {
        audioPlayer.pausePlayer()
        audioPlayerData.setIsPlaying(false)
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
            .setOngoing(audioPlayerData.isPlaying.value) // Prevents users from swiping away the notification (Not Working)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    companion object {
        const val CHANNEL_ID = "AudioServiceChannel"
        private const val TAG = "AudioService"
    }
}
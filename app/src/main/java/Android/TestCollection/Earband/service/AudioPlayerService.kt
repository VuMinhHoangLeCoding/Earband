package Android.TestCollection.Earband.service

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.application.AppPlayerDataModel
import Android.TestCollection.Earband.model.Audio
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerService : Service() {

    @Inject
    lateinit var appPlayerDataModel: AppPlayerDataModel

    private var audioManager: AudioManager? = null

    private lateinit var audioPlayer: AudioPlayer
    private lateinit var broadcastReceiver: BroadcastReceiver

    private var audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> {

            }

            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE -> {
                pausePlayer()
            }
        }
    }

    private var audioFocusRequest: AudioFocusRequest? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_GAME)
                    setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    build()
                })
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(audioFocusChangeListener)
                build()
            }
        } else {
            null
        }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Service Created")

        audioPlayer = AudioPlayer(this)
        audioPlayer.initiatePlayer()
        audioPlayer.addPlayerListener()

        createNotificationChannel()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager

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
            registerReceiver(broadcastReceiver, intentFilter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val audio: Audio = appPlayerDataModel.getSelectedAudio()
        val command = intent?.getStringExtra("MINI_PLAYER_COMMAND") ?: "PAUSE"
        when (command) {
            "PLAY" -> {
                audioPlayer.preparePlayer(appPlayerDataModel.selectedAudio.value.data)
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
        if (requestAudioFocus()) {
            audioPlayer.playPlayer()
            appPlayerDataModel.setIsPlaying(true)
        }
        else Log.d(TAG, "Audio focus request failed")
    }

    private fun pausePlayer() {
        audioPlayer.pausePlayer()
        appPlayerDataModel.setIsPlaying(false)
    }

    private fun requestAudioFocus(): Boolean {
        var res: Int?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { res = audioManager?.requestAudioFocus(it) }
        }

        @Suppress("DEPRECATION")
        res = audioManager?.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return res == AudioManager.AUDIOFOCUS_GAIN
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
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(appPlayerDataModel.isPlaying.value) // Prevents users from swiping away the notification (Not Working)
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
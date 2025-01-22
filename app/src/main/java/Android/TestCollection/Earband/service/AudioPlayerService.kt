package Android.TestCollection.Earband.service

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.R
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.activity.MainActivity
import Android.TestCollection.Earband.model.Audio
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioPlayerService : Service() {
    private var audioManager: AudioManager? = null

    private lateinit var audioPlayer: AudioPlayer
    private lateinit var broadcastReceiver: BroadcastReceiver

    private var audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> {}

            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE -> pausePlayer()
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
        var audio: Audio = Audio.emptyAudio
        try {
            if (Util.isAndroidVersionHigherOrEqualTiramisu()) {
                audio = intent?.getParcelableExtra("AUDIO", Audio::class.java)!!
            }
        } catch (e: Exception) {
            logError(e)
        }


        val command = intent?.getStringExtra("MINI_PLAYER_COMMAND") ?: "PAUSE"
        when (command) {
            "PLAY" -> {
                audioPlayer.preparePlayer(audio.data)
                playPlayer()
            }

            "BACKWARD" -> {
                val isPlayingBackward = audioPlayer.playAudioBackwardOrResetAudio()
                if (isPlayingBackward) Util.broadcastAction(this, Constants.BROADCAST_ACTION_PLAYER_BACKWARD)
            }
        }

        val audioTitle = audio.title
        val notification = buildNotification(audioTitle)
        startForeground(1, notification)
        return START_STICKY
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
            Util.broadcastBoolean(baseContext, true, Constants.BROADCAST_ACTION_IS_PLAYING)
        } else Log.e(TAG, "Audio focus request failed")
    }

    private fun pausePlayer() {
        audioPlayer.pausePlayer()
        Util.broadcastBoolean(baseContext, false, Constants.BROADCAST_ACTION_IS_PLAYING)
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
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }

    private fun buildNotification(audioTitle: String): Notification {

        val intent = Intent(baseContext, MainActivity::class.java)
        val thumb = BitmapFactory.decodeResource(resources, R.drawable.muel_icon_play_wrap)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.muel_icon_play_wrap)
            .setLargeIcon(thumb)
            .setContentTitle("Placeholder")
            .setContentText("Placeholder text")
            .addAction(R.drawable.muel_icon_play_wrap, "play", null)
            .addAction(R.drawable.muel_icon_forward, "forward", null)
            .addAction(R.drawable.muel_icon_backward, "backward", null)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true) // Prevents users from swiping away the notification (Not Working)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun logError(e: Exception) {
        Log.e(TAG, "Error at Player Service: $e")
    }

    companion object {
        const val CHANNEL_ID = "AudioServiceChannel"
        private const val TAG = "AudioService"
    }
}
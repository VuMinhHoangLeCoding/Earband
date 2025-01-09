package Android.TestCollection.Earband.service

import Android.TestCollection.Earband.Constants
import Android.TestCollection.Earband.Util
import android.content.Context
import android.os.Handler
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayer(private val context: Context) {

    @Suppress("DEPRECATION")
    private val handler = Handler()

    private var player: ExoPlayer? = null
    private var runnable: Runnable? = null

    fun initiatePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
        }
    }


    fun addPlayerListener() {
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        startTrackingPlayerProgress()
                    }

                    Player.STATE_ENDED -> {
                        stopTrackingPlayerProgression()
                        Util.broadcastState(context, Constants.BROADCAST_ACTION_PLAYER_ENDED)
                    }

                    else -> {
                        stopTrackingPlayerProgression()
                    }
                }
            }
        })
    }

    fun preparePlayer(uri: String) {
        player?.apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    fun getPlayerDuration(): Long {
        return player?.duration ?: 0L
    }

    fun pausePlayer() {
        player?.pause()
    }

    fun playPlayer() {
        player?.play()
    }


    fun stopPlayer() {
        player?.stop()
    }

    fun releasePlayer() {        //Release all data from player when not used
        player?.release()
        player = null
    }

    fun getCurrentProgression(): Long {
        return player?.currentPosition ?: 0
    }

    fun startTrackingPlayerProgress() {
        if (runnable == null) {
            runnable = object : Runnable {
                override fun run() {
                    val progress = getCurrentProgression()
                    Util.broadcastPlayerProgress(context, Constants.BROADCAST_ACTION_PLAYER_PROGRESSION, progress)
                    handler.postDelayed(this, 250)
                }
            }
        }
        handler.post(runnable!!)
    }

    private fun stopTrackingPlayerProgression() {
        if (runnable != null){
            runnable.let {
                handler.removeCallbacks(it!!)
            }
        }

    }

    fun changePosition(position: Long){
        player?.seekTo(position)
    }

    fun playAudioBackwardOrResetAudio(): Boolean {
        if (getCurrentProgression() >= 5000L) {
            player?.seekTo(0)
            return false
        } else {
            return true
        }
    }
}
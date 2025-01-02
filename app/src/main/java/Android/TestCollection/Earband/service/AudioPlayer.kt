package Android.TestCollection.Earband.service

import Android.TestCollection.Earband.BroadcastUtil
import Android.TestCollection.Earband.Constants
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayer(private val context: Context) {

    private var player: ExoPlayer? = null
    private val broadcastUtil = BroadcastUtil()

    fun initiatePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
        }
    }

    fun addPlayerListener() {
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_ENDED) {
                    broadcastUtil.broadcastState(context, Constants.BROADCAST_ACTION_PLAYER_ENDED)
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
        return player?.currentPosition ?: 0L
    }

    fun playBackwardPlayerOrResetAudio(uri: String): Boolean {
        if (getCurrentProgression() >= 5000L) {
            player?.seekTo(0)
            return false
        } else {
            stopPlayer()
            preparePlayer(uri)
            playPlayer()
            return true
        }
    }
}
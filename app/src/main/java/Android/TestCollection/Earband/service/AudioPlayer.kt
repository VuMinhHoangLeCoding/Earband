package Android.TestCollection.Earband.service

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayer(private val context: Context) {

    private var player: ExoPlayer? = null

    fun initiatePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
        }

    }

    fun preparePlayer(uri: String) {
        player?.apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    fun pausePlayer() {
        player?.pause()
    }

    fun resumePlayer() {
        player?.play()
    }


    fun stopPlayer() {
        player?.stop()
    }

    fun releasePlayer() {        //Release all data from player when not used
        player?.release()
        player = null
    }
}
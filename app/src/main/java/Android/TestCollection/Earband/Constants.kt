package Android.TestCollection.Earband

import android.Manifest.permission
import android.annotation.SuppressLint
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

object Constants {

    const val PERMISSION_REQUEST_CODE = 100

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    const val PERMISSION_AUDIO = permission.READ_MEDIA_AUDIO

    const val PERMISSION_EXTERNAL_STORAGE = permission.READ_EXTERNAL_STORAGE

    @RequiresApi(Build.VERSION_CODES.P)
    const val PERMISSION_FOREGROUND_SERVICE = permission.FOREGROUND_SERVICE

    @SuppressLint("InlinedApi")
    const val PERMISSION_FOREGROUND_SERVICE_MEDIA_PLAYBACK = permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK

    @SuppressLint("InlinedApi")
    const val PERMISSION_POST_NOTIFICATIONS = permission.POST_NOTIFICATIONS

    val baseProjection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DATE_MODIFIED,
        MediaStore.Audio.Media.COMPOSER
    )

    const val BROADCAST_ACTION_AUDIO_SELECTED: String = "AUDIO_SELECTED"
    const val BROADCAST_ACTION_MINI_PLAYER_PLAY: String = "MINI_PLAYER_PLAY"
    const val BROADCAST_ACTION_MINI_PLAYER_PAUSE: String = "MINI_PLAYER_PAUSE"
    const val BROADCAST_ACTION_PLAYER_ACTIVITY_PLAY: String = "PLAYER_ACTIVITY_PLAY"
    const val BROADCAST_ACTION_PLAYER_ACTIVITY_PAUSE: String = "PLAYER_ACTIVITY_PAUSE"
    const val BROADCAST_ACTION_PLAYER_ACTIVITY_FORWARD: String = "PLAYER_ACTIVITY_FORWARD"
    const val BROADCAST_ACTION_PLAYER_ACTIVITY_BACKWARD: String = "PLAYER_ACTIVITY_BACKWARD"
    const val BROADCAST_ACTION_PLAYER_ENDED: String = "PLAYER_ENDED"
    const val BROADCAST_ACTION_PLAYER_BACKWARD: String = "PLAYER_BACKWARD"
    const val BROADCAST_ACTION_PLAYER_PROGRESSION: String = "PLAYER_PROGRESSION"
    const val BROADCAST_ACTION_SEEKBAR_PROGRESSION_CHANGES: String = "SEEKBAR_PROGRESSION_CHANGES"
    const val BROADCAST_ACTION_SHUFFLE: String = "SHUFFLE"
    const val BROADCAST_ACTION_IS_PLAYING: String = "IS_PLAYING"

}
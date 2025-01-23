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
    const val BROADCAST_ACTION_FROM_MINI_PLAYER: String = "FROM_MINI_PLAYER"
    const val BROADCAST_ACTION_AUDIO_ENDED: String = "AUDIO_ENDED"
    const val BROADCAST_ACTION_CONFIRM_BACKWARD: String = "CONFIRM_BACKWARD"
    const val BROADCAST_ACTION_PLAYER_PROGRESSION: String = "PLAYER_PROGRESSION"
    const val BROADCAST_ACTION_SEEKBAR_PROGRESSION_CHANGES: String = "SEEKBAR_PROGRESSION_CHANGES"
    const val BROADCAST_ACTION_SHUFFLE: String = "SHUFFLE"
    const val BROADCAST_ACTION_FROM_PLAYER: String = "FROM_PLAYER"

}
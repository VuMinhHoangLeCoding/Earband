package Android.TestCollection.Earband

import android.Manifest.*
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

object Constants {

    const val PERMISSION_REQUEST_CODE = 100

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    const val PERMISSION_AUDIO = permission.READ_MEDIA_AUDIO

    const val PERMISSION_EXTERNAL_STORAGE = permission.READ_EXTERNAL_STORAGE

    const val PERMISSION_FOREGROUND_SERVICE = permission.FOREGROUND_SERVICE

    const val PERMISSION_FOREGROUND_SERVICE_MEDIA_PLAYBACK =
        permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK

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

    const val BROADCAST_ACTION_AUDIO_SELECTED: String = "Android.TestCollection.Earband.AUDIO_SELECTED"
    const val BROADCAST_ACTION_PLAYER_PLAY: String = "Android.TestCollection.Earband.PLAYER_PLAY"
    const val BROADCAST_ACTION_PLAYER_PAUSE: String = "Android.TestCollection.Earband.PLAYER_PAUSE"

}
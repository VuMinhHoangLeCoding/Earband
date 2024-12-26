package Android.TestCollection.Earband

import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

object Constants {

    const val PERMISSION_REQUEST_CODE = 100

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    const val PERMISSION_AUDIO = android.Manifest.permission.READ_MEDIA_AUDIO

    const val PERMISSION_EXTERNAL_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE

    val     baseProjection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.COMPOSER
        )

}
package Android.TestCollection.Earband.repository

import Android.TestCollection.Earband.Constants.baseProjection
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.model.Audio
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore


interface AudioRepository {

    fun audios(): List<Audio>

}

class RealAudioRepository(private val context: Context) : AudioRepository {

    override fun audios():List<Audio> {
        val audios =   arrayListOf<Audio>()

        val cursor = createAudioCursor()

        if(cursor != null && cursor.moveToFirst()) {
            do {
                if(getAudioFromCursor(cursor) != Audio.emptyAudio) audios.add(getAudioFromCursor(cursor))
            }
            while (cursor.moveToNext())
        }
        cursor?.close()

        val size = audios.size
        Util.triggerToast(context, "Number of audios: $size")

        return audios
    }

    private fun getAudioFromCursor(cursor: Cursor) : Audio {
        try {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow((MediaStore.Audio.Media.TITLE)))
            val track = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
            val year = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
            val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            val dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
            val composer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER))

            return Audio(id, title, track, year, duration, data, dateModified, composer)
        }
        catch (e : Exception) {
            return Audio.emptyAudio
        }
    }

    private fun createAudioCursor() : Cursor? {
        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                baseProjection,
                null, null, null
            )
            Util.triggerToast(context, "get cursor")
            return cursor
        }
        catch (e : Exception) {
            Util.triggerToast(context, "failed")
            return null
        }
    }


}
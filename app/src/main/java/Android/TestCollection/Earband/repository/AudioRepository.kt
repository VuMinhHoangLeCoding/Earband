package Android.TestCollection.Earband.repository

import Android.TestCollection.Earband.AudioSortOrder
import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.Util.baseProjection
import Android.TestCollection.Earband.model.Audio
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import java.text.Collator

interface AudioRepository {

    fun audios(sortOrder: String): List<Audio>
    fun audios(cursor: Cursor?): List<Audio>
    fun sortedAudios(cursor: Cursor?, sortOrder: String): List<Audio>

}

class RealAudioRepository(private val context: Context) : AudioRepository {

    override fun audios(sortOrder: String): List<Audio> {
        return sortedAudios(createAudioCursor(), sortOrder)
    }

    override fun audios(cursor: Cursor?): List<Audio> {
        val audios = arrayListOf<Audio>()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (getAudioFromCursor(cursor) != Audio.emptyAudio) audios.add(getAudioFromCursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return audios
    }

    override fun sortedAudios(cursor: Cursor?, sortOrder: String): List<Audio> {
        val collator = Collator.getInstance()
        val audios = audios(cursor)
        return when (sortOrder) {
            AudioSortOrder.AUDIO_DEFAULT -> {
                audios
            }

            AudioSortOrder.AUDIO_A_Z -> {
                audios.sortedWith { s1, s2 -> collator.compare(s1.title, s2.title) }
            }

            AudioSortOrder.AUDIO_Z_A -> {
                audios.sortedWith { s1, s2 -> collator.compare(s2.title, s1.title) }
            }

            AudioSortOrder.AUDIO_DATE_MODIFIED -> {
                audios.sortedWith { s1, s2 -> collator.compare(s1.dateModified, s2.dateModified) }
            }

            AudioSortOrder.AUDIO_DURATION -> {
                audios.sortedWith { s1, s2 -> collator.compare(s1.duration, s2.duration) }
            }

            else -> {
                audios
            }
        }
    }

    private fun getAudioFromCursor(cursor: Cursor): Audio {
        try {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow((MediaStore.Audio.Media.TITLE)))
            val track = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
            val year = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
            val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            val dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
            val albumId = cursor.getLongOrNull((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))) ?: -1
            val albumName = cursor.getStringOrNull((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))) ?: ""
            val artistId = cursor.getLongOrNull((cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))) ?: -1
            val artistName = cursor.getStringOrNull((cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))) ?: ""
            val composer = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER)) ?: ""
            val albumArtist = cursor.getStringOrNull((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST))) ?: ""
            val playlistId: Long = 0
            val idInPlaylist: Long = -1

            return Audio(
                id,
                title,
                track,
                year,
                duration,
                data,
                dateModified,
                albumId,
                albumName,
                artistId,
                artistName,
                composer,
                albumArtist,
                playlistId,
                idInPlaylist
            )
        } catch (e: Exception) {
            return Audio.emptyAudio
        }
    }

    private fun createAudioCursor(): Cursor? {
        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                baseProjection,
                null, null, null
            )
            return cursor
        } catch (e: Exception) {
            Util.triggerToast(context, "Failed to get cursor!")
            return null
        }
    }


}
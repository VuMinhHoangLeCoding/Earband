package Android.TestCollection.Earband.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class Audio(
    open val id: Long,
    open val title: String,
    open val trackNumber: Int,
    open val year: Int,
    open val duration: Long,
    open val data: String,
    open val dateModified: Long,
    open val albumId: Long,
    open val albumName: String,
    open val artistId: Long,
    open val artistName: String,
    open val composer: String? = "unknown",
    open val albumArtist: String?,
    open val playlistId: Long,
    open val idInPlaylist: Long
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Audio

        if (id != other.id) return false
        if (title != other.title) return false
        if (trackNumber != other.trackNumber) return false
        if (year != other.year) return false
        if (duration != other.duration) return false
        if (data != other.data) return false
        if (dateModified != other.dateModified) return false
        if (albumId != other.albumId) return false
        if (albumName != other.albumName) return false
        if (artistId != other.artistId) return false
        if (artistName != other.artistName) return false
        if (composer != other.composer) return false
        if (albumArtist != other.albumArtist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + trackNumber
        result = 31 * result + year
        result = 31 * result + duration.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + dateModified.hashCode()
        result = 31 * result + albumId.hashCode()
        result = 31 * result + albumName.hashCode()
        result = 31 * result + artistId.hashCode()
        result = 31 * result + artistName.hashCode()
        result = 31 * result + (composer?.hashCode() ?: 0)
        result = 31 * result + (albumArtist?.hashCode() ?: 0)
        result = 31 * result + playlistId.hashCode()
        result = 31 * result + idInPlaylist.hashCode()
        return result
    }

    companion object {
        val emptyAudio = Audio(
            id = -1,
            title = "",
            trackNumber = -1,
            year = -1,
            duration = -1,
            data = "",
            dateModified = -1,
            albumId = -1,
            albumName = "",
            artistId = -1,
            artistName = "",
            composer = "",
            albumArtist = "",
            playlistId = -1,
            idInPlaylist = -1
        )
    }

}

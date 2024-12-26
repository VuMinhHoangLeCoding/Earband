package Android.TestCollection.Earband.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Audio(
    val id: Long,
    val title: String,
    val trackNumber: Int,
    val year: Int,
    val duration: Long,
    val data: String,
    val dateModified: Long,
    val composer: String? = "unknown"
) : Parcelable {


    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false

        other as Audio

        if(id != other.id) return false
        if(title != other.title) return false
        if(trackNumber != other.trackNumber) return false
        if(year != other.year) return false
        if(duration != other.duration) return false
        if(data != other.data) return false
        if(dateModified != other.dateModified) return false
        if(composer != other.composer) return false

        return true
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
            composer = ""
        )
    }

}

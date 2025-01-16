package Android.TestCollection.Earband.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class Playlist(
    val id: Long,
    val name: String
) : Parcelable {

    companion object {
        val emptyPlaylist = Playlist(-1, "")
        val localPlaylist = Playlist(0, "")
    }
}
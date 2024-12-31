package Android.TestCollection.Earband.db

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize


@Parcelize
class PlaylistOneToManyAudio (
    @Embedded val playlistEntity: PlaylistEntity,
    @Relation(
        parentColumn ="playlist_id",
        entityColumn = "playlist_creator_id"
    )
    val audios: List<AudioEntity>
) : Parcelable
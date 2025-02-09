package Android.TestCollection.Earband.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UtilityEntity (
    @PrimaryKey
    @ColumnInfo
    val id: Int = 1,

    @ColumnInfo(name = "play_mode_index")
    val playMode: Int,

    @ColumnInfo(name = "theme")
    val theme: Int,

    @ColumnInfo(name = "sort_order_local")
    val sortOrderLocal: String
)
package Android.TestCollection.Earband.model

import Android.TestCollection.Earband.db.UtilityEntity

class Utility (
    var playMode: Int,
    var theme: Int,
    var sortOrderLocal: String
)


fun Utility.toEntity(): UtilityEntity {
    return UtilityEntity (
        id = 1,
        playMode = playMode,
        theme = theme,
        sortOrderLocal = sortOrderLocal
    )
}

fun UtilityEntity.toUtility(): Utility {
    return Utility (
        playMode = playMode,
        theme = theme,
        sortOrderLocal = sortOrderLocal
    )
}
package Android.TestCollection.Earband.model

import Android.TestCollection.Earband.db.UtilityEntity

class Utility (
    var playMode: Int
)


fun Utility.toEntity(): UtilityEntity {
    return UtilityEntity (
        id = 1,
        playMode = playMode
    )
}

fun UtilityEntity.toUtility(): Utility {
    return Utility (
        playMode = playMode
    )
}
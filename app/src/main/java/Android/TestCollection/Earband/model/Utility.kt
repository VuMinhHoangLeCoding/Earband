package Android.TestCollection.Earband.model

import Android.TestCollection.Earband.db.UtilityEntity

class Utility (
    val playMode: Int
)

fun Utility.toEntity(): UtilityEntity {
    return UtilityEntity (
        playMode = playMode
    )
}

fun UtilityEntity.toUtility(): Utility {
    return Utility (
        playMode = playMode
    )
}
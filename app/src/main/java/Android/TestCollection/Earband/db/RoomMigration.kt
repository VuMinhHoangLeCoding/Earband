package Android.TestCollection.Earband.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `UtilityEntity` (
                `id` INTEGER NOT NULL PRIMARY KEY,
                `play_mode_index` INTEGER NOT NULL
            )
        """
        )
    }
}
package Android.TestCollection.Earband.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                ALTER TABLE `UtilityEntity` ADD COLUMN `theme` INTEGER NOT NULL DEFAULT 0;
                ALTER TABLE `UtilityEntity` ADD COLUMN `sort_order_local` TEXT NOT NULL DEFAULT '';
            """
        )
    }
}
package Android.TestCollection.Earband.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [PlaylistEntity::class, AudioHistoryEntity::class, AudioEntity::class, UtilityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EarbandDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun audioHistoryDao(): AudioHistoryDao
    abstract fun utilityDao(): UtilityDao

    companion object {

        @Volatile
        private var INSTANCE: EarbandDatabase? = null

        fun getDatabase(
            context: Context
        ): EarbandDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EarbandDatabase::class.java,
                    "earband_database"
                )
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}
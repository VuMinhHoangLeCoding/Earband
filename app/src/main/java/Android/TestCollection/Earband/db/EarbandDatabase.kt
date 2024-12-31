package Android.TestCollection.Earband.db

import Android.TestCollection.Earband.EarbandApp
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope


@Database(
    entities = [PlaylistEntity::class, AudioHistoryEntity::class, AudioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EarbandDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun audioHistoryDao(): AudioHistoryDao

    companion object {

        @Volatile
        private var INSTANCE: EarbandDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): EarbandDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EarbandDatabase::class.java,
                    "earband_database"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}
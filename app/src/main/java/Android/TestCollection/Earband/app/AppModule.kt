package Android.TestCollection.Earband.app

import Android.TestCollection.Earband.db.AudioHistoryDao
import Android.TestCollection.Earband.db.EarbandDatabase
import Android.TestCollection.Earband.db.PlaylistDao
import Android.TestCollection.Earband.db.UtilityDao
import Android.TestCollection.Earband.repository.RealAudioRepository
import Android.TestCollection.Earband.repository.RealRoomRepository
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): EarbandDatabase {
        return EarbandDatabase.getDatabase(context)
    }

    @Provides
    fun providesPlaylistDao(database: EarbandDatabase): PlaylistDao {
        return database.playlistDao()
    }

    @Provides
    fun providesAudioHistoryDao(database: EarbandDatabase): AudioHistoryDao {
        return database.audioHistoryDao()
    }

    @Provides
    fun provideUtilityDao(database: EarbandDatabase): UtilityDao {
        return database.utilityDao()
    }

    @Provides
    @Singleton
    fun providesAudioRepository(@ApplicationContext context: Context) : RealAudioRepository {
        return RealAudioRepository(context)
    }

    @Provides
    @Singleton
    fun provideRoomRepository(playlistDao: PlaylistDao, audioHistoryDao: AudioHistoryDao, utilityDao: UtilityDao) : RealRoomRepository {
        return RealRoomRepository(playlistDao, audioHistoryDao, utilityDao)
    }

}
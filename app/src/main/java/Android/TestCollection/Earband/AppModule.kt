package Android.TestCollection.Earband

import Android.TestCollection.Earband.application.AudioPlayerData
import Android.TestCollection.Earband.db.AudioHistoryDao
import Android.TestCollection.Earband.db.EarbandDatabase
import Android.TestCollection.Earband.db.PlaylistDao
import Android.TestCollection.Earband.repository.RealAudioRepository
import Android.TestCollection.Earband.repository.RealRoomRepository
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Default)
    }

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
    @Singleton
    fun providesAudioRepository(@ApplicationContext context: Context) : RealAudioRepository {
        return RealAudioRepository(context)
    }

    @Provides
    @Singleton
    fun provideAudioPlayerData(): AudioPlayerData {
        return AudioPlayerData()
    }

    @Provides
    @Singleton
    fun provideRoomRepository(playlistDao: PlaylistDao, audioHistoryDao: AudioHistoryDao) : RealRoomRepository {
        return RealRoomRepository(playlistDao, audioHistoryDao)
    }

}
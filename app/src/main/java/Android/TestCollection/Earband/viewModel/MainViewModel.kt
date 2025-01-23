package Android.TestCollection.Earband.viewModel

import Android.TestCollection.Earband.db.AudioHistoryEntity
import Android.TestCollection.Earband.db.fromHistoryToAudios
import Android.TestCollection.Earband.db.toAudioHistory
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.model.Playlist
import Android.TestCollection.Earband.model.Utility
import Android.TestCollection.Earband.model.toEntity
import Android.TestCollection.Earband.model.toUtility
import Android.TestCollection.Earband.repository.RealAudioRepository
import Android.TestCollection.Earband.repository.RealRoomRepository
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val audioRepository: RealAudioRepository,
    private val roomRepository: RealRoomRepository
) : AndroidViewModel(application) {

    lateinit var observableAudioHistoryEntityList: LiveData<List<AudioHistoryEntity>>

    var appUtility: Utility? = null

    private val _playMode = MutableStateFlow(0)
    val playMode: StateFlow<Int> get() = _playMode

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _audioHistoryList = MutableLiveData<List<Audio>>()
    val audioHistoryList: LiveData<List<Audio>> = _audioHistoryList

    private val _currentAudio = MutableStateFlow(Audio.emptyAudio)
    val currentAudio: StateFlow<Audio> get() = _currentAudio

    private val _currentPlaylist = MutableStateFlow(Playlist.emptyPlaylist)
    val currentPlaylist: StateFlow<Playlist> get() = _currentPlaylist

    private val _currentAudios = MutableStateFlow<List<Audio>>(emptyList())
    val currentAudios: StateFlow<List<Audio>> get() = _currentAudios

    private val _localAudios = MutableLiveData<List<Audio>>()
    val localAudios: LiveData<List<Audio>> = _localAudios

    // ---------------------------Data initializer------------------------------
    private suspend fun loadAudiosFromLocal(): List<Audio> {
        return audioRepository.audios()
    }

    fun getAudiosFromLocal() {
        viewModelScope.launch(Dispatchers.IO) {
            val audios = loadAudiosFromLocal()
            withContext(Dispatchers.Main) {
                _localAudios.value = audios
            }
        }
    }

    fun getAppUtility() {
        viewModelScope.launch(Dispatchers.IO) {
            appUtility = roomRepository.getUtilityEntity()?.toUtility()
            if (appUtility == null) {
                Log.e(TAG, "create new utility")
                val newUtil = Utility(0).toEntity()
                roomRepository.upsertUtility(newUtil)
                try {
                    appUtility = roomRepository.getUtilityEntity()!!.toUtility()
                } catch (e: Exception) {
                    Log.e(TAG, "Error View Model: $e")
                }
            }
            setInitialObservableAppUtilities()
        }
    }

    private fun setInitialObservableAppUtilities() {
        setObservablePlayMode(appUtility!!.playMode)
    }

    fun getHistoryAndLatestAudio() {
        observableAudioHistoryEntityList = roomRepository.observableAudioHistoryEntityList()
    }

    // -------------------------------Getters and Setters----------------------------------


    fun setCurrentAudio(audio: Audio) {
        _currentAudio.value = audio
    }

    fun setCurrentPlaylist(playlist: Playlist) {
        _currentPlaylist.value = playlist
    }

    fun setIsPlaying(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun setCurrentAudios(audios: List<Audio>) {
        _currentAudios.value = audios
    }

    // --------------------------------------Special Getters and Setters-----------------------------------------------
    fun getAudioOnPositionFromLocal(position: Int): Audio {
        val playlistSize = localAudios.value?.size ?: 0
        val pos = if (position >= playlistSize) 0
        else if (position < 0) (playlistSize - 1)
        else position
        return localAudios.value?.getOrNull(pos) ?: Audio.emptyAudio
    }

    fun getAudioOnPositionFromCurrent(position: Int): Audio {
        val playlistSize = currentAudios.value.size
        val pos = if (position >= playlistSize) 0
        else if (position < 0) (playlistSize - 1)
        else position
        return currentAudios.value.getOrNull(pos) ?: Audio.emptyAudio
    }

    fun getRandomPositionFromCurrentAudios(): Int {
        val playlistSize = currentAudios.value.size
        return Random.nextInt(0, playlistSize + 1)
    }

    fun getPositionOnAudioFromCurrentAudios(audio: Audio): Int {
        return currentAudios.value.indexOf(audio)
    }

    fun setAudioHistoryList() {
        _audioHistoryList.value = observableAudioHistoryEntityList.value?.fromHistoryToAudios() ?: emptyList()
    }

    fun setObservablePlayMode(mode: Int) {
        try {
            _playMode.value = mode
        } catch (_: Exception) {
        }
    }

    fun setCurrentAudiosAsLocalAudios() {
        try {
            _currentAudios.value = localAudios.value!!
        }
        catch (e: Exception) {
            Log.e(TAG, "Error ViewModel: $e")
        }
    }

    // --------------------------------------Extra Functions------------------------------------

    fun getAudioInLocal(audio: Audio): Audio {
        localAudios.value?.forEach { au ->
            if (audio.id == au.id) return au
        }
        return Audio.emptyAudio
    }

    fun addNewAudioHistoryEntity(audio: Audio, time: Long) {
        viewModelScope.launch {
            roomRepository.upsertAudioHistory(audio.toAudioHistory(time))
        }
    }

    suspend fun upsertUtility() {
        roomRepository.upsertUtility(appUtility!!.toEntity())
        Log.d(TAG, "triggered upsert Utility")
    }

    companion object {
        const val TAG: String = "MainActivity"
    }
}
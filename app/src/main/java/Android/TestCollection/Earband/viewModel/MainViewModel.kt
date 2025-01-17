package Android.TestCollection.Earband.viewModel

import Android.TestCollection.Earband.db.AudioHistoryEntity
import Android.TestCollection.Earband.db.fromHistoryToAudios
import Android.TestCollection.Earband.db.toAudioHistory
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.repository.RealAudioRepository
import Android.TestCollection.Earband.repository.RealRoomRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val audioRepository: RealAudioRepository,
    private val roomRepository: RealRoomRepository
) : AndroidViewModel(application) {



    lateinit var observableAudioHistoryEntityList: LiveData<List<AudioHistoryEntity>>

    private val _audioHistoryList = MutableLiveData<List<Audio>>()
    val audioHistoryList: LiveData<List<Audio>> = _audioHistoryList

    private val _selectedAudio = MutableLiveData<Audio>()
    val selectedAudio: LiveData<Audio> = _selectedAudio

    private val _localAudios = MutableLiveData<List<Audio>>()
    val localAudios: LiveData<List<Audio>> = _localAudios


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

    fun getAudio(audio: Audio) {
        _selectedAudio.value = audio
    }

    fun getLocalAudios(): List<Audio> {
        return _localAudios.value ?: emptyList()
    }

    fun getCurrentAudioPlaylistId(): Long {
        return _selectedAudio.value?.playlistId ?: -1
    }

    fun loadAudioHistoryEntityList(): List<AudioHistoryEntity> {
        var audioHistoryEntities: List<AudioHistoryEntity> = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            audioHistoryEntities = roomRepository.getAudioHistoryList()
        }
        return audioHistoryEntities
    }

    fun loadObservableAudioHistoryEntityList() {
        observableAudioHistoryEntityList = roomRepository.observableAudioHistoryEntityList()
    }

    fun loadAudioHistoryList() {
        _audioHistoryList.value = observableAudioHistoryEntityList.value?.fromHistoryToAudios() ?: emptyList()
    }

    fun addNewAudioHistoryEntity(audio: Audio, time: Long) {
        viewModelScope.launch {
            roomRepository.upsertAudioHistory(audio.toAudioHistory(time))
        }
    }

    fun getAudioFromAudioHistoryOnPosition(position: Int): Audio {
        val audios = audioHistoryList.value ?: emptyList()
        return if (audios.isNotEmpty()) audios[position] else Audio.emptyAudio
    }

}
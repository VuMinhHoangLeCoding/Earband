package Android.TestCollection.Earband.viewModel

import Android.TestCollection.Earband.Util
import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.repository.AudioRepository
import Android.TestCollection.Earband.repository.RealAudioRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AudioViewModel(application: Application) : AndroidViewModel(application) {
    private val audioRepository: AudioRepository = RealAudioRepository(application)

    private val _currentAudio = MutableLiveData<Audio>()
    val currentAudio: LiveData<Audio> = _currentAudio

    private val _audios = MutableLiveData<List<Audio>>()
    val audios: LiveData<List<Audio>> = _audios



    fun loadAudiosFromLocal() : List<Audio> {
        var localAudios: List<Audio> = emptyList()
        viewModelScope.launch {
            localAudios = audioRepository.audios()
        }
        return localAudios
    }

    fun loadAudios() {
        _audios.value = loadAudiosFromLocal()
    }

    fun getAudio(audio: Audio) {
        _currentAudio.value = audio
    }

    fun getAudios(): List<Audio> {
        return _audios.value ?: emptyList()
    }

    fun getCurrentAudioPlaylistId(): Long {
        return currentAudio.value?.playlistId ?: -1
    }

}
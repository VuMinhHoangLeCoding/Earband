package Android.TestCollection.Earband.viewModel

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

    private val _selectedAudio = MutableLiveData<Audio>()
    val selectedAudio: LiveData<Audio> = _selectedAudio

    private val _localAudios = MutableLiveData<List<Audio>>()
    val loalAudios: LiveData<List<Audio>> = _localAudios



    private fun loadAudiosFromLocal() : List<Audio> {
        var localAudios: List<Audio> = emptyList()
        viewModelScope.launch {
            localAudios = audioRepository.audios()
        }
        return localAudios
    }

    fun loadAudios() {
        _localAudios.value = loadAudiosFromLocal()
    }

    fun getAudio(audio: Audio) {
        _selectedAudio.value = audio
    }

    fun getAudios(): List<Audio> {
        return _localAudios.value ?: emptyList()
    }

    fun getCurrentAudioPlaylistId(): Long {
        return selectedAudio.value?.playlistId ?: -1
    }

}
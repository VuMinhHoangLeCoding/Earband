package Android.TestCollection.Earband.viewModel

import Android.TestCollection.Earband.model.Audio
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MiniPLayerViewModel() : ViewModel() {

    private val _selectedAudio = MutableLiveData<Audio>()
    val selectedAudio: LiveData<Audio> = _selectedAudio

    private val _audioPlaylist = MutableLiveData<List<Audio>>()
    val audioPlaylist: LiveData<List<Audio>> = _audioPlaylist


    fun setAudioPlaylist(audios: List<Audio>) {
        _audioPlaylist.value = audios
    }

    fun setSelectedAudio(audio: Audio) {
        _selectedAudio.value = audio
    }

    fun getSelectedAudioFromPosition(position: Int): Audio {
        val playlistSize = _audioPlaylist.value?.size ?: 0

        val pos = if (position >= playlistSize || position < 0) 0 else position

        return _audioPlaylist.value?.getOrNull(pos) ?: Audio.emptyAudio
    }

    fun hasAudio(audio: Audio): Boolean {
        return _audioPlaylist.value?.contains(audio) == true
    }

    fun getAudioPosition(audio: Audio): Int {
        return _audioPlaylist.value?.indexOf(audio) ?: -1
    }

    fun hasPlaylist(): Boolean {
        return _audioPlaylist.value?.isNotEmpty() ?: false
    }

    fun getSelectedAudioPlaylist(): Long {
        return selectedAudio.value?.playlistId ?: -1
    }

}
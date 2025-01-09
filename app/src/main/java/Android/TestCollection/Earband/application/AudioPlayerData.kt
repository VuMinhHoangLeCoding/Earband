package Android.TestCollection.Earband.application

import Android.TestCollection.Earband.model.Audio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AudioPlayerData {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _selectedAudio = MutableStateFlow(Audio.emptyAudio)
    val selectedAudio: StateFlow<Audio> get() = _selectedAudio

    private val _currentAudioPlaylist = MutableStateFlow<List<Audio>>(emptyList())
    val currentAudioPlaylist: StateFlow<List<Audio>> get() = _currentAudioPlaylist

    fun setSelectedAudio(audio: Audio) {
        _selectedAudio.value = audio
    }

    fun getSelectedAudio(): Audio {
        return _selectedAudio.value
    }

    fun setAudioPlaylist(audios: List<Audio>) {
        _currentAudioPlaylist.value = audios
    }

    fun getAudioPlaylist(): List<Audio> {
        return _currentAudioPlaylist.value
    }

    fun getSelectedAudioFromPosition(position: Int): Audio {
        val playlistSize = _currentAudioPlaylist.value.size

        val pos = if (position >= playlistSize) 0
                    else if (position < 0) (playlistSize - 1)
                        else position

        return _currentAudioPlaylist.value.getOrNull(pos) ?: Audio.emptyAudio
    }

    fun hasAudio(audio: Audio): Boolean {
        return _currentAudioPlaylist.value.contains(audio)
    }

    fun getAudioPosition(audio: Audio): Int {
        return _currentAudioPlaylist.value.indexOf(audio)
    }

    fun getSelectedAudioPlaylist(): Long {
        return if (_selectedAudio.value != Audio.emptyAudio) _selectedAudio.value.playlistId else -1
    }

    fun setIsPlaying(state: Boolean) {
        _isPlaying.value = state
    }
}
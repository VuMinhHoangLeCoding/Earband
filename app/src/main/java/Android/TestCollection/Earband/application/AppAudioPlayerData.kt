package Android.TestCollection.Earband.application

import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.repository.RealAudioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class AppAudioPlayerData(private val audioRepository: RealAudioRepository, private val applicationScope: CoroutineScope) {

    private val _currentAudio = MutableStateFlow(Audio.emptyAudio)
    val currentAudio: StateFlow<Audio> get() = _currentAudio

    private val _currentAudioPlaylist = MutableStateFlow<List<Audio>>(emptyList())
    val currentAudioPlaylist: StateFlow<List<Audio>> get() = _currentAudioPlaylist

    fun setSelectedAudio(audio: Audio) {
        _currentAudio.value = audio
    }

    fun getSelectedAudio(): Audio {
        return _currentAudio.value
    }

    fun setAudioPlaylist(audios: List<Audio>) {
        _currentAudioPlaylist.value = audios
    }

    fun getAudioPlaylist(): List<Audio> {
        return _currentAudioPlaylist.value
    }

    fun fetchAudiosFromLocal(callback: (List<Audio>) -> Unit, onError: (Throwable) -> Unit) {
        applicationScope.launch {
            try {
                val audios = audioRepository.audios()
                callback(audios)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun setAudioPlaylistFromLocal() {
        fetchAudiosFromLocal(
            callback = { audios ->
                _currentAudioPlaylist.value = audios
            },
            onError = {
            }
        )
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
        return if (_currentAudio.value != Audio.emptyAudio) _currentAudio.value.playlistId else -1
    }
}
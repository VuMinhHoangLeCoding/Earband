package Android.TestCollection.Earband.application

import Android.TestCollection.Earband.model.Audio
import Android.TestCollection.Earband.model.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

class AppPlayerDataModel {

    private var playModeValue: Int = 0

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _selectedAudio = MutableStateFlow(Audio.emptyAudio)
    val selectedAudio: StateFlow<Audio> get() = _selectedAudio

    private val _currentPlaylist = MutableStateFlow(Playlist.emptyPlaylist)
    val currentPlaylist: StateFlow<Playlist> get() = _currentPlaylist

    private val _currentAudios = MutableStateFlow<List<Audio>>(emptyList())
    val currentAudios: StateFlow<List<Audio>> get() = _currentAudios

    fun setSelectedAudio(audio: Audio) {
        _selectedAudio.value = audio
    }

    fun getSelectedAudio(): Audio {
        return _selectedAudio.value
    }

    fun setPlayModeValue(mode: Int) {
        playModeValue = mode
    }

    fun getPlayModeValue(): Int {
        return playModeValue
    }

    fun setAudios(audios: List<Audio>) {
        _currentAudios.value = audios
    }

    fun getAudios(): List<Audio> {
        return _currentAudios.value
    }

    fun getCurrentPlaylist(): Playlist {
        return _currentPlaylist.value
    }

    fun setCurrentPlaylist(playlist: Playlist) {
        _currentPlaylist.value = playlist
    }

    fun getAudioFromPosition(position: Int): Audio {
        val playlistSize = _currentAudios.value.size

        val pos = if (position >= playlistSize) 0
        else if (position < 0) (playlistSize - 1)
        else position

        return _currentAudios.value.getOrNull(pos) ?: Audio.emptyAudio
    }

    fun getRandomPosition(): Int {
        val playlistSize = _currentAudios.value.size
        return Random.nextInt(0, playlistSize + 1)
    }

    fun hasAudio(audio: Audio): Boolean {
        _currentAudios.value.forEach { au ->
            if (audio.id == au.id) return true
        }
        return false
    }

    fun getAudioPosition(audio: Audio): Int {
        return _currentAudios.value.indexOf(audio)
    }

    fun getPlaylistIdFromSelectedAudio(): Long {
        return _selectedAudio.value.playlistId
    }

    fun setIsPlaying(state: Boolean) {
        _isPlaying.value = state
    }
}
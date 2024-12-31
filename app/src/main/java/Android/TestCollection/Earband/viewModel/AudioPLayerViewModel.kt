package Android.TestCollection.Earband.viewModel

import Android.TestCollection.Earband.model.Audio
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AudioPLayerViewModel : ViewModel() {

    private val _selectedAudio = MutableLiveData<Audio>()
    val selectedAudio: LiveData<Audio> = _selectedAudio

    fun setSelectedAudio(audio: Audio) {
        _selectedAudio.value = audio
    }

}